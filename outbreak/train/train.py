import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder, StandardScaler
from sklearn.metrics import accuracy_score, f1_score, classification_report
import xgboost as xgb
import joblib

df = pd.read_csv("pet_outbreak_training_dataset_srilanka_100cities.csv")

le_location = LabelEncoder()
df["loc_idx"] = le_location.fit_transform(df["Location"])

le_weather = LabelEncoder()
df["weather_idx"] = le_weather.fit_transform(df["Weather"])

le_risk = LabelEncoder()
df["risk_idx"] = le_risk.fit_transform(df["Risk_Level"])

X = df[["loc_idx", "Search_Volume", "Vet_Reported_Cases", "Humidity_Index", "weather_idx"]]
y = df["risk_idx"]

X_train, X_test, y_train, y_test = train_test_split(
    X, y, stratify=y, test_size=0.2, random_state=42
)

scaler = StandardScaler()
num_cols = ["Search_Volume", "Vet_Reported_Cases", "Humidity_Index"]

X_train_scaled = X_train.copy()
X_test_scaled = X_test.copy()

X_train_scaled[num_cols] = scaler.fit_transform(X_train[num_cols])
X_test_scaled[num_cols] = scaler.transform(X_test[num_cols])

model = xgb.XGBClassifier(
    objective="multi:softprob",
    num_class=len(le_risk.classes_),
    n_estimators=200,
    max_depth=6,
    learning_rate=0.1,
    subsample=0.8,
    colsample_bytree=0.8,
    eval_metric="mlogloss"
)

model.fit(X_train_scaled, y_train)

pred = model.predict(X_test_scaled)
print("Accuracy:", accuracy_score(y_test, pred))
print("F1 (macro):", f1_score(y_test, pred, average="macro"))
print(classification_report(y_test, pred, target_names=le_risk.classes_))

joblib.dump(model, "xgb_outbreak_model.joblib")
joblib.dump(scaler, "xgb_scaler.joblib")
joblib.dump(le_location, "le_location.joblib")
joblib.dump(le_weather, "le_weather.joblib")
joblib.dump(le_risk, "le_risk.joblib")

print("XGBoost model saved.")
