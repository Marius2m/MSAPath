package com.example.marius.path;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.example.marius.path.user_data.PostData;


public class PostDataFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_post_data, container, false);

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

                
                Log.d("InsideText", "TEXT");
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

                Log.d("InsidePicture", "PICTURE");
            }
        });

        //Bundle b = getArguments();
        PostData postData = (PostData) getArguments().getSerializable("PostData");
        Log.i("getPostData",postData.toString());

        return v;
    }
}
