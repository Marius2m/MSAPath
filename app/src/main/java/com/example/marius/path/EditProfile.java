package com.example.marius.path;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfile extends AppCompatActivity implements View.OnClickListener {

    // Firebase
    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;

    String userKey;

    private TextView edit_profile_title, reset_password;
    private EditText current_name, new_name, current_password, new_password, confirm_password, current_email, new_email, confirm_password_email;
    private Button change_name_btn, change_password_btn, change_email_btn;
    private LinearLayout change_profile_ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userKey = firebaseUser.getUid();

        setViews();
    }

    private void setViews() {
        String activityType = getIntent().getStringExtra("title");

        edit_profile_title = findViewById(R.id.edit_profile_title);
        edit_profile_title.append(activityType);

        // name
        current_name = findViewById(R.id.current_name);
        current_name.setFocusable(false);
        new_name = findViewById(R.id.new_name);
        change_name_btn = findViewById(R.id.change_name_btn);
        change_name_btn.setOnClickListener(this);

        // email
        current_email = findViewById(R.id.current_email);
        current_email.setFocusable(false);
        new_email = findViewById(R.id.new_email);
        change_email_btn = findViewById(R.id.change_email_btn);
        change_email_btn.setOnClickListener(this);
        confirm_password_email = findViewById(R.id.confirm_password_email);

        // password
        current_password = findViewById(R.id.current_password);
        new_password = findViewById(R.id.new_password);
        confirm_password = findViewById(R.id.confirm_password);
        change_password_btn = findViewById(R.id.change_password_btn);
        change_password_btn.setOnClickListener(this);
        reset_password = findViewById(R.id.reset_password);
        reset_password.setOnClickListener(this);

        if (activityType.equals("Name")) {
            current_name.setText(getIntent().getStringExtra("name"));
            change_profile_ll = findViewById(R.id.change_name_ll);
        } else if (activityType.equals("Email")) {
            current_email.setText(getIntent().getStringExtra("email"));
            change_profile_ll = findViewById(R.id.change_email_ll);
        } else if(activityType.equals("Password")) {
            change_profile_ll = findViewById(R.id.change_password_ll);
        }
        change_profile_ll.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_name_btn:
                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    Log.d("keyboard", "Failed to close keyboard");
                }
                changeName();
                break;

            case R.id.change_email_btn:
                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    Log.d("keyboard", "Failed to close keyboard");
                }
                changeEmail();
                break;

            case R.id.change_password_btn:
                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    Log.d("keyboard", "Failed to close keyboard");
                }
                Toast.makeText(this, "change_password_btn", Toast.LENGTH_LONG).show();
                finish();
                break;

            case R.id.reset_password:
                Toast.makeText(this, "reset_password", Toast.LENGTH_LONG).show();
                finish();
                break;

            default:
                break;
        }
    }

    private void changeEmail() {
        final String email = new_email.getText().toString();
        final String password = confirm_password_email.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            new_email.setError("A valid email is required.");
            new_email.requestFocus();
            return;
        }

        if(password.isEmpty()){
            confirm_password_email.setError("A password is required.");
            confirm_password_email.requestFocus();
            return;
        }

        if (!firebaseUser.getEmail().equals(email)) {
            AuthCredential credential = EmailAuthProvider
                    .getCredential(firebaseUser.getEmail(), password);

            firebaseUser.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("userReAuth", "User re-authenticated.");

                            firebaseUser.updateEmail(email).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Email has been successfully changed!", Toast.LENGTH_SHORT).show();
                                    mDatabase.child("users").child(userKey).child("email").setValue(email);
                                    Handler handler = new Handler();
                                    Runnable r = () -> {
                                        Intent intent = new Intent();
                                        intent.putExtra("email", email);
                                        setResult(Activity.RESULT_OK, intent);
                                        finish();
                                    };
                                    handler.postDelayed(r, 1500);
                                } else {
                                Toast.makeText(getApplicationContext(), "This email is already in use.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to reauthenticate.", Toast.LENGTH_SHORT).show();
                            Log.d("userReAuth", "Failed to re-authenticate user.");
                        }
                    });
        }
    }

    private void changeName() {
        final String name = new_name.getText().toString();

        if(name.isEmpty()){
            new_name.setError("New name must be of at least 6 characters.");
            new_name.requestFocus();
            return;
        }

        mDatabase.child("users").child(userKey).child("name").setValue(name)
                .addOnCompleteListener(task -> {
                    Toast.makeText(this, "Successfully changed name", Toast.LENGTH_SHORT).show();
                    Handler handler = new Handler();
                    Runnable r = () -> {
                        Intent intent = new Intent();
                        intent.putExtra("name", name);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    };
                    handler.postDelayed(r, 2000);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to change name", Toast.LENGTH_SHORT).show();
                });
    }
}
