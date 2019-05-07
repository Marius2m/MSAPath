package com.example.marius.path;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_REGISTER_ACTIVITY = 101;
    FirebaseAuth auth;
//    private FirebaseDatabase database;
//    private DatabaseReference databaseReference;

    Button login_btn;
    EditText userEmail, userPassword;
    TextView register_txt;
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
        register_txt = findViewById(R.id.register_txt);

        styleRegisterTxtView();

        userEmail = findViewById(R.id.regMail);
        userPassword = findViewById(R.id.regPassword);
        progressBar = findViewById(R.id.progressBar);
    }

    private void styleRegisterTxtView() {
        String registerText = "Don't have an account?  Register now";
        SpannableString ss = new SpannableString(registerText);
        ForegroundColorSpan fcsPrimaryClr = new ForegroundColorSpan(Color.parseColor("#FF3F51B5"));
        ClickableSpan registerClick = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(intent, REQUEST_CODE_REGISTER_ACTIVITY);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(fcsPrimaryClr,24, registerText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new StyleSpan(Typeface.BOLD), 24, registerText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(registerClick, 24, registerText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        register_txt.setText(ss);
        register_txt.setHighlightColor(Color.TRANSPARENT);
        register_txt.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_REGISTER_ACTIVITY) {
            if(resultCode == RESULT_OK) {
//                String email = data.getStringExtra("email");
//                userEmail.setText(email);
                Intent intent = getIntent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }
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

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);

                if(task.isSuccessful()){
                    Intent intent = getIntent();
                    setResult(RESULT_OK, intent);
                    finish();
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
