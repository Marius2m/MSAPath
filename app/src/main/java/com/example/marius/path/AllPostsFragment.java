package com.example.marius.path;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.marius.path.adapters.PostsAdapter;
import com.example.marius.path.data_model.IndividualPost;
import com.example.marius.path.user_data.ImageContent;
import com.example.marius.path.user_data.MapContent;
import com.example.marius.path.user_data.ParagraphContent;
import com.example.marius.path.user_data.PostContent;
import com.example.marius.path.user_data.UserAccount;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class AllPostsFragment extends Fragment {
    private View v;
    private List<IndividualPost> posts = new ArrayList<>();
    private RecyclerView recyclerView;
    private PostsAdapter mAdapter;
    private DatabaseReference mDatabase;
    private String oldestPostId;
    private UserAccount userAccount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_all_posts, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference("/posts");

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mAdapter = new PostsAdapter(posts);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        //populatePosts();
        initialPopulatePostsFromDB();
        mAdapter.notifyDataSetChanged();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    //populatePosts();
                    populatePostsDB();
                    //mAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), "Last", Toast.LENGTH_LONG).show();
                }
            }
        });

        return v;
    }

    private void populatePosts(){
        posts.add(new IndividualPost("1547220397", "13 FEB 2019", "MOCK_DATA_1", "5","Venice", "Marius Mircea"));
        posts.add(new IndividualPost("1547220397", "15 MAR 2019", "MOCK_DATA_2", "3","Francee", "Karina Ciupa"));
        posts.add(new IndividualPost("1547220397", "13 FEB 2019", "MOCK_DATA_3", "2","Tokyyo", "Andrei Lazor"));
    }

    private void initialPopulatePostsFromDB(){
        mDatabase.limitToFirst(4).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<IndividualPost> postsDB = new ArrayList<>();

                for(DataSnapshot postSnapShot : dataSnapshot.getChildren()){
                    List<PostContent> typePostContents = new ArrayList<>();

                    for(DataSnapshot contentList: postSnapShot.child("contents").getChildren()){
                        for(DataSnapshot content: contentList.getChildren()) {
                            if(content.getKey().equals("paragraph")){
                                typePostContents.add(new ParagraphContent(content.getValue().toString()));
                            }else if(content.getKey().equals("map")){
                                typePostContents.add(new MapContent(content.getValue().toString()));
                            }else if(content.getKey().equals("image")){
                                typePostContents.add(new ImageContent(content.getValue().toString()));
                            }
//                            System.out.println("ZZ 1: " + content.getKey());
//                            System.out.println("ZZ 2: " + content.getValue());
                        }
//                        System.out.println("YY 1" + contentList.getKey());
//                        System.out.println("YY 2" + contentList.getValue());
                    }

                    oldestPostId = postSnapShot.getKey();
                    final IndividualPost indivPost = postSnapShot.getValue(IndividualPost.class);
                    indivPost.setContents(typePostContents);

                    /*
                    FirebaseDatabase.getInstance().getReference("/users").child(indivPost.getUserId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            System.out.println("user DATA HERE: " + dataSnapshot.getValue());
                            userAccount = dataSnapshot.getValue(UserAccount.class);
                            System.out.println("Full Data::: " + userAccount.getName() + " " + userAccount.getDateCreated() + " " + userAccount.getEmail() + " " + userAccount.getAge());

                            indivPost.setUserId(userAccount.getName());
                            System.out.println("[1] ACC NAME: " + indivPost.getUserId());
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    */

//                    FirebaseDatabase.getInstance().getReference("/users").child(indivPost.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            System.out.println("user DATA HERE: " + dataSnapshot.getValue());
//                            userAccount = dataSnapshot.getValue(UserAccount.class);
//                            System.out.println("Full Data::: " + userAccount.getName() + " " + userAccount.getDateCreated() + " " + userAccount.getEmail() + " " + userAccount.getAge());
//
//                            indivPost.setUserId(userAccount.getName());
//                            System.out.println("[1] ACC NAME: " + indivPost.getUserId());
//                        }
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });

                    System.out.println("[2] ACC NAME: " + indivPost.getUserId());
                    postsDB.add(indivPost);
                    System.out.println("[3] ACC NAME: " + indivPost.getUserId());
                    System.out.println("postSnapShot.getKey: " + postSnapShot.getKey()); // "-key1", "-key2", etc
                    System.out.println("postSnapShot.getValue() " + postSnapShot.getValue()); // true, true, etc
                    Log.d("ShowPostId", oldestPostId);
                }

                posts.addAll(postsDB);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void populatePostsDB(){
        mDatabase.orderByKey().startAt(oldestPostId).limitToFirst(5).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<IndividualPost> postsDB = new ArrayList<>();

                for(DataSnapshot postSnapShot : dataSnapshot.getChildren()){
                    List<PostContent> typePostContents = new ArrayList<>();

                    for(DataSnapshot contentList: postSnapShot.child("contents").getChildren()){
                        for(DataSnapshot content: contentList.getChildren()) {
                            if(content.getKey().equals("paragraph")){
                                typePostContents.add(new ParagraphContent(content.getValue().toString()));
                            }else if(content.getKey().equals("map")){
                                typePostContents.add(new MapContent(content.getValue().toString()));
                            }else if(content.getKey().equals("image")){
                                typePostContents.add(new ImageContent(content.getValue().toString()));
                            }
//                            System.out.println("ZZ 1: " + content.getKey());
//                            System.out.println("ZZ 2: " + content.getValue());
                        }
//                        System.out.println("YY 1" + contentList.getKey());
//                        System.out.println("YY 2" + contentList.getValue());
                    }

                    oldestPostId = postSnapShot.getKey();
                    final IndividualPost indivPost = postSnapShot.getValue(IndividualPost.class);
                    indivPost.setContents(typePostContents);

                    /*
                    FirebaseDatabase.getInstance().getReference("/users").child(indivPost.getUserId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            System.out.println("user DATA HERE: " + dataSnapshot.getValue());
                            userAccount = dataSnapshot.getValue(UserAccount.class);
                            System.out.println("Full Data::: " + userAccount.getName() + " " + userAccount.getDateCreated() + " " + userAccount.getEmail() + " " + userAccount.getAge());

                            indivPost.setUserId(userAccount.getName());
                            System.out.println("[1] ACC NAME: " + indivPost.getUserId());
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    */

//                    FirebaseDatabase.getInstance().getReference("/users").child(indivPost.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            System.out.println("user DATA HERE: " + dataSnapshot.getValue());
//                            userAccount = dataSnapshot.getValue(UserAccount.class);
//                            System.out.println("Full Data::: " + userAccount.getName() + " " + userAccount.getDateCreated() + " " + userAccount.getEmail() + " " + userAccount.getAge());
//
//                            indivPost.setUserId(userAccount.getName());
//                            System.out.println("[1] ACC NAME: " + indivPost.getUserId());
//                        }
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });

                    System.out.println("[2] ACC NAME: " + indivPost.getUserId());
                    postsDB.add(indivPost);
                    System.out.println("[3] ACC NAME: " + indivPost.getUserId());
                    System.out.println("postSnapShot.getKey: " + postSnapShot.getKey()); // "-key1", "-key2", etc
                    System.out.println("postSnapShot.getValue() " + postSnapShot.getValue()); // true, true, etc
                    Log.d("ShowPostId", oldestPostId);
                }

                postsDB.remove(0);
                posts.addAll(postsDB);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
