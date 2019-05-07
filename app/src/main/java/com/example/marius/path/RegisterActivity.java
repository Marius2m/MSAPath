package com.example.marius.path;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
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
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marius.path.PicassoTransformations.CircleTransform;
import com.example.marius.path.user_data.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
    private EditText regName, regMail, regPassword, regPassword2;
    private TextView login_txt;
    private ImageView selectAvatarPicture, avatarPicture;
    private Button regBtn;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String userId;
    UserAccount userAcc;

    ContentResolver contentResolver;
    MimeTypeMap mime;

    private Uri avatarUri = null;
    private String profilePictureUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        contentResolver = this.getContentResolver();
        mime = MimeTypeMap.getSingleton();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        regName = findViewById(R.id.regName);
        regMail = findViewById(R.id.regMail);
        regPassword = findViewById(R.id.regPassword);
        regPassword2 = findViewById(R.id.regPassword2);
        login_txt = findViewById(R.id.login_txt);

        regBtn = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.progressBar);

        avatarPicture = findViewById(R.id.avatarPicture);
        selectAvatarPicture = findViewById(R.id.selectAvatarPicture);
        selectAvatarPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(RegisterActivity.this);
                Log.v("TAGG", " click");

            }
        });

        styleLoginTxtView();

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void styleLoginTxtView() {
        String loginText = "Already have an account?  Log In";
        SpannableString ss = new SpannableString(loginText);
        ForegroundColorSpan fcsPrimaryClr = new ForegroundColorSpan(Color.parseColor("#FF3F51B5"));
        ClickableSpan loginClick = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(fcsPrimaryClr,26,loginText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new StyleSpan(Typeface.BOLD), 26, loginText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(loginClick, 26, loginText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        login_txt.setText(ss);
        login_txt.setHighlightColor(Color.TRANSPARENT);
        login_txt.setMovementMethod(LinkMovementMethod.getInstance());
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
//                    auth.getInstance().signOut();

                    DateFormat dateF = new SimpleDateFormat("d MMM yyyy");
                    String date = dateF.format(Calendar.getInstance().getTime());

                    userAcc = new UserAccount(name, date, email);

                    if(avatarUri != null) {
                        uploadAvatar();
                    }else {
                        databaseReference.child("users").child(userId).setValue(userAcc);
                        Log.d("failTag", "fail" + userAcc.toString());

                        Intent intent = getIntent();
                        intent.putExtra("email", email);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                }else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "A user with this email has already registered.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                avatarUri = result.getUri();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        Picasso.get()
                .load(avatarUri)
                .centerCrop()
                .fit()
                .transform(new CircleTransform())
                .into(avatarPicture);

        Log.d("avatarUri:", avatarUri.toString());
    }

    private String getFileExtension(Uri uri){
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadAvatar(){
        StorageReference mStorageRef =  FirebaseStorage.getInstance().getReference();
        String path = "users/" + userId + "/";
        final StorageReference picRef = mStorageRef.child(path + "profilePicture"
                + ".jpg");

        picRef.putFile(avatarUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                picRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        userAcc.setProfilePictureUrl(uri.toString());
                        databaseReference.child("users").child(userId).setValue(userAcc);

                        Intent intent = getIntent();
                        intent.putExtra("email", regMail.getText().toString().trim());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }
        });
    }
}
