package com.example.plantshome;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_Login extends AppCompatActivity {
    private Button btnLogin;
    private ImageView btnfb;
    private ImageView btngo;

    private Button register;
    private EditText email;
    private EditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        register = findViewById(R.id.register);
        register.setOnClickListener(v -> NavigatetoSignup());

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

        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().isEmpty()){
                    email.setError("Email is Required!");
                    email.requestFocus();
                    return;
                }

                if(password.getText().toString().isEmpty()){
                    password.setError("Enter Password");
                    password.requestFocus();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
                    email.setError("Please Provide Valid Email");
                    email.requestFocus();
                }
            }
        });

    }
    public void LoginAuth(){
        Intent intent = new Intent(this,Activity_Choices.class);
        startActivity(intent);
    }

    public void NavigatetoSignup() {
        Intent intent1 = new Intent(this, Activity_Signup.class);
        startActivity(intent1);
    }
}
