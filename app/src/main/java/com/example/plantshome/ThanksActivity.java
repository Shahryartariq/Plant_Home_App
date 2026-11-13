package com.example.plantshome;

import android.content.Intent;
import android.os.Bundle;

import com.example.plantshome.databinding.ActivityThanksBinding;

public class ThanksActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityThanksBinding binding = ActivityThanksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Return to the existing home screen instead of stacking a new one.
        binding.gotoHome.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
            finish();
        });
    }
}
