package com.example.westyorkshirecrimeapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import android.content.Intent;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    // 1. Declare variables
    private EditText emailInput, passwordInput;
    private Button loginButton, registerButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Database Reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // 2. Link Java variables to XML IDs
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        // 3. Register Logic
        registerButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                registerUser(email, password);
            } else {
                Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        // 4. Login Logic
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                loginUser(email, password);
            }
        });
    }

    // LIFECYCLE METHOD: This runs every time the Activity starts
    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is already signed in (non-null)
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish(); // Close MainActivity so user can't go back to login
        }
    }

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();

                        // Create user with default "user" role
                        User newUser = new User("New User", email, "user");

                        mDatabase.child("Users").child(userId).setValue(newUser);

                        Toast.makeText(MainActivity.this, "Police Account Created!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}