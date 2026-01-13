import joblib
import numpy as np
import pandas as pd

model = joblib.load("train/xgb_outbreak_model.joblib")
scaler = joblib.load("train/xgb_scaler.joblib")
le_location = joblib.load("train/le_location.joblib")
le_weather = joblib.load("train/le_weather.joblib")
le_risk = joblib.load("train/le_risk.joblib")


def predict_outbreak(location, search_volume, vet_reports, weather, humidity_index):
    """
    location        : str   (ex: 'Colombo')
    search_volume   : int   (ex: 22)
    vet_reports     : int   (ex: 4)
    weather         : str   (ex: 'humid')
    humidity_index  : int   (ex: 10)
    """

    loc_idx = le_location.transform([location])[0]
    weather_idx = le_weather.transform([weather])[0]

    df = pd.DataFrame([{
        "loc_idx": loc_idx,
        "Search_Volume": search_volume,
        "Vet_Reported_Cases": vet_reports,
        "Humidity_Index": humidity_index,
        "weather_idx": weather_idx
    }])

    num_cols = ["Search_Volume", "Vet_Reported_Cases", "Humidity_Index"]
    df[num_cols] = scaler.transform(df[num_cols])

    prob = model.predict_proba(df)[0]

    class_index = np.argmax(prob)
    risk_label = le_risk.inverse_transform([class_index])[0]
    confidence = prob[class_index]

    return {
        "risk_level": risk_label,
        "confidence": float(confidence),
        "probabilities": {
            label: float(p) for label, p in zip(le_risk.classes_, prob)
        }
    }


if __name__ == "__main__":
    result = predict_outbreak(
        location="Embilipitiya",
        search_volume=30,
        vet_reports=6,
        weather="humid",
        humidity_index=10
    )

    print("\n--- Real-time prediction ---")
    print("Predicted Risk:", result["risk_level"])
    print("Confidence:", result["confidence"])
    print("Probabilities:", result["probabilities"])
