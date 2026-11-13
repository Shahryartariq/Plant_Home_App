package com.example.plantshome;

import android.os.Bundle;

import com.example.plantshome.databinding.ActivityResetPasswordBinding;
import com.example.plantshome.util.AppNavigator;
import com.example.plantshome.util.AuthErrors;
import com.example.plantshome.util.Forms;
import com.example.plantshome.util.InputValidator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Lets a signed-in user set a new password.
 */
public class ResetPasswordActivity extends BaseActivity {

    private ActivityResetPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            AppNavigator.signOut(this);
            return;
        }
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.resetPasswordBtn.setOnClickListener(v -> updatePassword());
    }

    private void updatePassword() {
        String password = Forms.raw(binding.newUserPassword);
        String confirmation = Forms.raw(binding.newConfirmPass);
        boolean valid = Forms.check(binding.newUserPassword, InputValidator.newPassword(password),
                R.string.error_password_required)
                && Forms.check(binding.newConfirmPass, InputValidator.confirmation(password, confirmation),
                R.string.error_password_required);
        if (!valid) {
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            AppNavigator.signOut(this);
            return;
        }
        binding.resetPasswordBtn.setEnabled(false);
        user.updatePassword(password)
                .addOnSuccessListener(this, unused -> {
                    toast(R.string.message_password_updated);
                    finish();
                })
                .addOnFailureListener(this, error -> {
                    binding.resetPasswordBtn.setEnabled(true);
                    toast(AuthErrors.message(this, error));
                    if (AuthErrors.needsRecentLogin(error)) {
                        AppNavigator.signOut(this);
                    }
                });
    }
}
