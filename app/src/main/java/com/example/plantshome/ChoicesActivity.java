package com.example.plantshome;

import android.os.Bundle;

import com.example.plantshome.databinding.ActivityChoicesBinding;
import com.example.plantshome.util.AppNavigator;
import com.example.plantshome.util.OnboardingPrefs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChoicesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityChoicesBinding binding = ActivityChoicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnRegister.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                AppNavigator.signOut(this);
                return;
            }
            OnboardingPrefs.setChoicesDone(this, user.getUid());
            AppNavigator.startFresh(this, MainActivity.class);
        });
    }
}
