package com.cy.pawtrackerai;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.SimpleColorFilter;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieValueCallback;
import com.cy.pawtrackerai.R;

public class PawLoadingDialog {

    private Dialog dialog;
    private LottieAnimationView lottieAnimationView;
    private TextView loadingText;

    public PawLoadingDialog(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_paw_loading, null);
        dialog.setContentView(view);

        // Make dialog background transparent
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        lottieAnimationView = view.findViewById(R.id.lottieLoading);
        loadingText = view.findViewById(R.id.loadingText);

        // Set default color (Orange to match your app theme)
        setPawColor(ContextCompat.getColor(context, R.color.loading));
    }

    /**
     * Change the color of the paw animation
     * @param color Color resource (e.g., R.color.loading) or Color.parseColor("#FF6600")
     */
    public void setPawColor(int color) {
        if (lottieAnimationView != null) {
            lottieAnimationView.addValueCallback(
                    new KeyPath("**"),
                    LottieProperty.STROKE_COLOR,
                    new LottieValueCallback<>(color)
            );
        }
    }

    /**
     * Show loading dialog with default message
     */
    public void show() {
        show("Loading...");
    }

    /**
     * Show loading dialog with custom message
     * @param message Custom loading message
     */
    public void show(String message) {
        if (loadingText != null) {
            loadingText.setText(message);
        }
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
            if (lottieAnimationView != null) {
                lottieAnimationView.playAnimation();
            }
        }
    }

    /**
     * Show loading dialog with custom message and color
     * @param message Custom loading message
     * @param color Color for the paw animation
     */
    public void show(String message, int color) {
        setPawColor(color);
        show(message);
    }

    /**
     * Dismiss the loading dialog
     */
    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            if (lottieAnimationView != null) {
                lottieAnimationView.cancelAnimation();
            }
            dialog.dismiss();
        }
    }

    /**
     * Check if dialog is showing
     */
    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }

    /**
     * Set loading message while dialog is showing
     * @param message New message to display
     */
    public void setMessage(String message) {
        if (loadingText != null) {
            loadingText.setText(message);
        }
    }

    /**
     * Set whether the dialog can be cancelled by touching outside
     * @param cancelable true if cancelable, false otherwise
     */
    public void setCancelable(boolean cancelable) {
        if (dialog != null) {
            dialog.setCancelable(cancelable);
        }
    }
}