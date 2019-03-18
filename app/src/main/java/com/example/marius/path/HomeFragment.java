package com.example.marius.path;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.marius.path.adapters.PostsAdapter;
import com.example.marius.path.data_model.IndividualPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private View v;
    private TextView nr_of_paths, nr_hearts, home_headerTitle;
    private RecyclerView recyclerView;
    private PostsAdapter mAdapter;

    int visibleItemCount = 0;
    int totalItemCount = 0;
    int pastVisibleItem = 0;
    boolean isLoading = false;

    private DatabaseReference mDatabase;

    private List<IndividualPost> posts = new ArrayList<>();
    private List<String> postsIds = new ArrayList<>();
    private static final int NR_POSTS_TO_DOWNLOAD = 3;
    private String userId;
    private int currentPost = 0;
    private int nrPostsDownloaded = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_home, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        recyclerView = (RecyclerView) v.findViewById(R.id.home_recyclerView2);
        mAdapter = new PostsAdapter(posts);

        nr_of_paths = (TextView) v.findViewById(R.id.nr_of_paths);
        nr_hearts = (TextView) v.findViewById(R.id.nr_hearts);
        home_headerTitle = (TextView) v.findViewById(R.id.home_headerTitle);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

//        populatePosts();
//        mAdapter.notifyDataSetChanged();
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

    public void getUserData(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();

        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("name").getValue(String.class);

                if(dataSnapshot.child("posts").getValue() != null) {
                    Iterable<DataSnapshot> it = dataSnapshot.child("posts").getChildren();
                    for (DataSnapshot post : it) {
                        postsIds.add(post.getValue().toString());
                    }
                    nr_of_paths.setText(String.valueOf(postsIds.size()));
                    home_headerTitle.setText(userName);
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
                        System.out.println("isLoading inside" + isLoading);
                    }
                }else{
                    isLoading = false;
                    mAdapter.notifyDataSetChanged();
                    System.out.println("isLoading inside" + isLoading);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    public void populateRecyclerView(){
//        System.out.println("ENTERED populateRecyclerView: " + currentPost + "========");
//        mDatabase.child("posts").child(postsIds.get(currentPost++)).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                final IndividualPost indivPost = dataSnapshot.getValue(IndividualPost.class);
//                posts.add(indivPost);
//                ++nrPostsDownloaded;
//
//                if(nrPostsDownloaded < NR_POSTS_TO_DOWNLOAD){
//                    if(currentPost < postsIds.size()) {
//                        populateRecyclerView();
//                    }else{
//                        isLoading = false;
//                        mAdapter.notifyDataSetChanged();
//                        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$4 COVERAGE 1");
//                    }
//                }else{
//                    isLoading = false;
//                    mAdapter.notifyDataSetChanged();
//                    System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$4 COVERAGE 2");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void populatePostsMockData_1(){
        posts.add(new IndividualPost("1547220397", "09 APR 2018", "JERULASEM", "5", "BeautifulMan", "11414fff", "xD"));
        posts.add(new IndividualPost("1547220397", "13 FEB 2019", "MOCK_DATA_1", "5","Venice", "Marius Mircea", "ASDADSA"));
        posts.add(new IndividualPost("1547220397", "15 MAR 2019", "MOCK_DATA_2", "3","Francee", "Karina Ciupa", "ASDAD"));
        posts.add(new IndividualPost("1547220397", "13 FEB 2019", "MOCK_DATA_3", "2","Tokyyo", "Andrei Lazor", "XXD"));
        posts.add(new IndividualPost("1547220397", "09 APR 2018", "JERULASEM", "5", "BeautifulMan", "11414fff", "xD"));
        posts.add(new IndividualPost("1547220397", "13 FEB 2019", "MOCK_DATA_1", "5","Venice", "Marius Mircea", "ASDADSA"));
        posts.add(new IndividualPost("1547220397", "15 MAR 2019", "MOCK_DATA_2", "3","Francee", "Karina Ciupa", "ASDAD"));
        posts.add(new IndividualPost("1547220397", "13 FEB 2019", "MOCK_DATA_3", "2","Tokyyo", "Andrei Lazor", "XXD"));
        posts.add(new IndividualPost("1547220397", "09 APR 2018", "JERULASEM", "5", "BeautifulMan", "11414fff", "xD"));
        posts.add(new IndividualPost("1547220397", "13 FEB 2019", "10 10 10 10", "5","Venice", "Marius Mircea", "ASDADSA"));

    }
    private void populatePostsMockData_2(){
        posts.add(new IndividualPost("1547220397", "09 APR 2018", "FAKEEEEEEEE", "5", "BeautifulMan", "11414fff", "xD"));
        isLoading = false;
    }

}
