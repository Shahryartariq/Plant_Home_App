package com.example.plantshome;

import android.os.Bundle;

import com.example.plantshome.databinding.ActivityDetailBinding;

public class PlantDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailBinding binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backToMain.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        binding.camkbtn.setOnClickListener(v -> toast(R.string.message_coming_soon));
    }
}
