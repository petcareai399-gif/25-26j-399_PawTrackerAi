import tensorflow as tf
import numpy as np
from tensorflow.keras.preprocessing import image
import os

MODEL_PATH = "train/best_pet_disease_model.h5"   # your saved model file
IMAGE_PATH = "test/image.jpg"             # input image for prediction
IMG_SIZE = (224, 224)                     # must match your training size

CLASS_NAMES = [
    "Dental_Disease_in_Cat",
    "Dental_Disease_in_Dog",
    "Distemper_in_Dog",
    "Ear_Mites_in_Cat",
    "Eye_Infection_in_Cat",
    "Eye_Infection_in_Dog",
    "Feline_Leukemia",
    "Feline_Panleukopenia",
    "Fungal_Infection_in_Cat",
    "Fungal_Infection_in_Dog",
    "Hot_Spots_in_Dog",
    "Kennel_Cough_in_Dog",
    "Mange_in_Dog",
    "Parvovirus_in_Dog",
    "Ringworm_in_Cat",
    "Scabies_in_Cat",
    "Skin_Allergy_in_Cat",
    "Skin_Allergy_in_Dog",
    "Tick_Infestation_in_Dog",
    "Urinary_Tract_Infection",
    "Worm_Infection_in_Cat",
    "Worm_Infection_in_Dog"
]

print("Loaded class names:", CLASS_NAMES)

print("Loading model...")
model = tf.keras.models.load_model(MODEL_PATH)
print("Model loaded successfully.")

if not os.path.exists(IMAGE_PATH):
    raise FileNotFoundError(f"Image not found: {IMAGE_PATH}")

img = image.load_img(IMAGE_PATH, target_size=IMG_SIZE)
img_array = image.img_to_array(img)
img_array = img_array / 255.0
img_array = np.expand_dims(img_array, axis=0)

pred = model.predict(img_array)[0]
predicted_index = np.argmax(pred)
confidence = pred[predicted_index] * 100

predicted_class = CLASS_NAMES[predicted_index]

print("\n========================")
print("Prediction Result")
print("========================")
print(f"Predicted Class : {predicted_class}")
print(f"Confidence      : {confidence:.2f}%")
print("========================\n")
