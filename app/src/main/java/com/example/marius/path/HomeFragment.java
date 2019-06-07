package com.example.marius.path;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marius.path.PicassoTransformations.CircleTransform;
import com.example.marius.path.adapters.PostsAdapter;
import com.example.marius.path.data_model.IndividualPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private View v;
    private TextView nr_of_paths, joined_date, home_headerTitle;
    private RecyclerView recyclerView;
    private PostsAdapter mAdapter;
    private ImageView home_avatar;

    int visibleItemCount = 0;
    int totalItemCount = 0;
    int pastVisibleItem = 0;
    boolean isLoading = false;

    private DatabaseReference mDatabase;

    private List<IndividualPost> posts = new ArrayList<>();
    private List<String> postsIds = new ArrayList<>();
    private Map<String, String> postKeys = new HashMap<>();
//    private List<String> postsKeys = new ArrayList<>();
    private static final int NR_POSTS_TO_DOWNLOAD = 3;
    private String userId;
    private int currentPost = 0;
    private int nrPostsDownloaded = 0;
    private String userName;
    private String email;
    private String profilePictureUrl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_home, container, false);

        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-message".
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("nr-paths-changed"));

        mDatabase = FirebaseDatabase.getInstance().getReference();

        recyclerView = v.findViewById(R.id.home_recyclerView2);
        mAdapter = new PostsAdapter(posts);

        nr_of_paths = v.findViewById(R.id.nr_of_paths);
        joined_date = v.findViewById(R.id.joined_date);
        home_headerTitle = v.findViewById(R.id.home_headerTitle);
        home_avatar = v.findViewById(R.id.home_avatar);
        home_avatar.setOnClickListener(this);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        getUserData();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!postsIds.isEmpty() && dy > 0 && currentPost < postsIds.size()) {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                    if (!isLoading) {
                        if ((visibleItemCount + pastVisibleItem) >= totalItemCount) {

                            nrPostsDownloaded = 0;
                            if(currentPost < postsIds.size()) {
                                isLoading = true;
                                initialPopulation();

                                Toast.makeText(getActivity(), "Loaded some posts", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getActivity(), "Displayed all posts", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        });

        return v;
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int index = intent.getIntExtra("position", -1);
            String postId = intent.getStringExtra("postId");
            if (index >= 0) {

                StringBuilder postKey = new StringBuilder(postKeys.get(postId));
                System.out.println(postKey.hashCode());
                System.out.println(postKeys.get(postId).hashCode());
                //                        postKeys.remove(postId);
                mDatabase.child("users").child(userId).child("posts").child(postKey.toString()).removeValue();
                System.out.println("postId is:" + postKeys.get(postId));

                nr_of_paths.setText(String.valueOf(posts.size()));
            }
        }
    };

    public void getUserData(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();

        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child("name").getValue(String.class);
                email = dataSnapshot.child("email").getValue(String.class);

                home_headerTitle.setText(userName);
                joined_date.setText(dataSnapshot.child("dateCreated").getValue(String.class));

                if(dataSnapshot.child("profilePictureUrl").getValue(String.class) != null) {
                    profilePictureUrl = dataSnapshot.child("profilePictureUrl").getValue(String.class);
                    Log.d("picasso", profilePictureUrl);
                    Picasso.get()
                            .load(dataSnapshot.child("profilePictureUrl").getValue(String.class))
                            .placeholder(home_avatar.getDrawable())
                            .centerCrop()
                            .fit()
                            .transform(new CircleTransform())
                            .into(home_avatar);
                }
                if(dataSnapshot.child("posts").getValue() != null) {

                    Iterable<DataSnapshot> it = dataSnapshot.child("posts").getChildren();
                    for (DataSnapshot post : it) {
                        postsIds.add(post.getValue().toString());
                        postKeys.put(post.getValue().toString(), post.getKey());
                        System.out.println("posts.getValue()" + post.getKey());
                    }

                    nr_of_paths.setText(String.valueOf(postsIds.size()));
                    System.out.println("Inside SizeKeys: " + postKeys.size());

                    initialPopulation();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initialPopulation(){
        mDatabase.child("posts").child(postsIds.get(currentPost++)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final IndividualPost indivPost = dataSnapshot.getValue(IndividualPost.class);
                indivPost.setPostId(postsIds.get(currentPost - 1));
                posts.add(indivPost);
                ++nrPostsDownloaded;

                if(nrPostsDownloaded < NR_POSTS_TO_DOWNLOAD){
                    if(currentPost < postsIds.size()) {
                        initialPopulation();
                    }else{
                        isLoading = false;
                        mAdapter.notifyDataSetChanged();
                    }
                }else{
                    isLoading = false;
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void populatePostsMockData_1(){
//        posts.add(new IndividualPost(new String(1547220397), "09 APR 2018", "JERULASEM", "5", "BeautifulMan", "11414fff", "xD"));
//        posts.add(new IndividualPost(new String(1547220397), "13 FEB 2019", "MOCK_DATA_1", "5","Venice", "Marius Mircea", "ASDADSA"));
//        posts.add(new IndividualPost(new Long(1547220397), "15 MAR 2019", "MOCK_DATA_2", "3","Francee", "Karina Ciupa", "ASDAD"));
//        posts.add(new IndividualPost(new Long(1547220397), "13 FEB 2019", "MOCK_DATA_3", "2","Tokyyo", "Andrei Lazor", "XXD"));
//        posts.add(new IndividualPost(new Long(1547220397), "09 APR 2018", "JERULASEM", "5", "BeautifulMan", "11414fff", "xD"));
//        posts.add(new IndividualPost(new Long(1547220397), "13 FEB 2019", "MOCK_DATA_1", "5","Venice", "Marius Mircea", "ASDADSA"));
//        posts.add(new IndividualPost(new Long(1547220397), "15 MAR 2019", "MOCK_DATA_2", "3","Francee", "Karina Ciupa", "ASDAD"));
//        posts.add(new IndividualPost(new Long(1547220397), "13 FEB 2019", "MOCK_DATA_3", "2","Tokyyo", "Andrei Lazor", "XXD"));
//        posts.add(new IndividualPost(new Long(1547220397), "09 APR 2018", "JERULASEM", "5", "BeautifulMan", "11414fff", "xD"));
//        posts.add(new IndividualPost(new Long(1547220397), "13 FEB 2019", "10 10 10 10", "5","Venice", "Marius Mircea", "ASDADSA"));
    }
    private void populatePostsMockData_2(){
//        posts.add(new IndividualPost(new Long(1547220397), "09 APR 2018", "FAKEEEEEEEE", "5", "BeautifulMan", "11414fff", "xD"));
//        isLoading = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_avatar:
                Toast.makeText(this.getContext(), "Worked", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getContext(), Settings.class);
                intent.putExtra("name", userName);
                intent.putExtra("email", email);
                intent.putExtra("avatar", profilePictureUrl);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
