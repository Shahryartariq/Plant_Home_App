package com.example.plantshome;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_Login extends AppCompatActivity {
    private Button btnLogin;
    private ImageView btnfb;
    private ImageView btngo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Navigate();
            }

        });

        btnfb = findViewById(R.id.btnfb);
        btnfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url="https://www.facebook.com/";
                Intent intentfb =new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intentfb);
            }
        });

        btngo = findViewById(R.id.btngo);
        btngo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url="https://accounts.google.com/servicelogin";
                Intent intentgo =new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intentgo);
            }
        });

    }
    public void Navigate(){
        Intent intent = new Intent(this,Activity_Choices.class);
        startActivity(intent);
    }
}
