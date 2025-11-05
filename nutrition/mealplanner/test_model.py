import pandas as pd
import joblib

# Load the trained model
pipeline = joblib.load("pet_meal_plan_model.pkl")

# Example pet input
new_pet = pd.DataFrame([{
    "Species": "Dog",
    "Breed": "Beagle",
    "Age_Years": 4.0,
    "Weight_kg": 12.0,
    "Gender": "Male",
    "Activity_Level": "Moderate",
    "Medical_Condition": "None"
}])

# Predict calorie requirement
predicted_calories = pipeline.predict(new_pet)[0]

# Compute meal plan details
daily_portion_g = predicted_calories / 3.5   # kcal per gram of dry food (approx)
meals_per_day = 3
portion_per_meal = daily_portion_g / meals_per_day

# Output the personalized meal plan
print("\nPersonalized Meal Plan:")
print(f"Predicted Calorie Requirement: {predicted_calories:.1f} kcal/day")
print(f"Total Portion: {daily_portion_g:.1f} g/day")
print(f"{meals_per_day} meals of ~{portion_per_meal:.1f} g each")
