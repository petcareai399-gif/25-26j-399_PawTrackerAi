
#include <Arduino.h>
#include <Wire.h>
#include <MAX30105.h>
#include "heartRate.h"

#include <OneWire.h>
#include <DallasTemperature.h>

#if defined(ESP32)
  #include <WiFi.h>
#elif defined(ESP8266)
  #include <ESP8266WiFi.h>
#endif

#include <Firebase_ESP_Client.h>

#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"

#define WIFI_SSID      "wifi"
#define WIFI_PASSWORD  "00000000"

#define API_KEY        "AIzaSyCeumt2R64GoJbGtzYd7j94oC5hXveFxO4"
#define DATABASE_URL   "https://pawtracker-496fa-default-rtdb.firebaseio.com/"

FirebaseData fbdo;

FirebaseAuth auth;
FirebaseConfig config;

unsigned long sendDataPrevMillis = 0;
bool signupOK = false;

MAX30105 particleSensor;

const byte RATE_SIZE = 4;
byte rates[RATE_SIZE];
byte rateSpot = 0;
long lastBeat = 0;

float beatsPerMinute;
int beatAvg;

#define DS18B20_PIN 4  // GPIO4 

OneWire oneWire(DS18B20_PIN);
DallasTemperature sensors(&oneWire);

volatile int globalBPM = 0;
volatile float globalTemp = 0.0;


int readHeartRate()
{
  long irValue = particleSensor.getIR();

  if (checkForBeat(irValue))
  {
    long delta = millis() - lastBeat;
    lastBeat = millis();

    beatsPerMinute = 60 / (delta / 1000.0);

    if (beatsPerMinute < 255 && beatsPerMinute > 20)
    {
      rates[rateSpot++] = (byte)beatsPerMinute;
      rateSpot %= RATE_SIZE;

      beatAvg = 0;
      for (byte x = 0; x < RATE_SIZE; x++)
        beatAvg += rates[x];
      beatAvg /= RATE_SIZE;
    }
  }

  return beatAvg;
}


float readTemperature()
{
  sensors.requestTemperatures();
  float tempC = sensors.getTempCByIndex(0);

  if (tempC == DEVICE_DISCONNECTED_C)
    return -1000;

  return tempC;
}

void heartRateTask(void *pvParameters)
{
  while (1)
  {
    globalBPM = readHeartRate();
    vTaskDelay(10 / portTICK_PERIOD_MS);
  }
}

void temperatureTask(void *pvParameters)
{
  while (1)
  {
    globalTemp = readTemperature();
    vTaskDelay(1000 / portTICK_PERIOD_MS);
  }
}


void setup()
{
  Serial.begin(115200);

  // -------- MAX30105 INIT ---------
  if (!particleSensor.begin(Wire, I2C_SPEED_FAST))
  {
    Serial.println("MAX30105 not found!");
    while (1);
  }

  particleSensor.setup();
  particleSensor.setPulseAmplitudeRed(0x0A);
  particleSensor.setPulseAmplitudeGreen(0);

  sensors.begin();

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to WiFi");

  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(300);
  }

  Serial.println();
  Serial.print("Connected! IP: ");
  Serial.println(WiFi.localIP());

  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;

  if (Firebase.signUp(&config, &auth, "", ""))
  {
    Serial.println("Firebase signup OK");
    signupOK = true;
  }
  else {
    Serial.printf("Signup failed: %s\n",
                  config.signer.signupError.message.c_str());
  }

  config.token_status_callback = tokenStatusCallback;

  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);


  xTaskCreate(heartRateTask, "HeartRateTask", 4096, NULL, 1, NULL);
  xTaskCreate(temperatureTask, "TemperatureTask", 4096, NULL, 1, NULL);

  Serial.println("Threads started!");
}



void loop()
{
  Serial.print("BPM = ");
  Serial.print(globalBPM);

  Serial.print(" | Temp = ");
  Serial.print(globalTemp);
  Serial.println(" °C");

  if (Firebase.ready() && signupOK &&
      (millis() - sendDataPrevMillis > 15000 || sendDataPrevMillis == 0))
  {
    sendDataPrevMillis = millis();

    // Upload heart rate
    if (!Firebase.RTDB.setInt(&fbdo, "/bpm", globalBPM))
      Serial.println("BPM Upload Failed: " + fbdo.errorReason());

    // Upload temperature
    if (!Firebase.RTDB.setFloat(&fbdo, "/temp", globalTemp))
      Serial.println("Temp Upload Failed: " + fbdo.errorReason());

    Serial.println("Firebase Updated Successfully!");
  }

  delay(500);
}
