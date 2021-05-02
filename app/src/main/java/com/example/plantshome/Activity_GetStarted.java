package com.example.plantshome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_GetStarted extends AppCompatActivity {

    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getstarted);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        startButton = findViewById(R.id.buttonGetStart);
        startButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Navigate();
            }

        });
    }

    public void Navigate(){
        Intent intent = new Intent(this,Activity_Login.class);
        startActivity(intent);
    }
}