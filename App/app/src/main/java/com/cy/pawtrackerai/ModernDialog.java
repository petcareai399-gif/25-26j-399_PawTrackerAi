package com.cy.pawtrackerai;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.DrawableRes;

import com.google.android.material.button.MaterialButton;


public class ModernDialog {

    public interface OnDialogButtonClickListener {
        void onPositiveClick();
        void onNegativeClick();
    }

    private Dialog dialog;
    private ImageView imageView;
    private TextView titleTextView;
    private TextView subtitleTextView;
    private MaterialButton positiveButton;
    private MaterialButton negativeButton;

    private ModernDialog(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_modern);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,  // Width
                    ViewGroup.LayoutParams.WRAP_CONTENT   // Height
            );

            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(layoutParams);
        }

        imageView = dialog.findViewById(R.id.dialog_image);
        titleTextView = dialog.findViewById(R.id.dialog_title);
        subtitleTextView = dialog.findViewById(R.id.dialog_subtitle);
        positiveButton = dialog.findViewById(R.id.dialog_positive_button);
        negativeButton = dialog.findViewById(R.id.dialog_negative_button);
    }

    public static class Builder {
        private Context context;
        private String title;
        private String subtitle;
        private int imageResId = -1;
        private String positiveButtonText = "OK";
        private String negativeButtonText = "Cancel";
        private boolean showImage = false;
        private boolean showSubtitle = false;
        private boolean cancelable = true;
        private OnDialogButtonClickListener listener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setSubtitle(String subtitle) {
            this.subtitle = subtitle;
            this.showSubtitle = true;
            return this;
        }

        public Builder setImage(@DrawableRes int imageResId) {
            this.imageResId = imageResId;
            this.showImage = true;
            return this;
        }

        public Builder setPositiveButton(String text, OnDialogButtonClickListener listener) {
            this.positiveButtonText = text;
            this.listener = listener;
            return this;
        }

        public Builder setNegativeButton(String text) {
            this.negativeButtonText = text;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public ModernDialog build() {
            ModernDialog modernDialog = new ModernDialog(context);

            // Set title
            if (title != null) {
                modernDialog.titleTextView.setText(title);
            }

            // Set subtitle
            if (showSubtitle && subtitle != null) {
                modernDialog.subtitleTextView.setVisibility(View.VISIBLE);
                modernDialog.subtitleTextView.setText(subtitle);
            } else {
                modernDialog.subtitleTextView.setVisibility(View.GONE);
            }

            // Set image
            if (showImage && imageResId != -1) {
                modernDialog.imageView.setVisibility(View.VISIBLE);
                modernDialog.imageView.setImageResource(imageResId);
            } else {
                modernDialog.imageView.setVisibility(View.GONE);
            }

            // Set positive button
            modernDialog.positiveButton.setText(positiveButtonText);
            modernDialog.positiveButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPositiveClick();
                }
                modernDialog.dismiss();
            });

            // Set negative button
            modernDialog.negativeButton.setText(negativeButtonText);
            modernDialog.negativeButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNegativeClick();
                }
                modernDialog.dismiss();
            });

            modernDialog.dialog.setCancelable(cancelable);

            return modernDialog;
        }
    }

    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }
}