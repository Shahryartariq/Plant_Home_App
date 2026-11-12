package com.example.plantshome;

import android.content.Intent;
import android.os.Bundle;

import com.example.plantshome.databinding.ActivityGetstartedBinding;

public class GetStartedActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityGetstartedBinding binding = ActivityGetstartedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonGetStart.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));
    }
}
