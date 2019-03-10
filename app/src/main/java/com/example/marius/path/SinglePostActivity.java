package com.example.marius.path;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marius.path.data_model.IndividualPost;
import com.example.marius.path.user_data.PostContent;
import com.example.marius.path.user_data.PostContentFactory;
import com.example.marius.path.user_data.PostContents;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SinglePostActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton back_button, heart_button;
    private boolean isRedHeart = false;
    private TextView postThumbnailTitle, destination_text, date_text, days_text;
    private RelativeLayout parentRLayout;
    private CardView cardView;
    private ImageView userProfilePicturePost, coverImage;
    private TextView userNamePost, creationDatePost;
    private ConstraintLayout constraintLayoutBottomSection;

    private PostContents postContents = null;
    private IndividualPost postData = null;
    private String userName = "";
    private int index = 2;
    private int currentId;

    final private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);


        constraintLayoutBottomSection = (ConstraintLayout) findViewById(R.id.constraintLayoutBottomSection);
        userProfilePicturePost = (ImageView) findViewById(R.id.userProfilePicturePost);
        userNamePost = (TextView) findViewById(R.id.userNamePost);
        creationDatePost = (TextView) findViewById(R.id.creationDatePost);
        parentRLayout = (RelativeLayout) findViewById(R.id.contentsListRelativeLayout);
        cardView = (CardView) findViewById(R.id.post_row_1);
        currentId = cardView.getId();
        Log.d("CARDVIEW ID", cardView.getId()+"");
        postThumbnailTitle = (TextView) findViewById(R.id.postThumbnailTitle);
        destination_text = (TextView) findViewById(R.id.destination_text);
        date_text = (TextView) findViewById(R.id.date_text);
        days_text = (TextView) findViewById(R.id.days_text);
        coverImage = (ImageView) findViewById(R.id.post_row_0);
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.m_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        back_button = (ImageButton) findViewById(R.id.back_toolbar_btn);
        back_button.setOnClickListener(this);
        heart_button = (ImageButton) findViewById(R.id.heart_toolbar_btn);
        heart_button.setOnClickListener(this);

        Intent intent = getIntent();
        postData = (IndividualPost)intent.getSerializableExtra("postObject");

        postContents = new PostContents();
        postContents.setPostContents(new ArrayList<PostContent>());
        getPostContents();


        System.out.println("INSIDE SINGLE POST ACTIVITY " + postData.toString() + "=!!!");
        Toast.makeText(this.getApplicationContext(), postData.getPostId(), Toast.LENGTH_SHORT).show();
    }

    private void getPostContents(){
        ref.child("/posts_contents/" + postData.getPostId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PostContentFactory postContentFactory = new PostContentFactory();
                System.out.println(dataSnapshot.getValue());
                for(DataSnapshot indexPostContentSnapShot : dataSnapshot.getChildren()){
                    for(DataSnapshot content : indexPostContentSnapShot.getChildren()){
                        String className = content.getKey().toString();
                        className = (className.charAt(0) + "").toUpperCase() + className.substring(1);
                        className += "Content";

                        PostContent postContent = postContentFactory.getPostContentObject(className);
                        postContent.setContent(content.getValue().toString());
                        postContents.addPostContent(postContent);
                    }
                }

//                System.out.println("=================");
//                for(PostContent content : postContents.getPostContents()){
//                    System.out.println(content.toString());
//                }
//                System.out.println("=================");
                getUserData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserData(){
        ref.child("/users/" + postData.getUserId() + "/name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = dataSnapshot.getValue().toString();

                renderPage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void renderPage(){
        Picasso.get()
                .load(Uri.parse(postData.getCoverImg()))
                .centerCrop()
                .fit()
                .into(coverImage);

        postThumbnailTitle.setText(postData.getTitle());
        destination_text.setText(postData.getLocation());
        date_text.setText(postData.getTravelDate());
        days_text.setText(postData.getNrDays());

        for(PostContent content : postContents.getPostContents()){
            String contentType = content.getType();
            if(contentType.equals("paragraph")){
                parentRLayout.addView(createNewTextView(content.getContent(),16, 16));
            }else if(contentType.equals("image")){
                Uri imageUri = Uri.parse(content.getContent());
                parentRLayout.addView(createNewImageView(imageUri,16, 16));
            }
        }


        parentRLayout.removeView(constraintLayoutBottomSection);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        Log.d("currentd layout:", currentId+"");
        layoutParams.addRule(RelativeLayout.BELOW, currentId);
        layoutParams.setMargins(16,16,16,48);
        constraintLayoutBottomSection.setLayoutParams(layoutParams);
        parentRLayout.addView(constraintLayoutBottomSection);

        userNamePost.setText(userName + ",");
        creationDatePost.setText(getFormattedTextDate(postData.getCreationDate()));

    }

    TextView createNewTextView(String text, int margin, int padding){
        TextView newDynamicTextView = new TextView(this.getApplicationContext());

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(margin * 1, margin * 1, margin * 1, 1 * margin);
        layoutParams.addRule(RelativeLayout.BELOW, currentId);
        newDynamicTextView.setLayoutParams(layoutParams);

        newDynamicTextView.setId(TextView.generateViewId());

        currentId = newDynamicTextView.getId();
        Log.d("SPAcurrentId TXT", ""+currentId);
        newDynamicTextView.setText(text);

        newDynamicTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        newDynamicTextView.setPadding(padding * 2, 0, padding * 2, padding * 1);

        Log.d("SPAnewDynamicTxt:", newDynamicTextView.getText().toString());
        return newDynamicTextView;
    }

    private ImageView createNewImageView(Uri mImageUri, int margin, int padding){
        ImageView newDynamicImageView = new ImageView(this.getApplicationContext());

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(margin * 1 , margin * 1, margin * 1, 1 * margin);
        layoutParams.addRule(RelativeLayout.BELOW, currentId);
        newDynamicImageView.setLayoutParams(layoutParams);

        newDynamicImageView.setId(ImageView.generateViewId());

        currentId = newDynamicImageView.getId();
        Log.d("SPAcurrentId IMG", ""+currentId);

        newDynamicImageView.setPadding(padding * 2,0, padding * 2, padding * 1);
        newDynamicImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        newDynamicImageView.setAdjustViewBounds(true);

        // Native
        //newDynamicImageView.setImageURI(mImageUri);

        // Picassso API
        Picasso.get()
                .load(mImageUri)
//                .resize(Resources.getSystem().getDisplayMetrics().widthPixels, 350)
//                .centerInside()
                //.transform(new RoundedCornersTransformation(5,5))
                //.fit()
                .into(newDynamicImageView);

        //pictureUris.add(mImageUri);
        Log.d("SPAnewDynamicImg:", currentId+"");

        return newDynamicImageView;
    }

    private String getFormattedTextDate(String unformatDate){
        String dateFormat = "dd MMM yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.UK);

        Date date = new Date(Integer.parseInt(unformatDate));
        return simpleDateFormat.format(date);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.back_toolbar_btn:
                Toast.makeText(v.getContext(), "lolk", Toast.LENGTH_SHORT).show();
                break;

            case R.id.heart_toolbar_btn:
                if(isRedHeart == false) {
                    heart_button.setImageResource(R.drawable.ic_favorite_red_24dp);
                    isRedHeart = true;
                }else{
                    heart_button.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    isRedHeart= false;
                }
                break;

//            case R.id.threeButton:
//                // do your code
//                break;

            default:
                break;
        }

    }
}
