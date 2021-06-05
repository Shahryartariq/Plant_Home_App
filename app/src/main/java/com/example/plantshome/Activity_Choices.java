package com.example.plantshome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_Choices extends AppCompatActivity {
    private Button btnRegister;
    private ImageButton backbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choices);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

//        backbtn = (ImageButton)findViewById(R.id.backToMain);
//        backbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Navigate();
            }

        });
    }

    public void Navigate(){
        Intent intent = new Intent(this,Activity_Main.class);
        startActivity(intent);
    }
}

