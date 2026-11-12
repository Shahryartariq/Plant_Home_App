package com.example.plantshome;

import android.os.Bundle;

import com.example.plantshome.databinding.ActivityVerifyemailBinding;
import com.example.plantshome.util.AppNavigator;
import com.example.plantshome.util.AuthErrors;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Gate between sign in and the app: the user stays here until their email is verified.
 */
public class VerifyEmailActivity extends BaseActivity {

    private ActivityVerifyemailBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            AppNavigator.signOut(this);
            return;
        }
        binding = ActivityVerifyemailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.logOut.setOnClickListener(v -> AppNavigator.signOut(this));
        binding.verifyBtn.setOnClickListener(v -> sendVerificationEmail());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The user verifies in their email app, then comes back: refresh and move on.
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            return;
        }
        user.reload().addOnSuccessListener(this, unused -> {
            FirebaseUser fresh = auth.getCurrentUser();
            if (fresh != null && fresh.isEmailVerified()) {
                AppNavigator.routeSignedInUser(this);
            }
        });
    }

    private void sendVerificationEmail() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            AppNavigator.signOut(this);
            return;
        }
        binding.verifyBtn.setEnabled(false);
        user.sendEmailVerification()
                .addOnSuccessListener(this, unused -> toast(R.string.message_verification_sent))
                .addOnFailureListener(this, error -> toast(AuthErrors.message(this, error)))
                .addOnCompleteListener(this, task -> binding.verifyBtn.setEnabled(true));
    }
}
