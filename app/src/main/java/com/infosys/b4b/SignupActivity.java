package com.infosys.b4b;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText RegEmail;
    private TextInputEditText RegPass;
    private Button btnRegister;
    private TextView btnLoginHere;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        RegEmail = findViewById(R.id.RegEmail);
        RegPass = findViewById(R.id.RegPass);
        btnRegister = findViewById(R.id.btnRegister);
        btnLoginHere = findViewById(R.id.btnLoginHere);
        FirebaseApp.initializeApp(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(view ->{
            createUser();
        });
        btnLoginHere.setOnClickListener(view ->{
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        });
    }

    private void createUser(){
        String email = RegEmail.getText().toString();
        String password = RegPass.getText().toString();

        if (TextUtils.isEmpty(email)){
            RegEmail.setError("Email cannot be empty");
            RegEmail.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            RegPass.setError("Password cannot be empty");
            RegPass.requestFocus();
        }else{
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(SignupActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                    }else{
                        Toast.makeText(SignupActivity.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}