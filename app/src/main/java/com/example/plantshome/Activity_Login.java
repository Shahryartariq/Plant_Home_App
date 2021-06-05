package com.example.plantshome;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class Activity_Login extends AppCompatActivity {
    private Button btnLogin;
    private ImageView btnfb;
    private ImageView btngo;

    private Button register;
    private Button forgotPassword;
    private EditText email;
    private EditText password;

    AlertDialog.Builder reset_user;
    LayoutInflater inflater;


    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        firebaseAuth = FirebaseAuth.getInstance();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //       Forget Password Start
        reset_user = new AlertDialog.Builder(this);
        inflater= this.getLayoutInflater();

        forgotPassword= findViewById(R.id.forgotPassword);
//                Listener

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start alertDialog
                View view= inflater.inflate(R.layout.reset_popup, null);
                reset_user.setTitle("Reset Forgot Password?").setMessage("Enter your email to get your new Password!").
                        setPositiveButton("Reset", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // first Validate the email address ----------------------------------------------------------------
                                EditText email= view.findViewById(R.id.resetEmailPop);
                                if(email.getText().toString().isEmpty()){
                                    email.setError("Email is not here!");
                                    return;
                                }
                                //send Reset Link---------------------------------------------
                                firebaseAuth.sendPasswordResetEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(Activity_Login.this, "Email Sent 🙂🙂🙂 ", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        Toast.makeText(Activity_Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", null).setView(view)
                        .create().show();
            }
        });

        //        Forget Password End



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

                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                // Log in is successful
                                startActivity(new Intent(getApplicationContext(), Activity_VerifyEmail.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {

                        Toast.makeText(Activity_Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

    }

    public void NavigatetoSignup() {
        Intent intent1 = new Intent(this, Activity_Signup.class);
        startActivity(intent1);
    }

    @Override
    protected void onStart() {

        super.onStart();
        // if our user is existed before then it will be true otherwise it wil redirect him to the register pgae.
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            startActivity(new Intent(getApplicationContext(), Activity_VerifyEmail.class));// user goes after logged in.
            finish();
        }
    }
}
