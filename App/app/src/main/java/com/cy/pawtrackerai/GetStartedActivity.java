package com.cy.pawtrackerai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class GetStartedActivity extends AppCompatActivity {

    private MaterialButton nextButton;
    private View featureCard1, featureCard2, featureCard3, featureCard4;
    private View dot1, dot2, dot3, dot4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_started);

        // Initialize views
        nextButton = findViewById(R.id.nextButton);

        // Find feature cards (you'll need to add IDs to your XML)
        featureCard1 = findViewById(R.id.featureCard1);
        featureCard2 = findViewById(R.id.featureCard2);
        featureCard3 = findViewById(R.id.featureCard3);
        featureCard4 = findViewById(R.id.featureCard4);

        // Find dots (add IDs to your XML)
        dot1 = findViewById(R.id.dot1);
        dot2 = findViewById(R.id.dot2);
        dot3 = findViewById(R.id.dot3);
        dot4 = findViewById(R.id.dot4);

        // Start animations
        animateFeatureCards();
        animateDots();
        animateButton();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add scale animation on click
                view.animate()
                        .scaleX(0.95f)
                        .scaleY(0.95f)
                        .setDuration(100)
                        .withEndAction(() -> {
                            view.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(100)
                                    .withEndAction(() -> {
                                        Intent intent = new Intent(GetStartedActivity.this, SignInActivity.class);
                                        startActivity(intent);
                                        // Add transition animation
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    })
                                    .start();
                        })
                        .start();
            }
        });
    }

    private void animateFeatureCards() {
        // Staggered slide-in animation from left with fade
        animateCardWithDelay(featureCard1, 0);
        animateCardWithDelay(featureCard2, 150);
        animateCardWithDelay(featureCard3, 300);
        animateCardWithDelay(featureCard4, 450);
    }

    private void animateCardWithDelay(View card, long delay) {
        if (card == null) return;

        card.setAlpha(0f);
        card.setTranslationX(-100f);

        card.animate()
                .alpha(1f)
                .translationX(0f)
                .setDuration(600)
                .setStartDelay(delay)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    private void animateDots() {
        // Pulsing animation for dots
        animateDotPulse(dot1, 0);
        animateDotPulse(dot2, 150);
        animateDotPulse(dot3, 300);
        animateDotPulse(dot4, 450);
    }

    private void animateDotPulse(View dot, long delay) {
        if (dot == null) return;

        dot.setScaleX(0f);
        dot.setScaleY(0f);

        dot.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(400)
                .setStartDelay(delay + 300)
                .setInterpolator(new OvershootInterpolator())
                .withEndAction(() -> startContinuousPulse(dot))
                .start();
    }

    private void startContinuousPulse(View dot) {
        if (dot == null) return;

        dot.animate()
                .scaleX(1.3f)
                .scaleY(1.3f)
                .setDuration(800)
                .withEndAction(() -> {
                    dot.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(800)
                            .withEndAction(() -> startContinuousPulse(dot))
                            .start();
                })
                .start();
    }

    private void animateButton() {
        // Slide up animation for button
        if (nextButton == null) return;

        nextButton.setAlpha(0f);
        nextButton.setTranslationY(100f);

        nextButton.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setStartDelay(800)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Clear all animations to prevent memory leaks
        if (dot1 != null) dot1.clearAnimation();
        if (dot2 != null) dot2.clearAnimation();
        if (dot3 != null) dot3.clearAnimation();
        if (dot4 != null) dot4.clearAnimation();
    }
}