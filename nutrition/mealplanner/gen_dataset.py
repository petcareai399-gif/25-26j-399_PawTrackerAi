import numpy as np
import pandas as pd
from faker import Faker
import random
import math

# Initialize faker and random seed
fake = Faker()
np.random.seed(42)
random.seed(42)

# Define possible values
breeds = [
    "Golden Retriever", "Bulldog", "German Shepherd", "Poodle", "Beagle",
    "Persian Cat", "Siamese Cat", "Maine Coon", "Ragdoll", "British Shorthair"
]
genders = ["Male", "Female"]
activity_levels = ["Low", "Moderate", "High"]
medical_conditions = ["None", "Obesity", "Diabetes", "Allergies", "Joint Issues"]

def generate_pet_data(num_samples=500):
    data = []

    for _ in range(num_samples):
        breed = random.choice(breeds)
        species = "Dog" if "Cat" not in breed else "Cat"
        age = round(random.uniform(0.5, 15), 1)
        gender = random.choice(genders)
        activity = random.choice(activity_levels)
        medical = random.choice(medical_conditions)

        # Generate realistic weight ranges (ensure positive)
        if species == "Dog":
            weight = abs(round(np.random.normal(25, 10), 1))
        else:
            weight = abs(round(np.random.normal(5, 2), 1))
        if weight < 1:  # ensure minimum weight
            weight = 1.0

        # Derived features
        ideal_weight = round(weight * np.random.uniform(0.9, 1.1), 1)

        # Use safe real power for calorie calculation
        base_calories = 70 * (abs(weight) ** 0.75)
        calorie_req = base_calories

        # Adjust calories for activity and medical conditions
        if activity == "Low":
            calorie_req *= 1.2
        elif activity == "Moderate":
            calorie_req *= 1.4
        else:
            calorie_req *= 1.6

        if medical == "Obesity":
            calorie_req *= 0.85
        elif medical == "Diabetes":
            calorie_req *= 0.9

        # Ensure calorie_req is a real float
        calorie_req = float(np.real_if_close(calorie_req))
        calorie_req = round(calorie_req, 1)

        # Nutrient distribution
        protein = round(random.uniform(25, 40), 1)
        fat = round(random.uniform(10, 25), 1)
        carbs = round(max(0, 100 - (protein + fat)), 1)

        # Meal portions
        meal_portion = round(calorie_req / 3.5, 1)  # kcal/g dry food
        meals_per_day = 2 if activity == "Low" else (3 if activity == "Moderate" else 4)
        portion_per_meal = round(meal_portion / meals_per_day, 1)

        # Adaptive learning score
        adaptivity_index = round(random.uniform(0.3, 0.9), 2)

        data.append({
            "Pet_ID": fake.uuid4(),
            "Species": species,
            "Breed": breed,
            "Age_Years": age,
            "Weight_kg": weight,
            "Gender": gender,
            "Activity_Level": activity,
            "Medical_Condition": medical,
            "Ideal_Weight_kg": ideal_weight,
            "Calorie_Requirement_kcal": calorie_req,
            "Protein_%": protein,
            "Fat_%": fat,
            "Carbs_%": carbs,
            "Daily_Meal_Portion_g": meal_portion,
            "Meals_Per_Day": meals_per_day,
            "Portion_Per_Meal_g": portion_per_meal,
            "Adaptivity_Index": adaptivity_index
        })

    return pd.DataFrame(data)

# Generate dataset
synthetic_pet_data = generate_pet_data(num_samples=1000)

# Save to CSV
synthetic_pet_data.to_csv("synthetic_pet_nutrition_dataset.csv", index=False)
print("✅ Synthetic pet nutrition dataset generated and saved as 'synthetic_pet_nutrition_dataset.csv'")
print(synthetic_pet_data.head(10))
