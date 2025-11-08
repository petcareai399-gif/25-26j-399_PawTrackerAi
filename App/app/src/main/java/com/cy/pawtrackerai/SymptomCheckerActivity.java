package com.cy.pawtrackerai;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SymptomCheckerActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 101;
    private static final int CAMERA_PERMISSION_CODE = 102;

    private PawLoadingDialog loadingDialog;

    // UI Components
    private ImageView btnBack, btnRefresh, btnCamera, btnGallery;
    private EditText etSymptomDescription;
    private CardView btnAnalyze;
    private LinearLayout layoutImageNotes;

    // Heart Rate
    private TextView tvHeartRate, tvHeartRateStatus, tvHeartRateRange;
    private ProgressBar progressHeartRate;

    // Temperature
    private TextView tvTemperature, tvTempStatus, tvTempRange;
    private ProgressBar progressTemperature;

    // Blood Oxygen
    private TextView tvOxygen, tvOxygenStatus, tvOxygenRange;
    private ProgressBar progressOxygen;

    // Symptom Tags
    private TextView tagVomiting, tagDiarrhea, tagLethargy, tagLossAppetite;
    private TextView tagCoughing, tagScratching, tagLimping, tagFever;
    private List<TextView> symptomTags;
    private List<String> selectedSymptoms;

    // Firebase
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private String userId;

    // Health data values
    private int bpm = 0;
    private double temp = 0.0;
    private int bo = 0;

    // Image data
    private List<String> imageBase64List;
    private int imageCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_checker);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        // Initialize loading dialog FIRST
        loadingDialog = new PawLoadingDialog(this);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize Lists
        symptomTags = new ArrayList<>();
        selectedSymptoms = new ArrayList<>();
        imageBase64List = new ArrayList<>();

        // Initialize Views
        initViews();

        // Set up listeners
        setupListeners();

        // Load data from Firebase
        loadHealthData();
    }

    private void initViews() {
        // Back button
        btnBack = findViewById(R.id.btnBack);
        btnRefresh = findViewById(R.id.btnRefresh);

        // Heart Rate
        tvHeartRate = findViewById(R.id.tvHeartRate);
        tvHeartRateStatus = findViewById(R.id.tvHeartRateStatus);
        tvHeartRateRange = findViewById(R.id.tvHeartRateRange);
        progressHeartRate = findViewById(R.id.progressHeartRate);

        // Temperature
        tvTemperature = findViewById(R.id.tvTemperature);
        tvTempStatus = findViewById(R.id.tvTempStatus);
        tvTempRange = findViewById(R.id.tvTempRange);
        progressTemperature = findViewById(R.id.progressTemperature);

        // Blood Oxygen
        tvOxygen = findViewById(R.id.tvOxygen);
        tvOxygenStatus = findViewById(R.id.tvOxygenStatus);
        tvOxygenRange = findViewById(R.id.tvOxygenRange);
        progressOxygen = findViewById(R.id.progressOxygen);

        // Upload Section
        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);
        layoutImageNotes = findViewById(R.id.layoutImageNotes);

        // Symptom Description
        etSymptomDescription = findViewById(R.id.etSymptomDescription);

        // Symptom Tags
        tagVomiting = findViewById(R.id.tagVomiting);
        tagDiarrhea = findViewById(R.id.tagDiarrhea);
        tagLethargy = findViewById(R.id.tagLethargy);
        tagLossAppetite = findViewById(R.id.tagLossAppetite);
        tagCoughing = findViewById(R.id.tagCoughing);
        tagScratching = findViewById(R.id.tagScratching);
        tagLimping = findViewById(R.id.tagLimping);
        tagFever = findViewById(R.id.tagFever);

        symptomTags.add(tagVomiting);
        symptomTags.add(tagDiarrhea);
        symptomTags.add(tagLethargy);
        symptomTags.add(tagLossAppetite);
        symptomTags.add(tagCoughing);
        symptomTags.add(tagScratching);
        symptomTags.add(tagLimping);
        symptomTags.add(tagFever);

        // Analyze Button
        btnAnalyze = findViewById(R.id.btnAnalyze);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnRefresh.setOnClickListener(v -> {
            Toast.makeText(this, "Refreshing data...", Toast.LENGTH_SHORT).show();
            loadHealthData();
        });
