package com.example.plantshome;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_Signup extends AppCompatActivity implements View.OnClickListener {
    private ImageButton backbtn;
    private ImageView btnfb;
    private ImageView btngo;

    private Button registerUser;
    private EditText editTextFullName, editTextEmail, EditTextPassword, EditTextConfirmPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__signup);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        backbtn = (ImageButton)findViewById(R.id.backToMain);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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


        registerUser  = (Button) findViewById(R.id.registerUser );
        registerUser.setOnClickListener(this);

        editTextFullName = (EditText) findViewById(R.id.fullName);
        editTextEmail = (EditText) findViewById(R.id.email);
        EditTextPassword = (EditText) findViewById(R.id.password);
        EditTextConfirmPassword = (EditText) findViewById(R.id.confirmPassword);





    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registerUser:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = EditTextPassword.getText().toString().trim();
        String fullName = editTextFullName.getText().toString().trim();

        if(fullName.isEmpty()){
            editTextFullName.setError("Full Name is Required!");
            editTextFullName.requestFocus();
            return;
        }

        if(email.isEmpty()){
            editTextEmail.setError("Email is Required!");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please Provide Valid Email");
            editTextEmail.requestFocus();
        }

        if(password.isEmpty()){
            EditTextPassword.setError("Password is Required!");
            EditTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            EditTextPassword.setError("Password Must be 6 characters Long!");
            EditTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            EditTextPassword.setError("Password Must be 6 characters Long!");
            EditTextPassword.requestFocus();
            return;
        }

        if (!password.equals(EditTextConfirmPassword)) {
            EditTextPassword.setError("Password donot Match!");
            EditTextPassword.requestFocus();
            return;
    }
}
}
