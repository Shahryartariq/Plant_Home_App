package com.example.plantshome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class ResetPassword extends AppCompatActivity {
    EditText userPassword, userConfPassword;
    Button resetUserBtn;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userConfPassword = findViewById(R.id.newConfirmPass);
        userPassword = findViewById(R.id.newUserPassword);
        resetUserBtn = findViewById(R.id.resetPasswordBtn);
        resetUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userPassword.getText().toString().isEmpty()) {
                    userPassword.setError("Field Required");
                    return;
                }
                if (userConfPassword.getText().toString().isEmpty()) {
                    userConfPassword.setError("Field Required");
                    return;
                }
                if (!userPassword.getText().toString().equals(userConfPassword.getText().toString())) {
                    userConfPassword.setError("Password doesn't match!");
                    return;
                }
                //--------------------------------- Connecting to Firebase ----------------------------------------
                user.updatePassword(userPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ResetPassword.this, "Password Updated!", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(getApplicationContext(), Activity_VerifyEmail.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(ResetPassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });


    }
}