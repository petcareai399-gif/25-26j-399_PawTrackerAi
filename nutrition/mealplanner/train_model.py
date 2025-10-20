import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import OneHotEncoder, StandardScaler
from sklearn.compose import ColumnTransformer
from sklearn.pipeline import Pipeline
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import mean_absolute_error, r2_score
import joblib

df = pd.read_csv("synthetic_pet_nutrition_dataset.csv")

X = df[["Species", "Breed", "Age_Years", "Weight_kg", "Gender",
        "Activity_Level", "Medical_Condition"]]
y = df["Calorie_Requirement_kcal"]


categorical_features = ["Species", "Breed", "Gender", "Activity_Level", "Medical_Condition"]
numeric_features = ["Age_Years", "Weight_kg"]

preprocessor = ColumnTransformer(
    transformers=[
        ("num", StandardScaler(), numeric_features),
        ("cat", OneHotEncoder(handle_unknown="ignore"), categorical_features)
    ]
)

model = RandomForestRegressor(
    n_estimators=200,
    max_depth=12,
    random_state=42
)

pipeline = Pipeline(steps=[
    ("preprocessor", preprocessor),
    ("model", model)
])

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

pipeline.fit(X_train, y_train)

y_pred = pipeline.predict(X_test)

mae = mean_absolute_error(y_test, y_pred)
r2 = r2_score(y_test, y_pred)

print(f"Model trained successfully!")
print(f"Mean Absolute Error: {mae:.2f}")
print(f"R² Score: {r2:.3f}")


joblib.dump(pipeline, "pet_meal_plan_model.pkl")
print("Model saved as 'pet_meal_plan_model.pkl'")
