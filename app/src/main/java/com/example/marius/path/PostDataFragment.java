package com.example.marius.path;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marius.path.adapters.PostContentsAdapter;
import com.example.marius.path.user_data.ImageContent;
import com.example.marius.path.user_data.ParagraphContent;
import com.example.marius.path.user_data.PostContent;
import com.example.marius.path.user_data.PostContents;
import com.example.marius.path.user_data.PostData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class PostDataFragment extends Fragment {
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    private static final int PICK_IMG_REQ_CODE = 2;

    private Button doneBtn;
    private Button cancelBtn;

    private String postKey;
    private Uri mImageUri;
    private Uri coverImageUri;
    private int index = 0;
    private String path;
    private boolean havePictures = false;
    private String userKey;

    public PostData postData;
    private ArrayList<Uri> pictureUris = new ArrayList<Uri>();
    private ArrayList<String> pictureUrls = new ArrayList<String>();
    private ArrayList<PostContent> postContents = new ArrayList<PostContent>();

    ContentResolver contentResolver;
    MimeTypeMap mime;

    final Handler handler = new Handler();

    private RecyclerView recyclerView;
    private PostContentsAdapter mAdapter;
    private ArrayList<PostContent> postContentsAdapter = new ArrayList<PostContent>();
    private List<String> posts = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_post_data, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        contentResolver = getContext().getContentResolver();
        mime = MimeTypeMap.getSingleton();

        doneBtn = (Button) v.findViewById(R.id.done_btn);
        cancelBtn = (Button) v.findViewById(R.id.cancel_btn);

        final FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fabtn);
        final FloatingActionButton picBtn = (FloatingActionButton) v.findViewById(R.id.fabPicture);
        FloatingActionButton textBtn = (FloatingActionButton) v.findViewById(R.id.fabText);

        final LinearLayout picLayout = (LinearLayout) v.findViewById(R.id.pictureLayout);
        final LinearLayout textLayout = (LinearLayout) v.findViewById(R.id.textLayout);
        picLayout.setVisibility(View.GONE);
        textLayout.setVisibility(View.GONE);

        final Animation showBtn = AnimationUtils.loadAnimation(getActivity(), R.anim.show_button);
        final Animation hideBtn = AnimationUtils.loadAnimation(getActivity(), R.anim.hide_button);
        final Animation showLayout = AnimationUtils.loadAnimation(getActivity(), R.anim.show_layout);
        final Animation hideLayout = AnimationUtils.loadAnimation(getActivity(), R.anim.hide_layout);


        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(picLayout.getVisibility() == View.VISIBLE && textLayout.getVisibility() == View.VISIBLE){
                    picLayout.setVisibility(View.GONE);
                    textLayout.setVisibility(View.GONE);

                    picLayout.startAnimation(hideLayout);
                    textLayout.startAnimation(hideLayout);
                    fab.startAnimation(hideBtn);
                }else{
                    picLayout.setVisibility(View.VISIBLE);
                    textLayout.setVisibility(View.VISIBLE);

                    picLayout.startAnimation(showLayout);
                    textLayout.startAnimation(showLayout);
                    fab.startAnimation(showBtn);
                }
            }
        });

        textBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picLayout.setVisibility(View.GONE);
                textLayout.setVisibility(View.GONE);

                picLayout.startAnimation(hideLayout);
                textLayout.startAnimation(hideLayout);
                fab.startAnimation(hideBtn);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.dialog_post_text, null);

                final EditText paragraphText = (EditText) mView.findViewById(R.id.editParagraphText);
                Button cancelBtn = (Button) mView.findViewById(R.id.cancelBtn);
                Button postBtn = (Button) mView.findViewById(R.id.postBtn);

                builder.setView(mView);
                final AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);

                postBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!paragraphText.getText().toString().isEmpty()){
                            Log.d("Dialog: text is: ", paragraphText.getText().toString());

                            postContents.add(new ParagraphContent(paragraphText.getText().toString()));
                            postContentsAdapter.add(new ParagraphContent(paragraphText.getText().toString()));
                            mAdapter.notifyItemInserted(mAdapter.getItemCount());

                            dialog.dismiss();
                        }else{
                            Toast.makeText(getActivity(), "Empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        picBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picLayout.setVisibility(View.GONE);
                textLayout.setVisibility(View.GONE);

                picLayout.startAnimation(hideLayout);
                textLayout.startAnimation(hideLayout);
                fab.startAnimation(hideBtn);

                chosePicture();

                Log.d("InsidePicture", "PICTURE");
            }
        });

        doneBtn.setOnClickListener(v1 -> {

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            userKey = firebaseUser.getUid();
            Log.d("userKey", userKey);

            postKey = mDatabase.child("posts").push().getKey();
            path = "posts/ " + postKey + "/";
            uploadCoverImg();

            postData.printContent();
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.dialog_discard_post_text, null);

                Button discardBtn = (Button) mView.findViewById(R.id.discardBtn);
                Button keepBtn = (Button) mView.findViewById(R.id.keepBtn);
                builder.setView(mView);

                final AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);

                keepBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                discardBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        FragmentTransaction fragmentT = getFragmentManager().beginTransaction();
                        fragmentT.replace(R.id.fragment_container, new AddFragment()).commit();
                    }
                });
            }
        });

        Bundle b = getArguments();
        postData = (PostData) getArguments().getSerializable("PostData");
        //String coverPhotoStringUri = (String) getArguments().getSerializable("CoverImgUri");
        coverImageUri = Uri.parse((String) getArguments().getSerializable("CoverImgUri"));

        Log.i("getPostData", postData.toString());
        Log.d("coverImageUri", coverImageUri.toString());

        recyclerView = v.findViewById(R.id.test_recyler_view);
        mAdapter = new PostContentsAdapter(postContentsAdapter);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        return v;
    }

    private void uploadCoverImg(){
        final StorageReference picRef = mStorageRef.child(path + System.currentTimeMillis()
                                        + "." + getFileExtension(coverImageUri));

        picRef.putFile(coverImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                picRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("999", uri.toString() + "\n" + "postKey:" + postKey);
                        postData.setCoverImg(uri.toString());

                        if (havePictures)
                            uploadPictures(pictureUris);
                        else
                            postDataToFirebase();
                    }
                });
            }
        });
    }

    private String getFileExtension(Uri uri){
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadPictures(final ArrayList<Uri> pictureUris){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userKey = firebaseUser.getUid();

        Uri img = pictureUris.get(index);
        final StorageReference picRef = mStorageRef.child(path + System.currentTimeMillis() + "." + getFileExtension(img));

        ++index;

        picRef.putFile(img)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        picRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                pictureUrls.add(uri.toString());
                                String x = uri.toString();
                                Log.d("uploadFileXXX:",x);
                                if(index < pictureUris.size()){
                                    uploadPictures(pictureUris);
                                }
                                else if(index == pictureUris.size()){
                                    addUrlsToPost();
                                    postDataToFirebase();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("uploadFile:", "crashed inside onFailure");
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        //mProgressBar.setProgress((int) progress);
                    }
                });
    }

    private void postDataToFirebase(){
        commitPostToFirebase();
    }

    private void commitPostToFirebase() {
        mDatabase.child("posts").child(postKey).setValue(postData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        for(PostContent p : postContents){
                            System.out.println(p.getContent() + "\n");
                        }

                        commitPostContentToFirebase();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to post data. Make sure you have mobile data turned on!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void commitPostContentToFirebase(){
        PostContents postContentsData = new PostContents();
        postContentsData.setPostContents(postContents);

        mDatabase.child("posts_contents").child(postKey).setValue(postContentsData.getPostContents())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("[userKey]", userKey);
                        String key = mDatabase.child("users").child(userKey).child("posts").push().getKey();
                        mDatabase.child("users").child(userKey).child("posts").child(key).setValue(postKey);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                FragmentTransaction fragmentT = getFragmentManager().beginTransaction();
                                fragmentT.replace(R.id.fragment_container, new AddFragment()).commit();
                            }
                        }, 2000);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to commit post_contents data. Make sure you have mobile data turned on!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addUrlsToPost(){
        int size = postContents.size();
        int index = 0;

        for(int i = 0; i < size; i++){
            PostContent postContent = postContents.get(i);
            if(postContent.getType().equals("image")){
                postContent.setContent(pictureUrls.get(index));
                index++;
            }
        }
    }

    private void chosePicture(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMG_REQ_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMG_REQ_CODE && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            mImageUri = data.getData();

            Uri temp = data.getData();
            pictureUris.add(temp);
            Log.d("mImageUri:", mImageUri.toString());

            postContents.add(new ImageContent());
            havePictures = true;

            ImageContent imageContent = new ImageContent();
            imageContent.setContent(mImageUri.toString());
            postContentsAdapter.add(imageContent);
            mAdapter.notifyItemInserted(mAdapter.getItemCount());
        }
    }
}
