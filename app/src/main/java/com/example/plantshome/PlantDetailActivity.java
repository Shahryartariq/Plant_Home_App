package com.example.plantshome;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.example.plantshome.data.Plant;
import com.example.plantshome.data.PlantCatalog;
import com.example.plantshome.data.PlantPhotos;
import com.example.plantshome.databinding.ActivityDetailBinding;
import com.example.plantshome.util.Session;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * One plant: its name, care notes, and a photo album the user can fill with the camera.
 */
public class PlantDetailActivity extends BaseActivity {

    private static final String EXTRA_PLANT = "plant";

    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());

    private ActivityDetailBinding binding;
    private Plant plant;
    private String uid;

    private final ActivityResultLauncher<Uri> takePhoto = registerForActivityResult(
            new ActivityResultContracts.TakePicture(), this::onPhotoTaken);

    @NonNull
    public static Intent intent(@NonNull Context context, int plantIndex) {
        return new Intent(context, PlantDetailActivity.class).putExtra(EXTRA_PLANT, plantIndex);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        plant = PlantCatalog.get(getIntent().getIntExtra(EXTRA_PLANT, 0));
        if (plant == null) {
            plant = PlantCatalog.all().get(0);
        }
        uid = Session.uid();
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.plantName.setText(plant.name);
        binding.plantIntro.setText(plant.care);
        binding.backToMain.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        binding.camkbtn.setOnClickListener(v -> openCamera());
        showAlbumPhoto();
    }

    private void openCamera() {
        File pending = PlantPhotos.pending(this, uid, plant.index);
        try {
            takePhoto.launch(PlantPhotos.shareableUri(this, pending));
        } catch (ActivityNotFoundException noCamera) {
            toast(R.string.message_no_app_found);
        }
    }

    private void onPhotoTaken(Boolean saved) {
        if (Boolean.TRUE.equals(saved) && PlantPhotos.commit(this, uid, plant.index)) {
            toast(R.string.detail_photo_saved);
            showAlbumPhoto();
        } else {
            PlantPhotos.discardPending(this, uid, plant.index);
        }
    }

    /** Shows the user's photo for this plant, or keeps the default album picture. */
    private void showAlbumPhoto() {
        File photo = PlantPhotos.photo(this, uid, plant.index);
        if (!photo.exists()) {
            return;
        }
        int width = Math.max(getResources().getDisplayMetrics().widthPixels, 1);
        io.execute(() -> {
            Bitmap bitmap = PlantPhotos.load(photo, width);
            main.post(() -> {
                if (isDestroyed() || binding == null) {
                    return;
                }
                if (bitmap == null) {
                    toast(R.string.detail_photo_failed);
                    return;
                }
                ImageView album = binding.photoAlbum;
                album.setScaleType(ImageView.ScaleType.CENTER_CROP);
                album.setImageBitmap(bitmap);
                album.setContentDescription(getString(R.string.detail_album_photo, getString(plant.name)));
            });
        });
    }

    @Override
    protected void onDestroy() {
        io.shutdownNow();
        super.onDestroy();
    }
}
