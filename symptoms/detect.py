
import joblib
loaded_pipeline, loaded_le = joblib.load("train\pet_health_symptom_model.pkl")

# Example inference
sample = ["dog vomiting with loss of appetite and fatigue"]
#sample = ["dog has rashes on skin"]  # user symptom text
pred_label_num = loaded_pipeline.predict(sample)[0]
pred_label = loaded_le.inverse_transform([pred_label_num])[0]
print("Predicted condition:", pred_label)
