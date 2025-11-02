package com.cy.pawtrackerai;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import com.cy.pawtrackerai.PawLoadingDialog;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private PawLoadingDialog loadingDialog;

    private CircleImageView ivPetAvatar;
    private TextView tvPetName;
    private TextView tvPetDetails;
    private ProgressBar progressBar;

    private CardView btnCheckSymptoms, cardMood, btnFeedNow, btnViewRecords;

    private LinearLayout alertBanner;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize loading dialog FIRST
        loadingDialog = new PawLoadingDialog(this);

        // Initialize UI components
        initializeViews();
        setupBottomNavigation();
        buttonFunc();

        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
            loadPetData();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            // Redirect to login if needed
        }
    }

    private void initializeViews() {
        ivPetAvatar = findViewById(R.id.ivPetAvatar);
        tvPetName = findViewById(R.id.tvPetName);
        tvPetDetails = findViewById(R.id.tvPetDetails);
        btnCheckSymptoms = findViewById(R.id.btnCheckSymptoms);

        cardMood = findViewById(R.id.btnMonitorMood);
        btnFeedNow = findViewById(R.id.btnFeedNow);
        btnViewRecords = findViewById(R.id.btnViewRecords);

        alertBanner = findViewById(R.id.alertBanner);

    }

    private void buttonFunc(){
        btnCheckSymptoms.setOnClickListener(view ->{
            startActivity(new Intent(MainActivity.this, SymptomCheckerActivity.class));
        });
        cardMood.setOnClickListener(view ->{
            startActivity(new Intent(MainActivity.this, MoodMonitoringActivity.class));
        });
        btnFeedNow.setOnClickListener(view ->{
            startActivity(new Intent(MainActivity.this, NutritionFeedingActivity.class));
        });
        btnViewRecords.setOnClickListener(view ->{
            startActivity(new Intent(MainActivity.this, NutritionFeedingActivity.class));
        });
        alertBanner.setOnClickListener(view ->{
            startActivity(new Intent(MainActivity.this, DiseaseAlertActivity.class));
        });
    }

    private void loadPetData() {
        loadingDialog.show();

        DatabaseReference userPetRef = mDatabase.child("users")
                .child(currentUserId)
                .child("petInfo");

        userPetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingDialog.dismiss();

                if (snapshot.exists()) {
                    // Get pet data
                    String petName = snapshot.child("petName").getValue(String.class);
                    String age = snapshot.child("age").getValue(String.class);
                    String breed = snapshot.child("breed").getValue(String.class);
                    String petImg = snapshot.child("petImg").getValue(String.class);
                    String species = snapshot.child("species").getValue(String.class);

                    // Display pet data
                    displayPetInfo(petName, age, breed, petImg);

                } else {
                    Toast.makeText(MainActivity.this,
                            "No pet information found",
                            Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Pet info does not exist for user: " + currentUserId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();

                Toast.makeText(MainActivity.this,
                        "Failed to load pet data: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });
    }

    private void displayPetInfo(String petName, String age, String breed, String petImgBase64) {
        // Set pet name
        if (petName != null && !petName.isEmpty()) {
            tvPetName.setText(capitalizeFirstLetter(petName));
        } else {
            tvPetName.setText("No Name");
        }

        // Set pet details (age • breed)
        if (age != null && breed != null) {
            String details = age + " years • " + capitalizeFirstLetter(breed);
            tvPetDetails.setText(details);
        } else if (age != null) {
            tvPetDetails.setText(age + " years");
        } else if (breed != null) {
            tvPetDetails.setText(capitalizeFirstLetter(breed));
        } else {
            tvPetDetails.setText("No details available");
        }

        // Load and display pet image from Base64
        if (petImgBase64 != null && !petImgBase64.isEmpty()) {
            loadBase64Image(petImgBase64);
        } else {
            // Set default pet avatar if no image
            ivPetAvatar.setImageResource(R.drawable.ic_dog);
        }
    }

    private void loadBase64Image(String base64String) {
        try {
            // Remove newlines and whitespace from base64 string
            String cleanBase64 = base64String.replaceAll("\\s", "");

            // Decode Base64 string to byte array
            byte[] decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT);

            // Convert byte array to Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            if (bitmap != null) {
                ivPetAvatar.setImageBitmap(bitmap);
            } else {
                // If decoding failed, set default image
                ivPetAvatar.setImageResource(R.drawable.ic_dog);
                Log.e(TAG, "Failed to decode bitmap from Base64");
            }
        } catch (Exception e) {
            // Handle any errors in decoding
            ivPetAvatar.setImageResource(R.drawable.ic_dog);
            Log.e(TAG, "Error loading Base64 image: " + e.getMessage());
        }
    }

    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView == null) return;

        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                return true;
            } else if (id == R.id.symptom) {
                startActivity(new Intent(this, SymptomCheckerActivity.class));
                return true;
            } else if (id == R.id.mood) {
                startActivity(new Intent(this, MoodMonitoringActivity.class));
                return true;
            } else if (id == R.id.feed) {
                startActivity(new Intent(this, NutritionFeedingActivity.class));
                return true;
            } else if (id == R.id.settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.home);
        }
    }
}