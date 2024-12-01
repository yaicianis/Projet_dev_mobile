package com.android.todo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private EditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button signUpButton;
    private TextView loginText;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.editTextTextPassword2);
        signUpButton = findViewById(R.id.SignBtn);
        loginText = findViewById(R.id.LoginTxt);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                if (username.isEmpty()) {
                    usernameEditText.setError("Username is required");
                    return;
                }

                if (email.isEmpty()) {
                    emailEditText.setError("Email is required");
                    return;
                }

                if (password.isEmpty()) {
                    passwordEditText.setError("Password is required");
                    return;
                }

                if (confirmPassword.isEmpty()) {
                    confirmPasswordEditText.setError("Please confirm your password");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    confirmPasswordEditText.setError("Passwords do not match");
                    return;
                }

                RegisterUser(username, email, password);
            }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void RegisterUser(String username, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if(user != null) {
                                saveUserToFirestore(user.getUid(), username);
                            }
                        } else {
                            Toast.makeText(SignupActivity.this, "Registration failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void saveUserToFirestore(String userId, String username) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", emailEditText.getText().toString().trim());

        db.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(SignupActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();

                        Log.d("FirestoreSave", "onSuccess: User data saved to Firestore");
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignupActivity.this, "Failed to save user data: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}