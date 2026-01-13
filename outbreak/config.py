"""
Configuration constants and helper utilities for the viral outbreak
detection pipeline.

These values are intentionally lightweight and are expected to be
overridden by environment-specific configuration in production.
"""

from pathlib import Path


PROJECT_ROOT = Path(__file__).resolve().parent

# Default paths for datasets and model artefacts.
DATA_DIR = PROJECT_ROOT / "train"
RAW_DATASET_PATH = DATA_DIR / "pet_outbreak_training_dataset_srilanka_100cities.csv"

MODEL_PATH = DATA_DIR / "xgb_outbreak_model.joblib"
SCALER_PATH = DATA_DIR / "xgb_scaler.joblib"

LOCATION_ENCODER_PATH = DATA_DIR / "le_location.joblib"
WEATHER_ENCODER_PATH = DATA_DIR / "le_weather.joblib"
RISK_ENCODER_PATH = DATA_DIR / "le_risk.joblib"


