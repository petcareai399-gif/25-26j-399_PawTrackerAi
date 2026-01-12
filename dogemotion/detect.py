import cv2
from ultralytics import YOLO
import os

model = YOLO("train/emotion_model.pt")

def predict(image_path="test/test.jpg"):
    if not os.path.exists(image_path):
        print(f"Error: '{image_path}' not found.")
        return

    frame = cv2.imread(image_path)
    image_resized = cv2.resize(frame, (640, 480))

    results = model.predict(source=image_resized, save=False)

    for result in results:
        for box in result.boxes:
            x1, y1, x2, y2 = map(int, box.xyxy[0])
            confidence = box.conf[0]
            class_id = int(box.cls[0])
            class_name = model.names[class_id]
            label = f"{class_name} {confidence:.2f}"

            cv2.rectangle(image_resized, (x1, y1), (x2, y2), (0, 255, 0), 2)
            cv2.putText(image_resized, label, (x1, y1 - 10), 
                        cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 0, 255), 2)

    cv2.imshow("Prediction", image_resized)
    cv2.waitKey(0)
    cv2.destroyAllWindows()

predict("test/test.jpg")
