package com.infosys.b4b;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signupActivity extends AppCompatActivity {

    private TextInputEditText RegEmail;
    private TextInputEditText RegPass;
    private TextInputEditText Username;
    private Button btnRegister;
    private TextView btnLoginHere;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private static final String USER = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Identify email, username and password textboxes
        RegEmail = findViewById(R.id.RegEmail);
        RegPass = findViewById(R.id.RegPass);
        Username = findViewById(R.id.Username);
        btnRegister = findViewById(R.id.btnRegister);
        btnLoginHere = findViewById(R.id.btnLoginHere);
        FirebaseApp.initializeApp(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();

        // Create DAO object which will be used to upload userdata
        daoUserData dao = new daoUserData();

        // Run register function on button click
        btnRegister.setOnClickListener(view ->{
            createUser();
        });
        btnLoginHere.setOnClickListener(view ->{                                                   // Redirect to login page if user clicks that instead
            startActivity(new Intent(signupActivity.this, loginActivity.class));
        });
    }

    private void createUser(){
        String email = RegEmail.getText().toString();
        String password = RegPass.getText().toString();
        String username = Username.getText().toString();

        //if either email, password or username textboxes is empty
        if (TextUtils.isEmpty(email)){
            RegEmail.setError("Email cannot be empty");
            RegEmail.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            RegPass.setError("Password cannot be empty");
            RegPass.requestFocus();
        }else if (TextUtils.isEmpty(username)){
            RegPass.setError("Username cannot be empty");
            RegPass.requestFocus();
        }
        else{
            // Attempt to create new user with provided credentials
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(signupActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        String id = user.getUid();
                        writeNewUser(email, username, id);

                        startActivity(new Intent(signupActivity.this, mainActivity.class));
                    }else{
                        // Display whatever error produced by Firebase as a toast
                        Toast.makeText(signupActivity.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    //Use the dao object to write new userdata into realtime database
    public void writeNewUser(String email, String username, String id) {
        daoUserData dao = new daoUserData();
        userData user = new userData(email, username, id);
        dao.add(user);

    }

}