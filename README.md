

---

# 🐾 Paw Tracker AI

### *AI-Powered Telemedicine Platform for Cats & Dogs*

---

## 📌 Project Overview

**Paw Tracker AI** is an **AI-powered pre-veterinary telemedicine platform** designed to help pet owners monitor the **physical and emotional health** of cats and dogs **before visiting a veterinary clinic**.

The platform integrates **multimodal Artificial Intelligence**, **IoT-based real-time health monitoring**, and **predictive analytics** into a single mobile application.
It reduces pet-owner anxiety, supports early health detection, and enables informed decision-making through intelligent insights prior to clinical consultation.

---

## ❗ Problem Statement

Pet owners often face significant challenges in accessing timely veterinary care due to:

* Difficulty transporting pets because of **size, aggression, or anxiety**
* Stress and uncertainty over **minor but concerning behavioral changes**
* Delayed detection of **physical and emotional health issues**
* **Fragmented** pet healthcare applications
* Lack of **real-time monitoring and predictive insights**

Traditional veterinary care is largely **reactive**, leading to late consultations and increased health risks.

---

## 🎯 Project Objectives

### 🎯 Main Objective

To develop an **AI-driven telemedicine platform** that supports **early detection, emotional monitoring, and proactive healthcare** for pets.

### 📍 Specific Objectives

* Provide **AI-based pre-consultation decision support**
* Detect pet emotions using **image-based AI models**
* Analyze symptoms using **text, images, and IoT health data**
* Generate **personalized nutrition and meal plans**
* Predict and alert **viral disease outbreaks**
* Reduce unnecessary or delayed veterinary visits

---

## 🧠 Core Features

### 1️⃣ AI Symptom Checker

* Multimodal input: **Text, Images, IoT data**
* Combines **NLP, CNN, and IoT analytics**
* **Explainable AI (XAI)** for transparency
* Actionable health insights and vet recommendations

### 2️⃣ AI Pet Mood Detection

* Image-based emotion recognition
* Detects:

  * 😊 Happy
  * 😌 Calm
  * 😟 Stressed
  * 😰 Anxious
* Urgency-based recommendations

### 3️⃣ Personalized Nutrition & Meal Planning

* Uses **Random Forest Regressor**
* Feeding plans based on pet profile
* Detects **obesity and malnutrition risks**

### 4️⃣ AI Viral Outbreak Detection & Alerts

* Powered by **XGBoost**
* Location-based disease alerts
* Preventive guidance for pet owners and vets

### 5️⃣ IoT Integration

* Smart collar integration
* Monitors:

  * ❤️ Heart rate
  * 🌡️ Temperature
  * 🩸 SpO₂
* Real-time Firebase data streaming

### 6️⃣ Unified Dashboard

* All modules in one interface
* Clean and intuitive UI
* Real-time alerts and analytics

---

## 🏗️ System Architecture

```
Mobile App  →  Firebase  →  AI Models  →  Health Insights
IoT Devices →  Firebase  →  AI Analytics → Alerts
```

* Cloud-based
* Scalable
* Real-time data processing

---

## 🧰 Technical Stack

### 📱 Frontend

* Android (Java)
* Material Design

### ☁️ Backend

* Firebase Authentication
* Firebase Realtime Database
* Firebase Storage
* REST APIs

### 🤖 AI / Machine Learning

* CNN – Image-based diagnosis & mood detection
* NLP – Text symptom analysis
* Random Forest Regressor – Nutrition planning
* XGBoost – Outbreak detection

### 🔌 IoT

* ESP32 / ESP8266
* MAX30105, DS18B20 sensors

---

## 📦 Project Dependencies

This section lists all dependencies used across the **Android App**, **Python AI models**, and **IoT collar system**.

---

### 📱 Android Application

#### 🔧 Gradle Configuration

* **Android Gradle Plugin**: `8.11.2`
* **Gradle Version**: Defined in `gradle-wrapper.properties`

#### 📚 Core Android Libraries

