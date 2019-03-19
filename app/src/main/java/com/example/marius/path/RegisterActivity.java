package com.example.marius.path;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.marius.path.user_data.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {
    private EditText regName, regMail, regPassword, regPassword2;
    private ImageView selectAvatarPicture;
    private Button regBtn;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        regName = (EditText) findViewById(R.id.regName);
        regMail = (EditText) findViewById(R.id.regMail);
        regPassword = (EditText) findViewById(R.id.regPassword);
        regPassword2 = (EditText) findViewById(R.id.regPassword2);

        regBtn = (Button) findViewById(R.id.login_btn);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        selectAvatarPicture = findViewById(R.id.selectAvatarPicture);
        selectAvatarPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("TAGG", " click");

            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser(){
        final String name = regName.getText().toString();
        final String email = regMail.getText().toString().trim();
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
                    FirebaseUser user = auth.getCurrentUser();
                    userId = user.getUid();

                    DateFormat dateF = new SimpleDateFormat("d MMM yyyy");
                    String date = dateF.format(Calendar.getInstance().getTime());

                    UserAccount userAcc = new UserAccount(name, date, email);
                    databaseReference.child("users").child(userId).setValue(userAcc);
                    Log.d("failTag", "fail" + name + " - " + date + " - " + email);
                    Log.d("userId", "userId" + userId);
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
