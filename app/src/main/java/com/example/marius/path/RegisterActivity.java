package com.example.marius.path;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class RegisterActivity extends AppCompatActivity {
    private EditText regName, regMail, regPassword, regPassword2;
    private Button regBtn;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        regName = (EditText) findViewById(R.id.regName);
        regMail = (EditText) findViewById(R.id.regMail);
        regPassword = (EditText) findViewById(R.id.regPassword);
        regPassword2 = (EditText) findViewById(R.id.regPassword2);

        regBtn = (Button) findViewById(R.id.login_btn);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser(){
        String name = regName.getText().toString();
        String email = regMail.getText().toString().trim();
        String password = regPassword.getText().toString().trim();
        String checkPassword = regPassword2.getText().toString().trim();

        if(name.isEmpty()){
            regName.setError("A nickname is required.");
            regName.requestFocus();
            return;
        }
        if(email.isEmpty()){
            regMail.setError("An email is required.");
            regMail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            regMail.setError("A valid email is required.");
            regMail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            regPassword.setError("A password is required.");
            regPassword.requestFocus();
            return;
        }
        if(checkPassword.isEmpty()){
            regPassword2.setError("Re-type your password.");
            regPassword2.requestFocus();
            return;
        }
        if(password.length() < 6){
            regPassword.setError("Please enter a password of length 6 or more.");
            regPassword.requestFocus();
            return;
        }
        if(!checkPassword.equals(password)){
            regPassword.setError("The two password must match.");
            regPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);

                if(task.isSuccessful()){


                    Intent intent = new Intent(RegisterActivity.this, ProfileActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //finish();
                    startActivity(intent);

                    //Toast.makeText(getApplicationContext(), "Registration successfull", Toast.LENGTH_SHORT).show();
                }else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "A user with this email has already registered.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
