package com.example.marius.path;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.marius.path.data_model.IndividualPost;

public class SinglePostActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton back_button, heart_button;
    boolean isRedHeart = false;
    IndividualPost postData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        final Toolbar mToolbar = (Toolbar) findViewById(R.id.m_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        back_button = (ImageButton) findViewById(R.id.back_toolbar_btn);
        back_button.setOnClickListener(this);
        heart_button = (ImageButton) findViewById(R.id.heart_toolbar_btn);
        heart_button.setOnClickListener(this);



        Intent intent = getIntent();
        postData = (IndividualPost)intent.getSerializableExtra("postObject");
        System.out.println("INSIDE SINGLE POST ACTIVITY " + postData.toString() + "=!!!");
        Toast.makeText(this.getApplicationContext(), postData.getPostId(), Toast.LENGTH_SHORT).show();
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