// Camera Button
        btnCamera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            } else {
                openCamera();
            }
        });

        // Gallery Button
        btnGallery.setOnClickListener(v -> openGallery());

        // Symptom Tags
        for (TextView tag : symptomTags) {
            tag.setOnClickListener(v -> toggleSymptomTag(tag));
        }

        // Analyze Button
        btnAnalyze.setOnClickListener(v -> test());
//        btnAnalyze.setOnClickListener(v -> analyzeSymptoms());
    }

    private void test(){
        startActivity(new Intent(SymptomCheckerActivity.this, AiAnalysisResultActivity.class));
    }
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE && data != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                if (imageBitmap != null) {
                    String base64Image = bitmapToBase64(imageBitmap);
                    imageBase64List.add(base64Image);
                    imageCounter++;
                    addImageNote("Camera image captured", imageCounter);
                }
            } else if (requestCode == GALLERY_REQUEST_CODE && data != null) {
                if (data.getClipData() != null) {
                    // Multiple images
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                    getContentResolver(), imageUri);
                            String base64Image = bitmapToBase64(bitmap);
                            imageBase64List.add(base64Image);
                            imageCounter++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    addImageNote(count + " images from gallery", imageCounter);
                } else if (data.getData() != null) {
                    // Single image
                    Uri imageUri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), imageUri);
                        String base64Image = bitmapToBase64(bitmap);
                        imageBase64List.add(base64Image);
                        imageCounter++;
                        addImageNote("Gallery image attached", imageCounter);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void addImageNote(String message, final int imageId) {
        layoutImageNotes.setVisibility(View.VISIBLE);

        LinearLayout noteLayout = new LinearLayout(this);
        noteLayout.setOrientation(LinearLayout.HORIZONTAL);
        noteLayout.setPadding(0, 8, 0, 8);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        noteLayout.setLayoutParams(params);

        TextView tvNote = new TextView(this);
        tvNote.setText("✓ " + message);
        tvNote.setTextColor(0xFF4CAF50);
        tvNote.setTextSize(13);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        );
        tvNote.setLayoutParams(textParams);

        TextView btnClear = new TextView(this);
        btnClear.setText("Clear");
        btnClear.setTextColor(0xFFFF5252);
        btnClear.setTextSize(12);
        btnClear.setPadding(16, 0, 0, 0);
        btnClear.setClickable(true);
        btnClear.setFocusable(true);

        final int notePosition = layoutImageNotes.getChildCount();
        btnClear.setOnClickListener(v -> {
            layoutImageNotes.removeViewAt(notePosition);
            if (notePosition < imageBase64List.size()) {
                imageBase64List.remove(notePosition);
            }
            if (layoutImageNotes.getChildCount() == 0) {
                layoutImageNotes.setVisibility(View.GONE);
            }
        });

        noteLayout.addView(tvNote);
        noteLayout.addView(btnClear);
        layoutImageNotes.addView(noteLayout);
    }

    private void toggleSymptomTag(TextView tag) {
        tag.setSelected(!tag.isSelected());
        String symptom = tag.getText().toString();

        if (tag.isSelected()) {
            if (!selectedSymptoms.contains(symptom)) {
                selectedSymptoms.add(symptom);
            }
        } else {
            selectedSymptoms.remove(symptom);
        }
    }

    private void analyzeSymptoms() {
        String description = etSymptomDescription.getText().toString().trim();

        if (description.isEmpty() && selectedSymptoms.isEmpty()) {
            Toast.makeText(this, "Please describe symptoms or select tags",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        loadingDialog.show("Uploading data...");

// After Firebase upload starts
        new Handler().postDelayed(() -> {
            loadingDialog.setMessage("Analyzing with AI...");
        }, 1000);

        // Generate history ID
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
                .format(new Date());
        String historyId = "history" + timestamp;

        // Prepare data
        Map<String, Object> symptomData = new HashMap<>();
        symptomData.put("bpm", bpm);
        symptomData.put("temperature", temp);
        symptomData.put("bloodOxygen", bo);
        symptomData.put("description", description);
        symptomData.put("selectedSymptoms", selectedSymptoms);
        symptomData.put("images", imageBase64List);
        symptomData.put("timestamp", System.currentTimeMillis());

        // Save to Firebase under users/userId/symptomHistory/historyId
        databaseReference.child("users")
                .child(userId)
                .child("symptomHistory")
                .child(historyId)
                .setValue(symptomData)
                .addOnSuccessListener(aVoid -> {

                    loadingDialog.dismiss();
                    Toast.makeText(this, "Analysis submitted successfully!",
                            Toast.LENGTH_SHORT).show();
                    // You can navigate to results page here
                    // Or show a dialog with AI analysis
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to submit: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void loadHealthData() {
        // Listen for real-time updates from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get BPM (Heart Rate)
                if (snapshot.hasChild("bpm")) {
                    bpm = snapshot.child("bpm").getValue(Integer.class);
                    updateHeartRateUI(bpm);
                }

                // Get Temperature
                if (snapshot.hasChild("temp")) {
                    Object tempValue = snapshot.child("temp").getValue();
                    if (tempValue instanceof Long) {
                        temp = ((Long) tempValue).doubleValue();
                    } else if (tempValue instanceof Double) {
                        temp = (Double) tempValue;
                    }
                    updateTemperatureUI(temp);
                }

                // Get Blood Oxygen
                if (snapshot.hasChild("bo")) {
                    bo = snapshot.child("bo").getValue(Integer.class);
                    updateBloodOxygenUI(bo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SymptomCheckerActivity.this,
                        "Failed to load data: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateHeartRateUI(int bpm) {
        tvHeartRate.setText(String.valueOf(bpm));

        // Normal range: 60-100 BPM
        if (bpm >= 60 && bpm <= 100) {
            // NORMAL
            tvHeartRateStatus.setText("NORMAL");
            tvHeartRateStatus.setTextColor(ContextCompat.getColor(this, R.color.green_primary));
            tvHeartRateStatus.setBackground(ContextCompat.getDrawable(this, R.drawable.card_rounded));
            tvHeartRateStatus.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green_shade)));
            tvHeartRate.setTextColor(ContextCompat.getColor(this, R.color.green_primary));
            progressHeartRate.setProgressTintList(
                    android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green_primary)));
            tvHeartRateRange.setText("✓ Normal");
            tvHeartRateRange.setTextColor(ContextCompat.getColor(this, R.color.green_primary));

        } else if (bpm > 100 && bpm <= 120) {
            // WARNING
            tvHeartRateStatus.setText("WARNING");
            tvHeartRateStatus.setTextColor(ContextCompat.getColor(this, R.color.orange_primary));
            tvHeartRateStatus.setBackground(ContextCompat.getDrawable(this, R.drawable.card_rounded));
            tvHeartRateStatus.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orange_shade)));
            tvHeartRate.setTextColor(ContextCompat.getColor(this, R.color.orange_primary));
            progressHeartRate.setProgressTintList(
                    android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orange_primary)));

            int percentage = (int) (((bpm - 100) / 100.0) * 100);
            tvHeartRateRange.setText("↑ " + percentage + "% from normal");
            tvHeartRateRange.setTextColor(ContextCompat.getColor(this, R.color.orange_primary));

        } else {
            // CRITICAL
            tvHeartRateStatus.setText("CRITICAL");
            tvHeartRateStatus.setTextColor(ContextCompat.getColor(this, R.color.red_primary));
            tvHeartRateStatus.setBackground(ContextCompat.getDrawable(this, R.drawable.card_rounded));
            tvHeartRateStatus.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red_shade)));
            tvHeartRate.setTextColor(ContextCompat.getColor(this, R.color.red_primary));
            progressHeartRate.setProgressTintList(
                    android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red_primary)));

            if (bpm > 120) {
                tvHeartRateRange.setText("↑ Dangerously high");
            } else {
                tvHeartRateRange.setText("↓ Too low");
            }
            tvHeartRateRange.setTextColor(0xFFFF4444);
        }

        // Update progress (scale to 0-150 BPM range)
        int progress = Math.min((bpm * 100) / 150, 100);
        progressHeartRate.setProgress(progress);
    }

    private void updateTemperatureUI(double temp) {
        // Check if temperature is valid (not -1000 or other error values)
        if (temp <= -100) {
            tvTemperature.setText("--");
            tvTempStatus.setText("NO DATA");
            tvTempStatus.setTextColor(0xFF999999);
            tvTempStatus.setBackgroundColor(0xFFEEEEEE);
            tvTempRange.setText("Sensor unavailable");
            tvTempRange.setTextColor(0xFF999999);
            progressTemperature.setProgress(0);
            return;
        }

        tvTemperature.setText(String.format("%.1f", temp));

        // Normal range for dogs: 38.0-39.2°C
        if (temp >= 38.0 && temp <= 39.2) {
            // NORMAL
            tvTempStatus.setText("NORMAL");
            tvTempStatus.setTextColor(ContextCompat.getColor(this, R.color.green_primary));
            tvTempStatus.setBackground(ContextCompat.getDrawable(this, R.drawable.card_rounded));
            tvTempStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green_shade)));
            tvTemperature.setTextColor(ContextCompat.getColor(this, R.color.green_primary));
            progressTemperature.setProgressTintList(
                    android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green_primary)));
            tvTempRange.setText("✓ Stable");
            tvTempRange.setTextColor(ContextCompat.getColor(this, R.color.green_primary));

        } else if ((temp > 39.2 && temp <= 39.7) || (temp >= 37.5 && temp < 38.0)) {
            // WARNING
            tvTempStatus.setText("WARNING");
            tvTempStatus.setTextColor(ContextCompat.getColor(this, R.color.orange_primary));
            tvTempStatus.setBackground(ContextCompat.getDrawable(this, R.drawable.card_rounded));
            tvTempStatus.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orange_shade)));
            tvTemperature.setTextColor(ContextCompat.getColor(this, R.color.orange_primary));
            progressTemperature.setProgressTintList(
                    android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orange_primary)));

            if (temp > 39.2) {
                tvTempRange.setText("Running up");
            } else {
                tvTempRange.setText("Below normal");
            }
            tvTempRange.setTextColor(ContextCompat.getColor(this, R.color.orange_primary));

        } else {
            // CRITICAL
            tvTempStatus.setText("CRITICAL");
            tvTempStatus.setTextColor(ContextCompat.getColor(this, R.color.red_primary));
            tvTempStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red_shade)));
            tvTemperature.setTextColor(ContextCompat.getColor(this, R.color.red_primary));
            progressTemperature.setProgressTintList(
                    android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red_primary)));

            if (temp > 39.7) {
                tvTempRange.setText("Fever detected");
            } else {
                tvTempRange.setText("Hypothermia risk");
            }
            tvTempRange.setTextColor(ContextCompat.getColor(this, R.color.red_primary));
        }

        // Update progress (scale to 36-42°C range)
        int progress = (int) (((temp - 36.0) / 6.0) * 100);
        progress = Math.max(0, Math.min(100, progress));
        progressTemperature.setProgress(progress);
    }

    private void updateBloodOxygenUI(int bo) {
        tvOxygen.setText(String.valueOf(bo));

        // Normal range: 95-100%
        if (bo >= 95 && bo <= 100) {
            // NORMAL
            tvOxygenStatus.setText("NORMAL");
            tvOxygenStatus.setTextColor(ContextCompat.getColor(this, R.color.green_primary));
            tvOxygenStatus.setBackground(ContextCompat.getDrawable(this, R.drawable.card_rounded));
            tvOxygenStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green_shade)));
            tvOxygen.setTextColor(ContextCompat.getColor(this, R.color.green_primary));
            progressOxygen.setProgressTintList(
                    android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green_primary)));
            tvOxygenRange.setText("✓ Stable");
            tvOxygenRange.setTextColor(ContextCompat.getColor(this, R.color.green_primary));

        } else if (bo >= 90 && bo < 95) {
            // WARNING
            tvOxygenStatus.setText("WARNING");
            tvOxygenStatus.setTextColor(ContextCompat.getColor(this, R.color.orange_primary));
            tvOxygenStatus.setBackground(ContextCompat.getDrawable(this, R.drawable.card_rounded));
            tvOxygenStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orange_shade)));
            tvOxygen.setTextColor(ContextCompat.getColor(this, R.color.orange_primary));
            progressOxygen.setProgressTintList(
                    android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orange_primary)));
            tvOxygenRange.setText("Below optimal");
            tvOxygenRange.setTextColor(ContextCompat.getColor(this, R.color.orange_primary));

        } else {
            // CRITICAL
            tvOxygenStatus.setText("CRITICAL");
            tvOxygenStatus.setTextColor(ContextCompat.getColor(this, R.color.red_primary));
            tvOxygenStatus.setBackground(ContextCompat.getDrawable(this, R.drawable.card_rounded));
            tvOxygenStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf
                    (ContextCompat.getColor(this, R.color.red_shade)));
                    tvOxygen.setTextColor(ContextCompat.getColor(this, R.color.red_primary));
            progressOxygen.setProgressTintList(
                    android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red_primary)));
            tvOxygenRange.setText("⚠ Low oxygen");
            tvOxygenRange.setTextColor(ContextCompat.getColor(this, R.color.red_primary));
        }

        // Update progress
        progressOxygen.setProgress(bo);
    }
}