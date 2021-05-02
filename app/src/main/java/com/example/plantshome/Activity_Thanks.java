package com.example.plantshome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_Thanks extends AppCompatActivity {
    private Button gotoHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanks);

        gotoHome = findViewById(R.id.gotoHome);
        gotoHome.setOnClickListener(new View.OnClickListener(){
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
