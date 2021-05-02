package com.example.plantshome;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_Detail extends AppCompatActivity {

    private ImageButton backbtn;
    private ImageButton camkbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        backbtn = (ImageButton)findViewById(R.id.backToMain);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        camkbtn = (ImageButton)findViewById(R.id.camkbtn);
        camkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenCam();
            }
        });

    }

    private void OpenCam() {
        Toast.makeText(this, "Open Camera ...", Toast.LENGTH_SHORT).show();

    }

}
