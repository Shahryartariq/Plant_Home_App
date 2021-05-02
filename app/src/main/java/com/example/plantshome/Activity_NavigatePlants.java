package com.example.plantshome;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_NavigatePlants extends AppCompatActivity {
    private Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigateplants);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

    }

}

