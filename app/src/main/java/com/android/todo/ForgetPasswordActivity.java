package com.android.todo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    private Button sendRequestButton;
    private EditText editEmail;
    private TextView backBtn;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        sendRequestButton = findViewById(R.id.SendRequestBtn);
        editEmail = findViewById(R.id.email);
        backBtn = findViewById(R.id.backBtn);

        mAuth = FirebaseAuth.getInstance();

        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString().trim();
                if(!TextUtils.isEmpty(email)){
                    ResetPassword(email);
                } else editEmail.setError("Email field can't be empty!");
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }

    private void ResetPassword(String email) {
        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ForgetPasswordActivity.this, "Reset password link has been sent to your e-mail!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ForgetPasswordActivity.this, "Error :- " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}