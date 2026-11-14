package com.example.plantshome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.plantshome.auth.GoogleSignIn;
import com.example.plantshome.databinding.ActivityLoginBinding;
import com.example.plantshome.databinding.ResetPopupBinding;
import com.example.plantshome.util.AppNavigator;
import com.example.plantshome.util.AuthErrors;
import com.example.plantshome.util.Forms;
import com.example.plantshome.util.InputValidator;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();

        binding.btnLogin.setOnClickListener(v -> signIn());
        binding.password.setOnEditorActionListener((field, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                signIn();
                return true;
            }
            return false;
        });
        binding.register.setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class)));
        binding.forgotPassword.setOnClickListener(v -> showResetPasswordDialog());
        binding.btnfb.setOnClickListener(v -> toast(R.string.message_coming_soon));
        binding.btngo.setOnClickListener(v -> signInWithGoogle());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            AppNavigator.routeSignedInUser(this);
        }
    }

    private void signIn() {
        String email = Forms.text(binding.email);
        String password = Forms.raw(binding.password);

        if (!Forms.check(binding.email, InputValidator.email(email), R.string.error_email_required)
                || !Forms.check(binding.password, InputValidator.existingPassword(password),
                R.string.error_password_required)) {
            return;
        }

        setLoading(true);
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(this, result -> AppNavigator.routeSignedInUser(this))
                .addOnFailureListener(this, error -> {
                    setLoading(false);
                    toast(AuthErrors.message(this, error));
                });
    }

    private void signInWithGoogle() {
        setLoading(true);
        GoogleSignIn.signIn(this, new GoogleSignIn.Callback() {
            @Override
            public void onSignedIn() {
                AppNavigator.routeSignedInUser(LoginActivity.this);
            }

            @Override
            public void onFailed(@Nullable String message) {
                setLoading(false);
                if (message != null) {
                    toast(message);
                }
            }
        });
    }

    private void showResetPasswordDialog() {
        ResetPopupBinding popup = ResetPopupBinding.inflate(getLayoutInflater());
        popup.resetEmailPop.setText(Forms.text(binding.email));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_reset_title)
                .setMessage(R.string.dialog_reset_message)
                .setView(popup.getRoot())
                .setPositiveButton(R.string.dialog_reset_positive, null)
                .setNegativeButton(R.string.dialog_cancel, null)
                .create();

        // Override the positive button so the dialog stays open when the email is invalid.
        dialog.setOnShowListener(shown -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    String email = Forms.text(popup.resetEmailPop);
                    if (!Forms.check(popup.resetEmailPop, InputValidator.email(email),
                            R.string.error_email_required)) {
                        return;
                    }
                    auth.sendPasswordResetEmail(email)
                            .addOnSuccessListener(this, unused -> toast(R.string.message_reset_email_sent))
                            .addOnFailureListener(this, error -> toast(AuthErrors.message(this, error)));
                    dialog.dismiss();
                }));
        dialog.show();
    }

    private void setLoading(boolean loading) {
        binding.progessBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!loading);
    }
}
