package com.example.marius.path;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
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
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
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

    private String userKey;
    private boolean isLoading = false;

    private TextView edit_profile_title, reset_password;
    private EditText current_name, new_name, current_password, new_password, confirm_password, current_email, new_email, confirm_password_email, confirm_password_delete_profile;
    private Button change_name_btn, change_password_btn, change_email_btn, delete_profile_btn;
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
        styleResetPassword();

        // deleteProfile
        confirm_password_delete_profile = findViewById(R.id.confirm_password_delete_profile);
        delete_profile_btn = findViewById(R.id.delete_profile_btn);
        delete_profile_btn.setOnClickListener(this);

        if (activityType.equals("Name")) {
            current_name.setText(getIntent().getStringExtra("name"));
            change_profile_ll = findViewById(R.id.change_name_ll);
        } else if (activityType.equals("Email")) {
            current_email.setText(getIntent().getStringExtra("email"));
            change_profile_ll = findViewById(R.id.change_email_ll);
        } else if(activityType.equals("Password")) {
            change_profile_ll = findViewById(R.id.change_password_ll);
        } else if (activityType.equals("DeleteProfile")) {
            edit_profile_title.setText("Delete Profile");
            change_profile_ll = findViewById(R.id.delete_profile_ll);
        }
        change_profile_ll.setVisibility(View.VISIBLE);
    }

    private void styleResetPassword() {
        String loginText = "Forgot password?  Reset via e-mail";
        SpannableString ss = new SpannableString(loginText);
        ForegroundColorSpan fcsPrimaryClr = new ForegroundColorSpan(Color.parseColor("#FF3F51B5"));
        ClickableSpan resetPasswordClick = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    Log.d("keyboard", "Failed to close keyboard");
                }

               FirebaseAuth.getInstance().sendPasswordResetEmail(firebaseUser.getEmail())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Reset link has been sent to your e-mail!", Toast.LENGTH_SHORT).show();
                                Handler handler = new Handler();
                                Runnable r = () -> {
                                    Intent intent = new Intent();
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                };
                                handler.postDelayed(r, 1500);
                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(fcsPrimaryClr,18,loginText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new StyleSpan(Typeface.BOLD), 18, loginText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(resetPasswordClick, 18, loginText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        reset_password.setText(ss);
        reset_password.setHighlightColor(Color.TRANSPARENT);
        reset_password.setMovementMethod(LinkMovementMethod.getInstance());
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
                changePassword();
                break;

            case R.id.delete_profile_btn:
                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    Log.d("keyboard", "Failed to close keyboard");
                }
                deleteProfile();
                break;
//            case R.id.reset_password:
////                Toast.makeText(this, "reset_password", Toast.LENGTH_LONG).show();
////                finish();
////                break;

            default:
                break;
        }
    }

    private void changePassword() {
        final String currentPassword = current_password.getText().toString();
        final String newPassword = new_password.getText().toString();
        final String confirmPassword = confirm_password.getText().toString();

        if (newPassword.length() < 6) {
            new_password.setError("A password is of at least 6 characters is required.");
            new_password.requestFocus();
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            new_password.setError("The two passwords must match.");
            new_password.requestFocus();
            return;
        }
        if (currentPassword.equals(newPassword)) {
            current_password.setError("New password cannot be current password.");
            current_password.requestFocus();
            return;
        }

        AuthCredential credential = EmailAuthProvider
                .getCredential(firebaseUser.getEmail(), currentPassword);

        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("userReAuth", "User re-authenticated.");

                        firebaseUser.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Password has been successfully changed!", Toast.LENGTH_SHORT).show();
                                Handler handler = new Handler();
                                Runnable r = () -> {
                                    Intent intent = new Intent();
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                };
                                handler.postDelayed(r, 1500);
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to change password.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Your current password is wrong.", Toast.LENGTH_SHORT).show();
                        Log.d("userReAuth", "Failed to re-authenticate.");
                    }
                });

    }

    private void deleteProfile() {

        final String currentPassword = confirm_password_delete_profile.getText().toString();

        if (currentPassword.length() < 6) {
            confirm_password_delete_profile.setError("A password is of at least 6 characters is required.");
            confirm_password_delete_profile.requestFocus();
            return;
        }
        AuthCredential credential = EmailAuthProvider
                .getCredential(firebaseUser.getEmail(), currentPassword);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        isLoading = true;
        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("userReAuthDelProfile", "User re-authenticated .");

                        Toast.makeText(getApplicationContext(), "Profile has been deleted. You will now be logged out.", Toast.LENGTH_SHORT).show();
                                Handler handler = new Handler();
                                Runnable r = () -> {
                                    Intent intent = new Intent();
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                };
                                handler.postDelayed(r, 1500);


                        // call firebase HTTP delete function
                        // -> onResponse log-out + deletestack
//                        firebaseUser.updatePassword(newPassword).addOnCompleteListener(task1 -> {
//                            if (task1.isSuccessful()) {
//                                Toast.makeText(getApplicationContext(), "Password has been successfully changed!", Toast.LENGTH_SHORT).show();
//                                Handler handler = new Handler();
//                                Runnable r = () -> {
//                                    Intent intent = new Intent();
//                                    setResult(Activity.RESULT_OK, intent);
//                                    finish();
//                                };
//                                handler.postDelayed(r, 1500);
//                            } else {
//                                Toast.makeText(getApplicationContext(), "Failed to change password.", Toast.LENGTH_SHORT).show();
//                            }
//                        });
                    } else {
                        isLoading = false;
                        Toast.makeText(getApplicationContext(), "Your current password is wrong.", Toast.LENGTH_SHORT).show();
                        Log.d("userReAuthDelProfile", "Failed to re-authenticate.");
                    }
                });

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

    @Override
    public void onBackPressed() {
        if (!isLoading)
            super.onBackPressed();
    }
}
