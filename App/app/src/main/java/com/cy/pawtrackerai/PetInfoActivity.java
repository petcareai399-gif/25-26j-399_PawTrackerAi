package com.cy.pawtrackerai;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PetInfoActivity extends AppCompatActivity {

    private ImageView backButton;
    private CircleImageView petProfileImage;
    private FloatingActionButton cameraButton;
    private TextInputLayout petNameInputLayout, speciesInputLayout, genderInputLayout;
    private TextInputLayout breedInputLayout, ageInputLayout, weightInputLayout;
    private TextInputEditText petNameEditText, breedEditText, ageEditText, weightEditText;
    private AutoCompleteTextView speciesSpinner, genderSpinner;
    private MaterialButton continueButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private String petImageBase64 = "";
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;

    // Activity Result Launchers
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_info);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        initViews();

        // Setup spinners
        setupSpinners();

        // Setup click listeners
        setupClickListeners();

        // Initialize activity result launchers
        initActivityResultLaunchers();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        petProfileImage = findViewById(R.id.petProfileImage);
        cameraButton = findViewById(R.id.cameraButton);

        petNameInputLayout = findViewById(R.id.petNameInputLayout);
        speciesInputLayout = findViewById(R.id.speciesInputLayout);
        genderInputLayout = findViewById(R.id.genderInputLayout);
        breedInputLayout = findViewById(R.id.breedInputLayout);
        ageInputLayout = findViewById(R.id.ageInputLayout);
        weightInputLayout = findViewById(R.id.weightInputLayout);

        petNameEditText = findViewById(R.id.petNameEditText);
        breedEditText = findViewById(R.id.breedEditText);
        ageEditText = findViewById(R.id.ageEditText);
        weightEditText = findViewById(R.id.weightEditText);

        speciesSpinner = findViewById(R.id.speciesSpinner);
        genderSpinner = findViewById(R.id.genderSpinner);

        continueButton = findViewById(R.id.continueButton);
    }

    private void setupSpinners() {
        // Species spinner with emojis
        String[] speciesArray = {"Dog 🐕", "Cat 🐈"};
        ArrayAdapter<String> speciesAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                speciesArray
        );
        speciesSpinner.setAdapter(speciesAdapter);

        // Gender spinner
        String[] genderArray = {"Male", "Female"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                genderArray
        );
        genderSpinner.setAdapter(genderAdapter);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        cameraButton.setOnClickListener(v -> showImagePickerDialog());

        continueButton.setOnClickListener(v -> savePetInfo());
    }

    private void initActivityResultLaunchers() {
        // Camera launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        if (imageBitmap != null) {
                            petProfileImage.setImageBitmap(imageBitmap);
                            petImageBase64 = convertBitmapToBase64(imageBitmap);
                        }
                    }
                }
        );

        // Gallery launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            petProfileImage.setImageBitmap(bitmap);
                            petImageBase64 = convertBitmapToBase64(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Take Photo
                if (checkCameraPermission()) {
                    openCamera();
                } else {
                    requestCameraPermission();
                }
            } else {
                // Choose from Gallery
                if (checkStoragePermission()) {
                    openGallery();
                } else {
                    requestStoragePermission();
                }
            }
        });
        builder.show();
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_CODE);
    }

    private boolean checkStoragePermission() {
        // For Android 13+ (API 33+), use READ_MEDIA_IMAGES
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            // For older versions, use READ_EXTERNAL_STORAGE
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestStoragePermission() {
        // For Android 13+ (API 33+), request READ_MEDIA_IMAGES
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    STORAGE_PERMISSION_CODE);
        } else {
            // For older versions, request READ_EXTERNAL_STORAGE
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        }
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
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(cameraIntent);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void savePetInfo() {
        // Get input values
        String petName = petNameEditText.getText().toString().trim();
        String species = speciesSpinner.getText().toString().trim();
        String gender = genderSpinner.getText().toString().trim();
        String breed = breedEditText.getText().toString().trim();
        String age = ageEditText.getText().toString().trim();
        String weight = weightEditText.getText().toString().trim();

        // Reset errors
        petNameInputLayout.setError(null);
        speciesInputLayout.setError(null);
        genderInputLayout.setError(null);

        // Validate mandatory fields
        boolean isValid = true;

        if (TextUtils.isEmpty(petName)) {
            petNameInputLayout.setError("Pet name is required");
            petNameEditText.requestFocus();
            isValid = false;
        }

        if (TextUtils.isEmpty(species)) {
            speciesInputLayout.setError("Species is required");
            speciesSpinner.requestFocus();
            isValid = false;
        }

        if (TextUtils.isEmpty(gender)) {
            genderInputLayout.setError("Gender is required");
            genderSpinner.requestFocus();
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        // Remove emoji from species for storage
        String speciesClean = species.replace("🐕", "").replace("🐈", "").trim();

        // Get current user ID
        String userId = mAuth.getCurrentUser().getUid();

        // Create pet data map
        HashMap<String, Object> petData = new HashMap<>();
        petData.put("petImg", petImageBase64);
        petData.put("petName", petName);
        petData.put("species", speciesClean);
        petData.put("gender", gender);
        petData.put("breed", breed);
        petData.put("age", age);
        petData.put("weight", weight);
        petData.put("profileCompleted", true);

        // Show loading
        continueButton.setEnabled(false);
        continueButton.setText("Saving...");

        // Save to Firebase under users/{userId}/petInfo
        mDatabase.child("users").child(userId).child("petInfo").setValue(petData)
                .addOnCompleteListener(task -> {
                    continueButton.setEnabled(true);
                    continueButton.setText("Continue");

                    if (task.isSuccessful()) {
                        Toast.makeText(PetInfoActivity.this,
                                "Pet information saved successfully!",
                                Toast.LENGTH_SHORT).show();

                        // Navigate to MainActivity
                        Intent intent = new Intent(PetInfoActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(PetInfoActivity.this,
                                "Failed to save pet information",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}