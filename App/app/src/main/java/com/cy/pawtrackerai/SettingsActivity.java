package com.cy.pawtrackerai;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "PawTrackerPrefs";
    private static final String KEY_REMEMBER_ME = "rememberMe";
    private PawLoadingDialog loadingDialog;

    private MaterialButton btnSignOut;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    private TextView tvUserName, tvUserEmail, tvPetName, tvPetInfo,tvPetAge, tvPetBreed;

    private ImageView imgPet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        // Initialize loading dialog
        loadingDialog = new PawLoadingDialog(this);
        btnSignOut = findViewById(R.id.btnSignOut);

        btnSignOut.setOnClickListener(v-> signOutUser());

        initViews();

        loadUserData();
    }

   private void initViews(){

       tvUserEmail = findViewById(R.id.tvUserEmail);
       tvUserName = findViewById(R.id.tvUserName);
       tvPetName = findViewById(R.id.tvPetName);
       tvPetAge = findViewById(R.id.tvPetAge);
       tvPetBreed = findViewById(R.id.tvPetBreed);
       imgPet = findViewById(R.id.imgPet);

    }




    private void signOutUser() {
        // Show modern confirmation dialog
        new ModernDialog.Builder(this)
                .setImage(R.drawable.log_out)
                .setTitle("Sign Out")
                .setSubtitle("Are you sure you want to sign out?")
                .setPositiveButton("Sign Out", new ModernDialog.OnDialogButtonClickListener() {
                    @Override
                    public void onPositiveClick() {
                        loadingDialog.show("Signing out...");

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            // Sign out from Firebase
                            FirebaseAuth.getInstance().signOut();

                            // Clear Remember Me preference
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(KEY_REMEMBER_ME, false);
                            editor.apply();

                            loadingDialog.dismiss();

                            // Redirect to Sign In page
                            Intent intent = new Intent(SettingsActivity.this, SignInActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }, 2000);
                    }

                    @Override
                    public void onNegativeClick() {
                        // User cancelled - do nothing
                    }
                })
                .setNegativeButton("Cancel")
                .setCancelable(true)
                .build()
                .show();
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Display email from FirebaseAuth (always available immediately)
            tvUserEmail.setText(currentUser.getEmail());

            // Show loading dialog
            loadingDialog.show("Loading...");

            // Fetch data from Realtime Database
            mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Dismiss loading dialog when data is received
                    loadingDialog.dismiss();

                    if (snapshot.exists()) {
                        // Get user data
                        String name = snapshot.child("name").getValue(String.class);

                        // Get pet data from petInfo node
                        DataSnapshot petInfoSnapshot = snapshot.child("petInfo");

                        String petName = petInfoSnapshot.child("petName").getValue(String.class);
                        String breed = petInfoSnapshot.child("breed").getValue(String.class);
                        String age = petInfoSnapshot.child("age").getValue(String.class);
                        String petImgBase64 = petInfoSnapshot.child("petImg").getValue(String.class);

                        // Set data to TextViews
                        tvUserName.setText(name != null ? name : "N/A");
                        tvPetName.setText(petName != null ? petName : "N/A");
                        tvPetAge.setText(age != null ? age + " years  • " : "N/A");
                        tvPetBreed.setText(breed != null ? breed : "N/A");

                        // Decode and display Base64 image
                        if (petImgBase64 != null && !petImgBase64.isEmpty()) {
                            loadBase64Image(petImgBase64);
                        }
                    } else {
                        Toast.makeText(SettingsActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Dismiss loading dialog on error
                    loadingDialog.dismiss();
                    Toast.makeText(SettingsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadBase64Image(String base64String) {
        try {
            // Remove prefix if exists (e.g., "data:image/png;base64,")
            if (base64String.contains(",")) {
                base64String = base64String.split(",")[1];
            }

            // Decode Base64 string to byte array
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);

            // Convert byte array to Bitmap
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            // Set bitmap to ImageView
            imgPet.setImageBitmap(decodedBitmap);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
        }
    }
}