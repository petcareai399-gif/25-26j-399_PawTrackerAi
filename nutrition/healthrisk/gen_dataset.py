import numpy as np
import pandas as pd
import random
from datetime import datetime, timedelta

NUM_PETS = 200        # number of unique pets
DAYS = 60             # days of data per pet
np.random.seed(42)
random.seed(42)

species_list = ["Dog", "Cat"]
breeds = {
    "Dog": ["Labrador", "Beagle", "Bulldog", "German Shepherd", "Poodle"],
    "Cat": ["Siamese", "Persian", "Maine Coon", "Bengal", "Sphynx"]
}
activity_levels = ["Low", "Moderate", "High"]

def generate_pet_profile(pet_id):
    species = random.choice(species_list)
    breed = random.choice(breeds[species])
    gender = random.choice(["Male", "Female"])
    age = round(np.random.uniform(0.5, 14.0), 1)  # years

    # base weight
    weight_range = {"Dog": (5, 40), "Cat": (2.5, 8)}
    base_weight = np.random.uniform(*weight_range[species])

    # average feeding (grams/day)
    avg_feed = base_weight * np.random.uniform(25, 35)
    base_activity = random.choice(activity_levels)

    return {
        "Pet_ID": pet_id,
        "Species": species,
        "Breed": breed,
        "Gender": gender,
        "Age_Years": age,
        "Base_Weight": base_weight,
        "Base_Feed_g": avg_feed,
        "Base_Activity_Level": base_activity
    }

def simulate_daily_data(pet_profile, days):
    records = []
    start_date = datetime.today() - timedelta(days=days)

    # simulate realistic trend patterns
    weight = pet_profile["Base_Weight"]
    for d in range(days):
        date = start_date + timedelta(days=d)
        activity_level = pet_profile["Base_Activity_Level"]

        # Map activity level to minutes
        if activity_level == "Low":
            activity_min = np.random.randint(20, 45)
        elif activity_level == "Moderate":
            activity_min = np.random.randint(45, 90)
        else:
            activity_min = np.random.randint(90, 150)

        # simulate feed intake with some randomness
        feed_today = np.random.normal(pet_profile["Base_Feed_g"], pet_profile["Base_Feed_g"] * 0.1)

        # simulate weight changes over time based on feed vs. activity
        weight_change_factor = (feed_today / pet_profile["Base_Feed_g"]) - (activity_min / 90) * 0.05
        weight += weight_change_factor * np.random.uniform(-0.1, 0.2)
        weight = max(0.8 * pet_profile["Base_Weight"], min(1.3 * pet_profile["Base_Weight"], weight))  # clamp realistic

        bmi_index = round(weight / (pet_profile["Age_Years"] + 0.5), 2)

        # determine AI health risk
        if weight > pet_profile["Base_Weight"] * 1.2:
            risk_flag = "Obesity Risk"
            suggestion = "Reduce fat content, increase exercise"
        elif weight < pet_profile["Base_Weight"] * 0.8:
            risk_flag = "Underweight Risk"
            suggestion = "Increase calorie intake, check nutrient balance"
        else:
            risk_flag = "Healthy"
            suggestion = "Maintain current routine"

        confidence = round(np.random.uniform(0.75, 0.99), 2)

        if d > 30:
            # compute 30-day trend
            if random.random() < 0.05 and risk_flag != "Healthy":
                early_warning = "Yes"
            else:
                early_warning = "No"
        else:
            early_warning = "No"

        # Vet override occasionally
        vet_override = random.choices(["Yes", "No"], weights=[0.05, 0.95])[0]

        records.append({
            "Date": date.strftime("%Y-%m-%d"),
            "Pet_ID": pet_profile["Pet_ID"],
            "Species": pet_profile["Species"],
            "Breed": pet_profile["Breed"],
            "Gender": pet_profile["Gender"],
            "Age_Years": pet_profile["Age_Years"],
            "Current_Weight_kg": round(weight, 2),
            "Daily_Feed_g": round(feed_today, 1),
            "Activity_Min_per_Day": activity_min,
            "BMI_Index": bmi_index,
            "Risk_Prediction": risk_flag,
            "AI_Confidence": confidence,
            "Suggested_Action": suggestion,
            "Early_Warning": early_warning,
            "Vet_Override": vet_override
        })
    return records

all_records = []
for pet_id in range(1, NUM_PETS + 1):
    profile = generate_pet_profile(pet_id)
    pet_records = simulate_daily_data(profile, DAYS)
    all_records.extend(pet_records)

df = pd.DataFrame(all_records)

df.to_csv("synthetic_pet_health_timeseries.csv", index=False)

print("Synthetic time-series dataset generated successfully!")
print(f"Total records: {len(df)} ({NUM_PETS} pets × {DAYS} days)")
print(df.head(10))