* AndroidX AppCompat – `1.7.1`
* Material Design – `1.13.0`
* Activity – `1.11.0`
* ConstraintLayout – `2.2.1`
* RecyclerView – `1.3.2`
* CardView – `1.0.0`
* Core Splash Screen – `1.0.1`

#### 🔥 Firebase Services

* Firebase BOM – `34.4.0`

  * Authentication
  * Realtime Database
  * Storage
* Google Services Plugin – `4.4.4`

#### 🎨 UI Components

* CircleImageView – `3.1.0`
* MPAndroidChart – `3.1.0`
* Flexbox Layout – `3.0.0`
* Lottie Animations – `6.1.0`

#### 🛠️ Utility Libraries

* SSP – `1.1.0`
* SDP – `1.1.1`

#### 🧪 Testing Libraries

* JUnit – `4.13.2`
* AndroidX Test JUnit – `1.3.0`
* Espresso Core – `3.7.0`

#### 📱 Android Requirements

* Minimum SDK: `24`
* Target SDK: `36`
* Compile SDK: `36`
* Java Version: `11`

---

### 🐍 Python ML / AI Components

#### 🧠 Computer Vision Model (`diseaseCV/`)

```bash
pip install tensorflow numpy matplotlib scikit-learn pillow
```

**Dependencies**

* tensorflow ≥ 2.0.0
* numpy ≥ 1.19.0
* matplotlib ≥ 3.3.0
* scikit-learn ≥ 0.24.0
* Pillow ≥ 8.0.0

---

#### 🩺 Symptom Detection Model (`symptoms/`)

```bash
pip install pandas scikit-learn joblib numpy
```

**Dependencies**

* pandas ≥ 1.2.0
* scikit-learn ≥ 0.24.0
* joblib ≥ 1.0.0
* numpy ≥ 1.19.0

---

#### 📦 Combined Installation

```bash
pip install tensorflow numpy matplotlib scikit-learn pandas joblib pillow
```

---

### 🔌 Arduino / IoT Component

#### 📚 Required Arduino Libraries

* Wire (Built-in)
* MAX30105
* heartRate.h
* OneWire (Paul Stoffregen)
* DallasTemperature (Miles Burton)
* Firebase ESP Client (Mobizt)
* WiFi (ESP32 / ESP8266)
* Firebase helpers (`TokenHelper.h`, `RTDBHelper.h`)

#### 🔧 Hardware Requirements

* ESP32 or ESP8266
* MAX30105 (Heart rate & SpO₂)
* DS18B20 (Temperature sensor)

---

## ⚙️ Quick Installation Guide

## 📁 Repository
- **GitHub**: https://github.com/petcareai399-gif/25-26j-399_PawTrackerAi.git
- **Clone**: `git clone https://github.com/petcareai399-gif/25-26j-399_PawTrackerAi.git`

### 📱 Android App

```bash
cd App
./gradlew build
```

### 🐍 Python AI

```bash
python -m venv venv
source venv/bin/activate
pip install tensorflow numpy matplotlib scikit-learn pandas joblib pillow
```

### 🔌 Arduino / IoT

1. Open Arduino IDE
2. Install required libraries
3. Select ESP32 / ESP8266 board
4. Upload `IOT/collar/collar.ino`

---

## 📈 Project Progress

* ✅ AI models trained and validated
* ✅ Functional Android UI
* ✅ Backend services operational
* ✅ IoT data flow tested

**Overall Progress:** ~50%

---

## ⚠️ Limitations

* Requires stable internet connectivity
* Privacy and consent constraints
* Hardware limitations

---

## 🚀 Future Enhancements

* Full smart-collar deployment
* Predictive health scoring
* Long-term health analytics
* Offline support
* Veterinarian dashboards
* Enhanced Explainable AI (XAI)

---

## 💰 Commercialization & Sustainability

* Freemium mobile application
* Subscription-based AI services
* IoT hardware partnerships
* In-app premium health modules

---

## 🤝 Contributing

1. Create a feature branch
2. Commit changes
3. Test thoroughly
4. Submit a Pull Request

---

## 🙏 Acknowledgments

* AI & IoT research community
* Veterinary professionals
* Open-source libraries and tools

---


