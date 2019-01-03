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

import com.example.marius.path.user_data.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth auth;
//    private FirebaseDatabase database;
//    private DatabaseReference databaseReference;

    Button login_btn;
    EditText userEmail, userPassword;
    ProgressBar progressBar;
//    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        auth = FirebaseAuth.getInstance();
//        database = FirebaseDatabase.getInstance();
//        databaseReference = database.getReference();


        findViewById(R.id.login_btn).setOnClickListener(this);

        userEmail = (EditText) findViewById(R.id.regMail);
        userPassword = (EditText) findViewById(R.id.regPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void userLogIn(){
        String email = userEmail.getText().toString().trim();
        String password = userPassword.getText().toString().trim();

        if(email.isEmpty()){
            userEmail.setError("An email is required.");
            userEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            userEmail.setError("A valid email is required.");
            userEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            userPassword.setError("A password is required.");
            userPassword.requestFocus();
            return;
        }
        if(password.length() < 6){
            userPassword.setError("Please enter a password of length 6 or more.");
            userPassword.requestFocus();
            return;
        }


        progressBar.setVisibility(View.VISIBLE);

        final String allData = email + password;
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);

                if(task.isSuccessful()){
                    //Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LogInActivity.this, ProfileActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //clears the activity stack
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch( v.getId()){
            case R.id.login_btn:
                userLogIn();
                break;
        }
    }
}
