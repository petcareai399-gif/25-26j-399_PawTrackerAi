import numpy as np
import joblib
from tensorflow.keras.models import load_model

model = load_model("cnn_pet_health_model.h5")
le_risk = joblib.load("risk_label_encoder.pkl")
scaler = joblib.load("feature_scaler.pkl")

sample_data = np.array([
    [12.0,  320.0,  45.0,  22.3],
    [12.1,  330.0,  47.0,  22.5],
    [12.3,  340.0,  42.0,  22.8],
    [12.4,  350.0,  40.0,  23.0],
    [12.5,  360.0,  38.0,  23.2],
    [12.6,  365.0,  35.0,  23.4],
    [12.8,  370.0,  33.0,  23.6]
])

features = ["Current_Weight_kg", "Daily_Feed_g", "Activity_Min_per_Day", "BMI_Index"]

sample_scaled = scaler.transform(sample_data)

sample_input = np.expand_dims(sample_scaled, axis=0)
print(f"Input shape for model: {sample_input.shape}")

pred_probs = model.predict(sample_input)
pred_label = np.argmax(pred_probs)
pred_risk = le_risk.inverse_transform([pred_label])[0]

risk_to_suggestion = {
    "Healthy": "Maintain current routine.",
    "Obesity Risk": "Reduce fat content and increase daily exercise.",
    "Underweight Risk": "Increase calorie intake and monitor nutrient balance."
}
pred_suggestion = risk_to_suggestion.get(pred_risk, "🔍 Monitor pet health closely.")

print("\n--- Prediction Result ---")
print(f"Predicted Risk: {pred_risk}")
print(f"Suggested Action: {pred_suggestion}")
print(f"Model confidence: {pred_probs[0][pred_label]*100:.2f}%")
