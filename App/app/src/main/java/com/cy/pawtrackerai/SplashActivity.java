package com.cy.pawtrackerai;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private FrameLayout swipeButton;
    private RelativeLayout swipeContainer;
    private TextView swipeHintText;
    private TextView titleText;
    private TextView descriptionText;
    private LinearLayout bottomContent;
    private ImageView dogIcon;

    private float initialX = 0f;
    private float buttonStartX = 0f;
    private float maxSwipeDistance = 0f;

    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;

    private static final String PREF_NAME = "PawTrackerPrefs";
    private static final String KEY_FIRST_LAUNCH = "isFirstLaunch";
    private static final String KEY_REMEMBER_ME = "rememberMe";
    private static final int SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Install splash screen BEFORE super.onCreate()
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Initialize views
        swipeButton = findViewById(R.id.swipeButton);
        swipeContainer = findViewById(R.id.swipeContainer);
        swipeHintText = findViewById(R.id.swipeHintText);
        titleText = findViewById(R.id.titleText);
        descriptionText = findViewById(R.id.descriptionText);
        bottomContent = findViewById(R.id.bottom_content);
        dogIcon = findViewById(R.id.dogIcon);

        // Check if this is the first launch
        boolean isFirstLaunch = sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true);

        if (isFirstLaunch) {
            // First launch - show swipe screen with animations
            showSwipeScreen();
        } else {
            // Not first launch - hide swipe UI and check authentication
            hideSwipeUI();
            checkAuthenticationAndNavigate();
        }
    }

    private void showSwipeScreen() {
        // Show title and bottom content for first launch
        titleText.setVisibility(View.VISIBLE);
        bottomContent.setVisibility(View.VISIBLE);

        // Start entrance animations after a short delay
        titleText.postDelayed(new Runnable() {
            @Override
            public void run() {
                startEntranceAnimations();
            }
        }, 200);

        // Set up swipe functionality
        setupSwipeButton();
    }

    private void startEntranceAnimations() {
        // Title Animation: Fade in + Slide down from top
        titleText.setAlpha(0f);
        titleText.setTranslationY(-100f);
        ObjectAnimator titleAlpha = ObjectAnimator.ofFloat(titleText, "alpha", 0f, 1f);
        titleAlpha.setDuration(800);

        ObjectAnimator titleTranslate = ObjectAnimator.ofFloat(titleText, "translationY", -100f, 0f);
        titleTranslate.setDuration(800);
        titleTranslate.setInterpolator(new OvershootInterpolator(1.2f));

        // Description Animation: Fade in + Slide up
        descriptionText.setAlpha(0f);
        descriptionText.setTranslationY(50f);
        ObjectAnimator descAlpha = ObjectAnimator.ofFloat(descriptionText, "alpha", 0f, 1f);
        descAlpha.setDuration(800);
        descAlpha.setStartDelay(300);

        ObjectAnimator descTranslate = ObjectAnimator.ofFloat(descriptionText, "translationY", 50f, 0f);
        descTranslate.setDuration(800);
        descTranslate.setStartDelay(300);
        descTranslate.setInterpolator(new AccelerateDecelerateInterpolator());

        // Swipe Container: Fade in + Slide up
        swipeContainer.setAlpha(0f);
        swipeContainer.setTranslationY(100f);
        ObjectAnimator containerAlpha = ObjectAnimator.ofFloat(swipeContainer, "alpha", 0f, 1f);
        containerAlpha.setDuration(800);
        containerAlpha.setStartDelay(500);

        ObjectAnimator containerTranslate = ObjectAnimator.ofFloat(swipeContainer, "translationY", 100f, 0f);
        containerTranslate.setDuration(800);
        containerTranslate.setStartDelay(500);
        containerTranslate.setInterpolator(new AccelerateDecelerateInterpolator());

        // Dog Icon: Scale bounce animation
        dogIcon.setScaleX(0f);
        dogIcon.setScaleY(0f);
        ObjectAnimator iconScaleX = ObjectAnimator.ofFloat(dogIcon, "scaleX", 0f, 1f);
        iconScaleX.setDuration(600);
        iconScaleX.setStartDelay(800);
        iconScaleX.setInterpolator(new BounceInterpolator());

        ObjectAnimator iconScaleY = ObjectAnimator.ofFloat(dogIcon, "scaleY", 0f, 1f);
        iconScaleY.setDuration(600);
        iconScaleY.setStartDelay(800);
        iconScaleY.setInterpolator(new BounceInterpolator());

        // Rotate animation for dog icon
        ObjectAnimator iconRotate = ObjectAnimator.ofFloat(dogIcon, "rotation", -180f, 0f);
        iconRotate.setDuration(600);
        iconRotate.setStartDelay(800);

        // Play all animations
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                titleAlpha, titleTranslate,
                descAlpha, descTranslate,
                containerAlpha, containerTranslate,
                iconScaleX, iconScaleY, iconRotate
        );
        animatorSet.start();

        // Start blinking animation for hint text after entrance
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startBlinkingAnimation();
                startPawIconPulse();
            }
        }, 1500);
    }

    private void startPawIconPulse() {
        // Continuous gentle pulse for paw icon
        ObjectAnimator pulseX = ObjectAnimator.ofFloat(dogIcon, "scaleX", 1f, 1.15f, 1f);
        pulseX.setDuration(1500);
        pulseX.setRepeatCount(ValueAnimator.INFINITE);
        pulseX.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator pulseY = ObjectAnimator.ofFloat(dogIcon, "scaleY", 1f, 1.15f, 1f);
        pulseY.setDuration(1500);
        pulseY.setRepeatCount(ValueAnimator.INFINITE);
        pulseY.setInterpolator(new AccelerateDecelerateInterpolator());

        AnimatorSet pulseSet = new AnimatorSet();
        pulseSet.playTogether(pulseX, pulseY);
        pulseSet.start();
    }

    private void hideSwipeUI() {
        // Hide swipe UI elements
        titleText.setVisibility(View.GONE);
        bottomContent.setVisibility(View.GONE);
    }

    private void checkAuthenticationAndNavigate() {
        // Show logo for 2 seconds before navigating
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean rememberMe = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);
                FirebaseUser currentUser = mAuth.getCurrentUser();

                if (rememberMe && currentUser != null && currentUser.isEmailVerified()) {
                    // User wants to be remembered and is logged in with verified email
                    navigateToMain();
                } else {
                    // User doesn't want to be remembered or is not logged in
                    // Sign out to clear the session
                    if (currentUser != null) {
                        mAuth.signOut();
                    }
                    navigateToSignIn();
                }
            }
        }, SPLASH_DELAY);
    }

    private void startBlinkingAnimation() {
        ObjectAnimator blinkAnimator = ObjectAnimator.ofFloat(swipeHintText, "alpha", 0.6f, 0.2f);
        blinkAnimator.setDuration(1000);
        blinkAnimator.setRepeatCount(ValueAnimator.INFINITE);
        blinkAnimator.setRepeatMode(ValueAnimator.REVERSE);
        blinkAnimator.start();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupSwipeButton() {
        swipeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = event.getRawX();
                        buttonStartX = view.getX();

                        // Calculate max swipe distance
                        maxSwipeDistance = swipeContainer.getWidth() - swipeButton.getWidth() - 10f;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float deltaX = event.getRawX() - initialX;
                        float newX = buttonStartX + deltaX;

                        // Constrain movement within container
                        if (newX < 0) newX = 0f;
                        if (newX > maxSwipeDistance) newX = maxSwipeDistance;

                        view.setX(newX);

                        // Fade out hint text as button moves
                        float progress = newX / maxSwipeDistance;
                        swipeHintText.setAlpha(0.6f * (1 - progress));

                        // Scale button slightly when dragging
                        float scale = 1f + (0.1f * progress);
                        view.setScaleX(scale);
                        view.setScaleY(scale);

                        return true;

                    case MotionEvent.ACTION_UP:
                        float currentX = view.getX();
                        float swipeProgress = currentX / maxSwipeDistance;

                        if (swipeProgress >= 0.8f) {
                            // Swipe completed - mark first launch as complete and navigate
                            completeSwipe(view);
                        } else {
                            // Swipe not completed - animate back to start
                            animateBackToStart(view);
                        }
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    private void completeSwipe(View view) {
        // Mark first launch as complete
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_FIRST_LAUNCH, false);
        editor.apply();

        // Animate button to the end
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "x", maxSwipeDistance);
        animator.setDuration(200);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addListener(new android.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(android.animation.Animator animation) {}

            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                // Navigate to GetStarted activity
                navigateToGetStarted();
            }

            @Override
            public void onAnimationCancel(android.animation.Animator animation) {}

            @Override
            public void onAnimationRepeat(android.animation.Animator animation) {}
        });
        animator.start();

        // Fade out hint text completely
        swipeHintText.animate().alpha(0f).setDuration(200).start();
    }

    private void animateBackToStart(View view) {
        // Animate button back to start position
        ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(view, "x", 0f);
        positionAnimator.setDuration(300);
        positionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        positionAnimator.start();

        // Reset scale
        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .start();

        // Fade hint text back in
        swipeHintText.animate()
                .alpha(0.6f)
                .setDuration(300)
                .start();
    }

    private void navigateToGetStarted() {
        Intent intent = new Intent(SplashActivity.this, GetStartedActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void navigateToMain() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void navigateToSignIn() {
        Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}