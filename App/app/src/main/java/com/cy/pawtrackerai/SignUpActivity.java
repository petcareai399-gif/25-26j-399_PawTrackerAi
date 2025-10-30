package com.cy.pawtrackerai;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout emailInputLayout,nameInputLayout, passwordInputLayout, confirmPasswordInputLayout;
    private TextInputEditText emailEditText, passwordEditText, confirmPasswordEditText, nameEditText;
    private MaterialButton signUpButton;
    private ProgressBar progressBar;
    private ImageView backButton;
    private TextView signInText;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        initViews();

        // Set click listeners
        setClickListeners();
    }

    private void initViews() {
        emailInputLayout = findViewById(R.id.emailInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        confirmPasswordInputLayout = findViewById(R.id.confirmPasswordInputLayout);
        nameInputLayout = findViewById(R.id.nameInputLayout);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        nameEditText = findViewById(R.id.nameEditText);

        signUpButton = findViewById(R.id.signUpButton);
        progressBar = findViewById(R.id.progressBar);
        backButton = findViewById(R.id.backButton);
        signInText = findViewById(R.id.signInText);
    }

    private void setClickListeners() {
        signUpButton.setOnClickListener(v -> registerUser());

        backButton.setOnClickListener(v -> finish());

        signInText.setOnClickListener(v -> {
            // Navigate to Login Activity
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            finish();
        });
    }

    private void registerUser() {
        // Get input values
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Reset errors
        nameInputLayout.setError(null);
        emailInputLayout.setError(null);
        passwordInputLayout.setError(null);
        confirmPasswordInputLayout.setError(null);

        // Validate inputs
        if (!validateInputs( email, password, confirmPassword, name)) {
            return;
        }

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);
        signUpButton.setEnabled(false);

        // Create user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign up success
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Send verification email
                            sendVerificationEmail(user);

                            // Save user data to Realtime Database
                            saveUserToDatabase(user, email, name);
                        }
                    } else {
                        // Sign up failed
                        progressBar.setVisibility(View.GONE);
                        signUpButton.setEnabled(true);

                        String errorMessage = task.getException() != null
                                ? task.getException().getMessage()
                                : "Registration failed";
                        Toast.makeText(SignUpActivity.this, errorMessage,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validateInputs(String email, String password, String confirmPassword, String name) {
        // Validate email


        if (TextUtils.isEmpty(email)) {
            emailInputLayout.setError("Email is required");
            emailEditText.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.setError("Please enter a valid email");
            emailEditText.requestFocus();
            return false;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            passwordInputLayout.setError("Password is required");
            passwordEditText.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(name)){
            nameInputLayout.setError("Name is required");
            nameEditText.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            passwordInputLayout.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return false;
        }

        // Validate confirm password
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordInputLayout.setError("Please confirm your password");
            confirmPasswordEditText.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordInputLayout.setError("Passwords do not match");
            confirmPasswordEditText.requestFocus();
            return false;
        }

        return true;
    }

    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this,
                                "Verification email sent to " + user.getEmail(),
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SignUpActivity.this,
                                "Failed to send verification email",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToDatabase(FirebaseUser user, String email, String name) {
        // Create user data map
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("name", name);
        userData.put("userId", user.getUid());
        userData.put("createdAt", System.currentTimeMillis());
        userData.put("emailVerified", false);
        userData.put("profileCompleted", false);

        // Save to Realtime Database: users -> userId -> userData
        mDatabase.child("users").child(user.getUid()).setValue(userData)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    signUpButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        // Show success message
                        Toast.makeText(SignUpActivity.this,
                                "Registration successful! Please check your email to verify your account.",
                                Toast.LENGTH_LONG).show();

                        // Sign out user until they verify email
                        mAuth.signOut();

                        // Navigate to login screen
                        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this,
                                "Failed to save user data",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}