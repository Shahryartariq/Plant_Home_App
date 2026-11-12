package com.example.plantshome;

import android.os.Bundle;
import android.view.View;

import com.example.plantshome.databinding.ActivitySignupBinding;
import com.example.plantshome.util.AppNavigator;
import com.example.plantshome.util.AuthErrors;
import com.example.plantshome.util.Forms;
import com.example.plantshome.util.InputValidator;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignupActivity extends BaseActivity {

    private ActivitySignupBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();

        binding.backToMain.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        binding.registerUser.setOnClickListener(v -> register());
        binding.btnfb.setOnClickListener(v -> toast(R.string.message_coming_soon));
        binding.btngo.setOnClickListener(v -> toast(R.string.message_coming_soon));
    }

    private void register() {
        String fullName = Forms.text(binding.fullName);
        String email = Forms.text(binding.email);
        String password = Forms.raw(binding.password);
        String confirmation = Forms.raw(binding.confirmPassword);

        boolean valid = Forms.check(binding.fullName, InputValidator.required(fullName),
                R.string.error_full_name_required)
                && Forms.check(binding.email, InputValidator.email(email), R.string.error_email_required)
                && Forms.check(binding.password, InputValidator.newPassword(password),
                R.string.error_password_required)
                && Forms.check(binding.confirmPassword, InputValidator.confirmation(password, confirmation),
                R.string.error_password_required);
        if (!valid) {
            return;
        }

        setLoading(true);
        auth.createUserWithEmailAndPassword(email, password)
                .onSuccessTask(result -> {
                    FirebaseUser user = result.getUser();
                    if (user == null) {
                        return Tasks.<Void>forException(new IllegalStateException("No user after sign up"));
                    }
                    UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                            .setDisplayName(fullName)
                            .build();
                    return user.updateProfile(profile)
                            .onSuccessTask(unused -> user.sendEmailVerification());
                })
                .addOnSuccessListener(this, unused ->
                        AppNavigator.startFresh(this, VerifyEmailActivity.class))
                .addOnFailureListener(this, error -> {
                    setLoading(false);
                    toast(AuthErrors.message(this, error));
                    // The account may exist even if a later step failed; carry on to verification.
                    if (auth.getCurrentUser() != null) {
                        AppNavigator.startFresh(this, VerifyEmailActivity.class);
                    }
                });
    }

    private void setLoading(boolean loading) {
        binding.progessBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.registerUser.setEnabled(!loading);
    }
}
