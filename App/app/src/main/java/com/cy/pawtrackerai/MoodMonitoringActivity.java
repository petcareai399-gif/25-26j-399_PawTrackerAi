package com.cy.pawtrackerai;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MoodMonitoringActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_VIDEO_CAPTURE = 102;
    private static final int REQUEST_GALLERY_PICK = 103;

    private CardView cardTakePhoto, cardRecordVideo, cardChooseGallery, cardMediaPreview;
    private ImageView btnBack, imgMediaPreview, btnRemoveMedia, imgMediaTypeIcon;
    private TextView tvMediaStatus;
    private Button btnAnalyzeMood;

    private Uri currentMediaUri;
    private String currentMediaType; // "photo", "video", or "gallery"
    private File currentMediaFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_monitoring);

        initViews();
        btnAnalyzeMood.setOnClickListener(v -> {
            Intent intent = new Intent(this, MoodAnalysisActivity.class);
            startActivity(intent);
        });
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        cardTakePhoto = findViewById(R.id.cardTakePhoto);
        cardRecordVideo = findViewById(R.id.cardRecordVideo);
        cardChooseGallery = findViewById(R.id.cardChooseGallery);
        cardMediaPreview = findViewById(R.id.cardMediaPreview);
        imgMediaPreview = findViewById(R.id.imgMediaPreview);
        btnRemoveMedia = findViewById(R.id.btnRemoveMedia);
        imgMediaTypeIcon = findViewById(R.id.imgMediaTypeIcon);
        tvMediaStatus = findViewById(R.id.tvMediaStatus);
        btnAnalyzeMood = findViewById(R.id.btnAnalyzeMood);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        cardTakePhoto.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                clearCurrentMedia();
                openCamera();
            } else {
                requestCameraPermission();
            }
        });

        cardRecordVideo.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                clearCurrentMedia();
                openVideoRecorder();
            } else {
                requestCameraPermission();
            }
        });

        cardChooseGallery.setOnClickListener(v -> {
            clearCurrentMedia();
            openGallery();
        });

        btnRemoveMedia.setOnClickListener(v -> clearCurrentMedia());

//        btnAnalyzeMood.setOnClickListener(v -> {
//            if (currentMediaUri != null) {
//                analyzeMedia();
//            }
//        });

        //test code

    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_CAMERA_PERMISSION);
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create a file to save the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                currentMediaFile = photoFile;
                currentMediaUri = FileProvider.getUriForFile(this,
                        "com.cy.pawtrackerai.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentMediaUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void openVideoRecorder() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            // Create a file to save the video
            File videoFile = null;
            try {
                videoFile = createVideoFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error creating video file", Toast.LENGTH_SHORT).show();
            }

            if (videoFile != null) {
                currentMediaFile = videoFile;
                currentMediaUri = FileProvider.getUriForFile(this,
                        "com.cy.pawtrackerai.fileprovider",
                        videoFile);
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentMediaUri);
                takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30); // 30 seconds max
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
            }
        } else {
            Toast.makeText(this, "No video recorder app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setType("image/* video/*");
        String[] mimeTypes = {"image/*", "video/*"};
        pickIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(pickIntent, REQUEST_GALLERY_PICK);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir("Pictures");
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private File createVideoFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String videoFileName = "VIDEO_" + timeStamp + "_";
        File storageDir = getExternalFilesDir("Videos");
        return File.createTempFile(videoFileName, ".mp4", storageDir);
    }

    private void clearCurrentMedia() {
        currentMediaUri = null;
        currentMediaType = null;
        if (currentMediaFile != null && currentMediaFile.exists()) {
            currentMediaFile.delete();
        }
        currentMediaFile = null;
        cardMediaPreview.setVisibility(View.GONE);
        btnAnalyzeMood.setEnabled(false);
        btnAnalyzeMood.setAlpha(0.5f);
    }

    private void showMediaPreview(String mediaType) {
        cardMediaPreview.setVisibility(View.VISIBLE);
        currentMediaType = mediaType;

        // Enable analyze button
        btnAnalyzeMood.setEnabled(true);
        btnAnalyzeMood.setAlpha(1.0f);

        // Update status text and icon based on media type
        switch (mediaType) {
            case "photo":
                tvMediaStatus.setText("Image attached");
                imgMediaTypeIcon.setImageResource(R.drawable.ic_check);
                // Load image into preview
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), currentMediaUri);
                    imgMediaPreview.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case "video":
                tvMediaStatus.setText("Video attached");
                imgMediaTypeIcon.setImageResource(R.drawable.ic_check);
                // For video, show a thumbnail or placeholder
                imgMediaPreview.setImageResource(R.drawable.ic_video_placeholder);
                break;

            case "gallery":
                // Determine if it's image or video from gallery
                String type = getContentResolver().getType(currentMediaUri);
                if (type != null && type.startsWith("video")) {
                    tvMediaStatus.setText("Video selected from gallery");
                    imgMediaPreview.setImageResource(R.drawable.ic_video_placeholder);
                } else {
                    tvMediaStatus.setText("Image selected from gallery");
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), currentMediaUri);
                        imgMediaPreview.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                imgMediaTypeIcon.setImageResource(R.drawable.ic_check);
                break;
        }
    }

    private void analyzeMedia() {
        // TODO: Implement mood analysis
        // This is where you'll send the media to your AI model
        Toast.makeText(this, "Analyzing mood... (Feature coming soon)", Toast.LENGTH_SHORT).show();

        // Navigate to mood analysis results page
        // Intent intent = new Intent(this, MoodAnalysisActivity.class);
        // intent.putExtra("media_uri", currentMediaUri.toString());
        // intent.putExtra("media_type", currentMediaType);
        // startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    // Image captured successfully
                    if (currentMediaUri != null) {
                        showMediaPreview("photo");
                    }
                    break;

                case REQUEST_VIDEO_CAPTURE:
                    // Video recorded successfully
                    if (currentMediaUri != null) {
                        showMediaPreview("video");
                    }
                    break;

                case REQUEST_GALLERY_PICK:
                    // Media picked from gallery
                    if (data != null && data.getData() != null) {
                        currentMediaUri = data.getData();
                        showMediaPreview("gallery");
                    }
                    break;
            }
        } else {
            // User cancelled or error occurred
            clearCurrentMedia();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}