import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder
import joblib

df = pd.read_csv("pet_health_symptoms_dataset.csv")  # adjust path/filename as needed

print(df.columns)
print(df.head())

input_col = "text"
target_col = "condition"

df = df.dropna(subset=[input_col, target_col])

le = LabelEncoder()
df["label"] = le.fit_transform(df[target_col])

X = df[input_col].values
y = df["label"].values

X_train, X_test, y_train, y_test = train_test_split(
    X, y, test_size=0.2, random_state=42, stratify=y
)

from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.pipeline import Pipeline
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import classification_report, accuracy_score

pipeline = Pipeline([
    ("tfidf", TfidfVectorizer(
        lowercase=True,
        stop_words="english",
        ngram_range=(1,2),
        max_features=10000
    )),
    ("clf", LogisticRegression(max_iter=1000))
])

pipeline.fit(X_train, y_train)

y_pred = pipeline.predict(X_test)

print("Accuracy:", accuracy_score(y_test, y_pred))
print(classification_report(y_test, y_pred, target_names=le.classes_))



joblib.dump((pipeline, le), "pet_health_symptom_model.pkl")
