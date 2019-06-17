package com.example.marius.path;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marius.path.PicassoTransformations.CircleTransform;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static final int REQUEST_CODE_NAME_OK = 105;
    private static final int REQUEST_CODE_EMAIL_OK = 106;
    private static final int REQUEST_CODE_DELETE_PROFILE_OK = 107;
    private String name;
    private String email;
    private String avatar;
    private Uri avatarUri = null;
    private String profilePictureUrl;

    private Button delete_profile_btn, sign_out_btn;
    private TextView change_name, change_email, change_password;
    private ImageView avatarPictureSettings, selectAvatarPictureSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");

        setViews();
        signOutListener();
    }

    private void setViews() {
        avatarPictureSettings = findViewById(R.id.avatarPictureSettings);
        Picasso.get()
                .load(getIntent().getStringExtra("avatar"))
                .centerCrop()
                .fit()
                .transform(new CircleTransform())
                .into(avatarPictureSettings);
        selectAvatarPictureSettings = findViewById(R.id.selectAvatarPictureSettings);

        selectAvatarPictureSettings.setOnClickListener(v -> {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(Settings.this);
            Log.v("TAGG", " click");

        });

        change_name = findViewById(R.id.change_name);
        change_email = findViewById(R.id.change_email);
        change_password = findViewById(R.id.change_password);
        delete_profile_btn = findViewById(R.id.delete_profile_btn);
        sign_out_btn = findViewById(R.id.sign_out_btn);
        change_name.setOnClickListener(this);
        change_email.setOnClickListener(this);
        change_password.setOnClickListener(this);
        delete_profile_btn.setOnClickListener(this);
        sign_out_btn.setOnClickListener(this);
    }

    private void signOutListener(){
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null) {
                Log.d("signout", "user still signed in" + user.getUid());
            } else {
                Log.d("signout", "user has been signed out");
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        };
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("avatar", getIntent().getStringExtra("avatar"));
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState == null) return;

        avatar = savedInstanceState.getString("avatar");
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_CODE_NAME_OK){
            if (resultCode == RESULT_OK){
                name = data.getExtras().getString("name");
                System.out.println("Changed name is: " + name);
            }
        } else if (requestCode == REQUEST_CODE_EMAIL_OK){
            if (resultCode == RESULT_OK){
                email = data.getExtras().getString("email");
                System.out.println("Changed email is: " + email);
            }
        } else if (requestCode == REQUEST_CODE_DELETE_PROFILE_OK){
            if (resultCode == RESULT_OK){
                System.out.println("Deleted profile successfully, now going to Log-in");
                mAuth.signOut();
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                avatarUri = result.getUri();
                uploadAvatar();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    private void uploadAvatar(){
        StorageReference mStorageRef =  FirebaseStorage.getInstance().getReference();
        String path = "users/" + FirebaseAuth.getInstance().getUid() + "/";
        final StorageReference picRef = mStorageRef.child(path + "profilePicture"
                + ".jpg");

        picRef.putFile(avatarUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                picRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("profilePictureUrl").setValue(uri.toString());
                        Picasso.get()
                                .load(avatarUri)
                                .centerCrop()
                                .fit()
                                .transform(new CircleTransform())
                                .into(avatarPictureSettings);
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_profile_btn: {
                Intent intent = new Intent(this, EditProfile.class);
                intent.putExtra("title", "DeleteProfile");
                intent.putExtra("view", R.id.change_name_ll);
                startActivityForResult(intent, REQUEST_CODE_DELETE_PROFILE_OK);
                break;
            }

            case R.id.sign_out_btn:
                mAuth.signOut();
                finish();
                break;

            case R.id.change_name: {
                Intent intent = new Intent(this, EditProfile.class);
                intent.putExtra("title", "Name");
                intent.putExtra("name", name);
                intent.putExtra("view", R.id.change_name_ll);
                startActivityForResult(intent, REQUEST_CODE_NAME_OK);
                break;
            }

            case R.id.change_email: {
                Intent intent = new Intent(this, EditProfile.class);
                intent.putExtra("title", "Email");
                intent.putExtra("email", email);
                intent.putExtra("view", R.id.change_email_ll);
                startActivityForResult(intent, REQUEST_CODE_EMAIL_OK);
                break;
            }

            case R.id.change_password: {
                Intent intent = new Intent(this, EditProfile.class);
                intent.putExtra("title", "Password");
                intent.putExtra("view", R.id.change_password_ll);
                startActivity(intent);
                break;
            }

            default:
                break;
        }
    }
}
