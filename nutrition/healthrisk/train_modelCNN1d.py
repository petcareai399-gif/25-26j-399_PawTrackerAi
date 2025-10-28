#!pip install numpy pandas scikit-learn tensorflow matplotlib joblib

import numpy as np
import pandas as pd
import joblib
from sklearn.preprocessing import LabelEncoder, StandardScaler
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Conv1D, MaxPooling1D, Dropout, Flatten, Dense
from tensorflow.keras.utils import to_categorical
from tensorflow.keras.callbacks import EarlyStopping
from tensorflow.keras.optimizers import Adam

df = pd.read_csv("synthetic_pet_health_timeseries.csv")

df["Date"] = pd.to_datetime(df["Date"])
df = df.sort_values(by=["Pet_ID", "Date"]).reset_index(drop=True)


le_risk = LabelEncoder()
df["Risk_Label"] = le_risk.fit_transform(df["Risk_Prediction"])

features = ["Current_Weight_kg", "Daily_Feed_g", "Activity_Min_per_Day", "BMI_Index"]
target = "Risk_Label"

scaler = StandardScaler()
df[features] = scaler.fit_transform(df[features])

SEQ_LEN = 7
X, y = [], []

for pet_id, pet_df in df.groupby("Pet_ID"):
    pet_data = pet_df[features].values
    pet_labels = pet_df[target].values
    for i in range(len(pet_data) - SEQ_LEN):
        X.append(pet_data[i:i+SEQ_LEN])
        y.append(pet_labels[i+SEQ_LEN])

X = np.array(X)
y = np.array(y)
y_cat = to_categorical(y)

print(f"Input shape: {X.shape}, Output shape: {y_cat.shape}")

split = int(0.8 * len(X))
X_train, X_test = X[:split], X[split:]
y_train, y_test = y_cat[:split], y_cat[split:]

model = Sequential([
    Conv1D(filters=64, kernel_size=3, activation='relu', padding='same', input_shape=(SEQ_LEN, len(features))),
    MaxPooling1D(pool_size=2),
    Dropout(0.3),

    Conv1D(filters=128, kernel_size=3, activation='relu', padding='same'),
    MaxPooling1D(pool_size=2),
    Dropout(0.3),

    Flatten(),
    Dense(64, activation='relu'),
    Dense(y_cat.shape[1], activation='softmax')
])

model.compile(optimizer=Adam(learning_rate=0.001),
              loss='categorical_crossentropy',
              metrics=['accuracy'])

early_stop = EarlyStopping(monitor='val_loss', patience=3, restore_best_weights=True)

history = model.fit(
    X_train, y_train,
    epochs=20,
    batch_size=32,
    validation_split=0.2,
    callbacks=[early_stop],
    verbose=1
)

loss, acc = model.evaluate(X_test, y_test)
print(f"Test Accuracy: {acc*100:.2f}%")

model.save("cnn_pet_health_model.h5")
joblib.dump(le_risk, "risk_label_encoder.pkl")
joblib.dump(scaler, "feature_scaler.pkl")
print("\nModel, encoders, and test data saved successfully!")
print("Files created:")
print(" - cnn_pet_health_model.h5")
print(" - risk_label_encoder.pkl")
print(" - feature_scaler.pkl")