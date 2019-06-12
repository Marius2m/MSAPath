package com.example.marius.path;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marius.path.data_model.IndividualPost;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

public class SearchFragment extends Fragment {
    private View v;
    private ImageView featuredPostImage;
    private TextView featuredPostText;
    private CardView cardView;
    private CardView cardViewMap;
    private CardView cardViewFeatured;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private IndividualPost indivPost;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_search, container, false);

        featuredPostImage = v.findViewById(R.id.featuredPostImage);
        featuredPostText = v.findViewById(R.id.featuredPostText);

        database.getReference().child("featuredPost").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String postId = (String) dataSnapshot.getValue();

                database.getReference().child("posts").child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        indivPost = dataSnapshot.getValue(IndividualPost.class);
                        indivPost.setPostId(postId);

                        Picasso.get()
                                .load(Uri.parse(indivPost.getCoverImg()))
                                .centerCrop()
                                .fit()
                                .into(featuredPostImage, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        featuredPostText.setText(indivPost.getTitle());
                                        cardViewFeatured.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onError(Exception e) {

                                    }
                                });
                        Log.d("indivPostFP: ", indivPost.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("featuredPost ", "failed");
                    }
                });
                Log.d("featuredPostId: ", postId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("featuredPostId: ", "failed");
            }
        });

        cardView = v.findViewById(R.id.cardViewAll);
        cardView.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), AllPostsActivity.class);
            startActivity(intent);
        });

        cardViewMap = v.findViewById(R.id.cardViewMap);
        cardViewMap.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), MapGlobeActivity.class);
            startActivity(intent);
        });

        cardViewFeatured = v.findViewById(R.id.cardFeaturedView);
        cardViewFeatured.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), SinglePostActivity.class);
            intent.putExtra("postObject",(Serializable) indivPost);

            startActivity(intent);
            Toast.makeText(v.getContext(), indivPost.getPostId(), Toast.LENGTH_SHORT).show();
        });


        //return inflater.inflate(R.layout.fragment_search, container, false);
        return v;
    }

}
