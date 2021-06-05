package com.example.plantshome;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class Activity_VerifyEmail extends AppCompatActivity {
    TextView verifyMsg;
    Button verifyBtn, logOut;
    AlertDialog.Builder reset_user;
    LayoutInflater inflater;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifyemail);

//        if (getSupportActionBar() != null) {
//            getSupportActionBar().hide();
//        }

        auth = FirebaseAuth.getInstance();
        inflater=this.getLayoutInflater();
        verifyMsg = findViewById(R.id.verifyEmailMsg);
        reset_user =new AlertDialog.Builder(this);
        verifyBtn = findViewById(R.id.verifyBtn);
        logOut = findViewById(R.id.logOut);
// verifying the email-------------------------------------------------------------------------------------------------------

        if (auth.getCurrentUser().isEmailVerified()==true) {
            Navigate();

        }

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(getApplicationContext(), Activity_Login.class));
                finish();
            }
        });
//verify the email by sending verification email.----------------------------------------------------------------------------

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Activity_VerifyEmail.this, "Email sent! Check Email And Comeback", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), Activity_Login.class));
                        finish();

                    }
                });
            }
        });

    }


//    ///////----------------------------------------- Reset Password----------------------------------------
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.option_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if (item.getItemId() == R.id.resetUserPassword) {
//
//            startActivity(new Intent(getApplicationContext(), ResetPassword.class));
//        }
//        if (item.getItemId() == R.id.updateEmailMenu) {
//            //start alertDialog
//
//            View view = inflater.inflate(R.layout.reset_popup,null);
//            reset_user.setTitle("Update Email").setMessage("Enter your email Address")
//                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // first Validate the email address ----------------------------------------------------------------
//                            EditText email = view.findViewById(R.id.resetEmailPop);
//                            if (email.getText().toString().isEmpty()) {
//                                email.setError("Email is not here!");
//                                return;
//                            }
//                            //send Reset Link---------------------------------------------
//                            FirebaseUser user= auth.getCurrentUser();
//                            user.updateEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//                                    Toast.makeText(Activity_VerifyEmail.this, "Email Updated!", Toast.LENGTH_SHORT).show();
//
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull @NotNull Exception e) {
//
//                                    Toast.makeText(Activity_VerifyEmail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//
//                                }
//                            });
//
//
//                        }
//                    }).setNegativeButton("Cancel", null).setView(view)
//                    .create().show();
//        }
//
//
//        //======================================== Delete the User Account=================================
//        if(item.getItemId()==R.id.delete_user_menu){
//            reset_user.setTitle("Delete Account Permanently!").setMessage("Are you Sure?").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//
//                    FirebaseUser user= auth.getCurrentUser();
//                    user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void unused) {
//                            Toast.makeText(Activity_VerifyEmail.this, "Account Deleted", Toast.LENGTH_SHORT).show();
//                            auth.signOut();
//                            startActivity(new Intent(getApplicationContext(), Activity_Login.class));
//                            finish();
//
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull @NotNull Exception e) {
//                            Toast.makeText(Activity_VerifyEmail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//
//                        }
//                    });
//                }
//            }).setNegativeButton("Cancel", null).create().show();
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    public void Navigate(){
        Intent intent = new Intent(this,Activity_Choices.class);
        startActivity(intent);
    }






}