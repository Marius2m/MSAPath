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

public class SinglePostActivity extends AppCompatActivity {
    ImageButton back_button, heart_button;
    boolean isRedHeart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        final Toolbar mToolbar = (Toolbar) findViewById(R.id.m_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        back_button = (ImageButton) findViewById(R.id.back_toolbar_btn);
        heart_button = (ImageButton) findViewById(R.id.heart_toolbar_btn);

        heart_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRedHeart == false) {
                    heart_button.setImageResource(R.drawable.ic_favorite_red_24dp);
                    isRedHeart = true;
                }else{
                    heart_button.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    isRedHeart= false;
                }
            }
        });
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    FragmentManager fm = getSupportFragmentManager();
                    fm.popBackStackImmediate();

            }
        });


        Intent intent = getIntent();
        String postId = intent.getStringExtra("postId");
        Toast.makeText(this.getApplicationContext(), postId, Toast.LENGTH_SHORT).show();
    }
}
