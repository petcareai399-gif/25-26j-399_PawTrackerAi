import pandas as pd
import numpy as np
import random

locations = [
    "Colombo", "Kandy", "Galle", "Jaffna", "Negombo", "Anuradhapura", "Trincomalee", 
    "Batticaloa", "Kurunegala", "Ratnapura", "Badulla", "Matara", "Gampaha", "Kalutara",
    "Puttalam", "Polonnaruwa", "Hambantota", "Monaragala", "Mannar", "Vavuniya",
    "Mullaitivu", "Kilinochchi", "Nuwara Eliya", "Matale", "Kegalle", "Ampara",
    "Bandarawela", "Hatton", "Point Pedro", "Chilaw", "Balangoda", "Weligama",
    "Avissawella", "Embilipitiya", "Kadawatha", "Homagama", "Moratuwa", "Dehiwala",
    "Panadura", "Wattala", "Ja-Ela", "Kelaniya", "Kotte", "Maharagama", "Boralesgamuwa",
    "Piliyandala", "Galgamuwa", "Dambulla", "Haputale", "Kataragama", "Tissamaharama",
    "Horana", "Beruwala", "Ambalangoda", "Aluthgama", "Ginigathhena", "Warakapola",
    "Kuliyapitiya", "Minuwangoda", "Seeduwa", "Katunayake", "Kandana", "Ragama",
    "Moragahamula", "Weeraketiya", "Deniyaya", "Elpitiya", "Mirissa", "Hikkaduwa",
    "Gonapola", "Kosgama", "Hanwella", "Kahawatte", "Rathmalana", "Bandaragama",
    "Middeniya", "Mapalana", "Mawanella", "Galewela", "Wariyapola", "Ganemulla",
    "Pelmadulla", "Kalawana", "Giriulla", "Narammala", "Marawila", "Dankotuwa",
    "Matugama", "Agunukolapelessa", "Buttala", "Wellawaya", "Passara", "Mahiyanganaya",
    "Kothmale", "Maskeliya"
]

print("Total cities:", len(locations))

np.random.seed(42)

search_terms = [
    "dog coughing", "dog fever", "dog vomiting", "itchy skin", 
    "loss of appetite", "lethargy", "eye infection", 
    "kennel cough", "fleas", "ticks"
]

weather_conditions = ["humid", "dry", "rainy", "hot"]

records = 5000  # dataset size

def compute_risk(search_volume, vet_reports, humidity):
    score = search_volume * 0.5 + vet_reports * 0.7 + humidity * 0.4

    if score > 16:
        return "High"
    elif score > 8:
        return "Medium"
    else:
        return "Low"

data = []

for _ in range(records):
    location = random.choice(locations)
    term = random.choice(search_terms)
    
    search_volume = np.random.poisson(lam=random.randint(1, 20))
    vet_reports = np.random.poisson(lam=random.randint(0, 10))
    
    weather = random.choice(weather_conditions)
    
    humidity = {
        "humid": 10,
        "rainy": 6,
        "hot": 4,
        "dry": 2
    }[weather]

    risk = compute_risk(search_volume, vet_reports, humidity)

    data.append([           //adding new record
        location,
        term,
        search_volume,
        vet_reports,
        weather,
        humidity,
        risk
    ])

df = pd.DataFrame(data, columns=[               //create structured table
    "Location",
    "Search_Term",
    "Search_Volume",
    "Vet_Reported_Cases",
    "Weather",
    "Humidity_Index",    "Risk_Level"
])

df.to_csv("pet_outbreak_training_dataset_srilanka_100cities.csv", index=False)

print("Dataset generated: pet_outbreak_training_dataset_srilanka_100cities.csv")
print(df.head())
