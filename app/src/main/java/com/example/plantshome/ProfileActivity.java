package com.example.plantshome;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;

import com.example.plantshome.data.CareLog;
import com.example.plantshome.data.PlantPhotos;
import com.example.plantshome.databinding.ActivityProfileBinding;
import com.example.plantshome.databinding.ResetPopupBinding;
import com.example.plantshome.util.AppNavigator;
import com.example.plantshome.util.AuthErrors;
import com.example.plantshome.util.Forms;
import com.example.plantshome.util.InputValidator;
import com.example.plantshome.util.OnboardingPrefs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileActivity extends BaseActivity {

    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());

    private ActivityProfileBinding binding;

    /** Tapping the profile picture opens the system photo picker (no storage permission needed). */
    private final ActivityResultLauncher<PickVisualMediaRequest> pickPhoto = registerForActivityResult(
            new ActivityResultContracts.PickVisualMedia(), this::onPhotoPicked);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (currentUser() == null) {
            AppNavigator.signOut(this);
            return;
        }
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.updateEmailRow.setOnClickListener(v -> showUpdateEmailDialog());
        binding.resetPasswordRow.setOnClickListener(v ->
                startActivity(new Intent(this, ResetPasswordActivity.class)));
        binding.deleteAccountRow.setOnClickListener(v -> confirmDeleteAccount());
        binding.userProfilePhoto.setOnClickListener(v -> pickPhoto.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build()));
        showProfilePhoto();
    }

    private void onPhotoPicked(Uri uri) {
        FirebaseUser user = currentUser();
        if (uri == null || user == null) {
            return;
        }
        File target = PlantPhotos.profilePhoto(this, user.getUid());
        io.execute(() -> {
            boolean saved = PlantPhotos.copy(this, uri, target);
            main.post(() -> {
                if (saved) {
                    showProfilePhoto();
                } else if (!isDestroyed()) {
                    toast(R.string.detail_photo_failed);
                }
            });
        });
    }

    private void showProfilePhoto() {
        FirebaseUser user = currentUser();
        if (user == null) {
            return;
        }
        File photo = PlantPhotos.profilePhoto(this, user.getUid());
        if (!photo.exists()) {
            return;
        }
        int size = Math.max(binding.userProfilePhoto.getLayoutParams().width, 256);
        io.execute(() -> {
            Bitmap bitmap = PlantPhotos.load(photo, size);
            main.post(() -> {
                if (bitmap != null && !isDestroyed() && binding != null) {
                    binding.userProfilePhoto.setImageBitmap(bitmap);
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        io.shutdownNow();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showUser();
    }

    private void showUser() {
        FirebaseUser user = currentUser();
        if (user == null || binding == null) {
            return;
        }
        String name = TextUtils.isEmpty(user.getDisplayName()) ? "-" : user.getDisplayName();
        String email = TextUtils.isEmpty(user.getEmail()) ? "-" : user.getEmail();
        binding.profileName.setText(getString(R.string.profile_name, name));
        binding.profileEmail.setText(getString(R.string.profile_email, email));
    }

    private void showUpdateEmailDialog() {
        ResetPopupBinding popup = ResetPopupBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_update_email_title)
                .setMessage(R.string.dialog_update_email_message)
                .setView(popup.getRoot())
                .setPositiveButton(R.string.dialog_update_email_positive, null)
                .setNegativeButton(R.string.dialog_cancel, null)
                .create();

        dialog.setOnShowListener(shown -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    String newEmail = Forms.text(popup.resetEmailPop);
                    if (!Forms.check(popup.resetEmailPop, InputValidator.email(newEmail),
                            R.string.error_email_required)) {
                        return;
                    }
                    FirebaseUser user = currentUser();
                    if (user == null) {
                        AppNavigator.signOut(this);
                        return;
                    }
                    // Firebase only switches the email after the user opens the link sent to it.
                    user.verifyBeforeUpdateEmail(newEmail)
                            .addOnSuccessListener(this, unused -> toast(R.string.message_email_update_sent))
                            .addOnFailureListener(this, this::handleSensitiveActionError);
                    dialog.dismiss();
                }));
        dialog.show();
    }

    private void confirmDeleteAccount() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_delete_message)
                .setPositiveButton(R.string.dialog_delete_positive, (dialog, which) -> deleteAccount())
                .setNegativeButton(R.string.dialog_cancel, null)
                .show();
    }

    private void deleteAccount() {
        FirebaseUser user = currentUser();
        if (user == null) {
            AppNavigator.signOut(this);
            return;
        }
        String uid = user.getUid();
        binding.deleteAccountRow.setEnabled(false);
        user.delete()
                .addOnSuccessListener(this, unused -> {
                    OnboardingPrefs.clear(this, uid);
                    CareLog.clear(this, uid);
                    PlantPhotos.deleteAll(this, uid);
                    toast(R.string.message_account_deleted);
                    AppNavigator.signOut(this);
                })
                .addOnFailureListener(this, error -> {
                    binding.deleteAccountRow.setEnabled(true);
                    handleSensitiveActionError(error);
                });
    }

    /** Email changes and account deletion need a fresh login; send the user to log in again. */
    private void handleSensitiveActionError(Exception error) {
        toast(AuthErrors.message(this, error));
        if (AuthErrors.needsRecentLogin(error)) {
            AppNavigator.signOut(this);
        }
    }

    private static FirebaseUser currentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}
