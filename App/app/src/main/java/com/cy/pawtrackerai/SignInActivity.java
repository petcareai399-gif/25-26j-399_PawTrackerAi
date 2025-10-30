package com.cy.pawtrackerai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {

    private TextInputLayout emailInputLayout, passwordInputLayout;
    private TextInputEditText emailEditText, passwordEditText;
    private MaterialButton signInButton;
    private ProgressBar progressBar;
    private ImageView backButton;
    private TextView signUpText;
    private CheckBox rememberMeCheckBox;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "PawTrackerPrefs";
    private static final String KEY_REMEMBER_ME = "rememberMe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Initialize views
        initViews();

        // Set click listeners
        setClickListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is already signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in, check if email is verified
            if (currentUser.isEmailVerified()) {
                // Update database verification status
                updateEmailVerificationStatus(currentUser.getUid());
                navigateToMain();
            } else {
                // Email not verified, sign out
                mAuth.signOut();
                Toast.makeText(this, "Please verify your email first. Check your inbox and spam folder.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initViews() {
        emailInputLayout = findViewById(R.id.emailInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        signInButton = findViewById(R.id.signInButton);
        backButton = findViewById(R.id.backButton);
        signUpText = findViewById(R.id.signUpText);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);

        // Create progress bar programmatically if not in layout
        progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.GONE);
    }

    private void setClickListeners() {
        signInButton.setOnClickListener(v -> signInUser());

        backButton.setOnClickListener(v -> finish());

        signUpText.setOnClickListener(v -> {
            // Navigate to Sign Up Activity
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        });
    }

    private void signInUser() {
        // Get input values
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        boolean rememberMe = rememberMeCheckBox.isChecked();

        // Reset errors
        emailInputLayout.setError(null);
        passwordInputLayout.setError(null);

        // Validate inputs
        if (!validateInputs(email, password)) {
            return;
        }

        // Show progress
        showProgress(true);

        // Sign in with Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Reload user to get latest email verification status
                            user.reload().addOnCompleteListener(reloadTask -> {
                                if (user.isEmailVerified()) {
                                    // Save remember me preference
                                    saveRememberMePreference(rememberMe);

                                    // Update database verification status
                                    updateEmailVerificationStatus(user.getUid());


                                    navigateToMain();
                                } else {
                                    // Email not verified
                                    showProgress(false);
                                    mAuth.signOut();

                                    Toast.makeText(SignInActivity.this,
                                            "Please verify your email before signing in.\nCheck your inbox and spam folder.",
                                            Toast.LENGTH_LONG).show();

                                    // Show resend verification option
                                    showResendVerificationOption(user);
                                }
                            });
                        }
                    } else {
                        // Sign in failed
                        showProgress(false);

                        String errorMessage = task.getException() != null
                                ? task.getException().getMessage()
                                : "Authentication failed";

                        // Handle specific error cases
                        if (errorMessage.contains("no user record")) {
                            Toast.makeText(SignInActivity.this,
                                    "No account found with this email",
                                    Toast.LENGTH_SHORT).show();
                        } else if (errorMessage.contains("password is invalid") ||
                                errorMessage.contains("INVALID_PASSWORD")) {
                            Toast.makeText(SignInActivity.this,
                                    "Incorrect password",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignInActivity.this,
                                    "Sign in failed: " + errorMessage,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean validateInputs(String email, String password) {
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

        if (password.length() < 6) {
            passwordInputLayout.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return false;
        }

        return true;
    }

    private void saveRememberMePreference(boolean rememberMe) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_REMEMBER_ME, rememberMe);
        editor.apply();
    }

    private void updateEmailVerificationStatus(String userId) {
        // Update emailVerified field in database
        mDatabase.child("users").child(userId).child("emailVerified").setValue(true)
                .addOnFailureListener(e -> {
                    // Log error but don't prevent sign in
                    e.printStackTrace();
                });
    }

    private void showResendVerificationOption(FirebaseUser user) {
        // Resend verification email
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignInActivity.this,
                                "Verification email resent. Please check your inbox and spam folder.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SignInActivity.this,
                                "Failed to resend verification email. Please try again later.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showProgress(boolean show) {
        if (show) {
            signInButton.setEnabled(false);
            signInButton.setText("Signing in...");
        } else {
            signInButton.setEnabled(true);
            signInButton.setText("Sign In");
        }
    }

    private void navigateToMain() {
        String userId = mAuth.getCurrentUser().getUid();

        // Check if profile is completed
        mDatabase.child("users").child(userId).child("petInfo").child("profileCompleted")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Boolean profileCompleted = task.getResult().getValue(Boolean.class);

                        Intent intent;
                        if (profileCompleted != null && profileCompleted) {
                            // Profile completed - go to MainActivity
                            intent = new Intent(SignInActivity.this, MainActivity.class);
                        } else {
                            // Profile not completed - go to PetInfoActivity
                            intent = new Intent(SignInActivity.this, PetInfoActivity.class);
                        }

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error fetching data - default to PetInfoActivity
                        Intent intent = new Intent(SignInActivity.this, PetInfoActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
    }
}