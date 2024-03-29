package com.infosys.b4b;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class loginActivity extends AppCompatActivity {

    private TextInputEditText LoginEmail;
    private TextInputEditText LoginPass;
    private TextView btnRegisterHere;
    private Button btnLogin;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_MyFirstApp);
        setContentView(R.layout.activity_login);

        // Identify username and password textboxes
        LoginEmail = findViewById(R.id.LoginEmail);
        LoginPass = findViewById(R.id.LoginPass);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegisterHere = findViewById(R.id.btnRegisterHere);
        FirebaseApp.initializeApp(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();

        // Run login function on button click
        btnLogin.setOnClickListener(view -> {
            loginUser();

        });

        btnRegisterHere.setOnClickListener(view ->{
            startActivity(new Intent(loginActivity.this, signupActivity.class));
        });
    }

    private void loginUser(){
        String email = LoginEmail.getText().toString();
        String password = LoginPass.getText().toString();

        // If either email or password textbox is empty
        if (TextUtils.isEmpty(email)){
            LoginEmail.setError("Email cannot be empty");
            LoginEmail.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            LoginPass.setError("Password cannot be empty");
            LoginPass.requestFocus();
        }else{
            // Attempt login with provided credentials
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(loginActivity.this, "User logged in successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(loginActivity.this, mainActivity.class));
                    }else{
                        // Display whatever error produced by Firebase as a toast
                        Toast.makeText(loginActivity.this, "Log in Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }



}
