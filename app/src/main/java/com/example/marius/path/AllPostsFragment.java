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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marius.path.adapters.PostsAdapter;
import com.example.marius.path.data_model.FilteredPostsBySearch;
import com.example.marius.path.data_model.GlobePosts;
import com.example.marius.path.data_model.IndividualPost;
import com.example.marius.path.services.JsonPlaceholderApi;
import com.example.marius.path.user_data.EndlessRecyclerViewScrollListener;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AllPostsFragment extends Fragment implements View.OnClickListener {
    private View v;
    private List<IndividualPost> posts = new ArrayList<>();
    private RecyclerView recyclerView;
    private PostsAdapter mAdapter;
    private DatabaseReference mDatabase;
    private String oldestPostId;

    private ImageButton searchBtn, sortBtn;
    private EditText searchBar;

    private JsonPlaceholderApi jsonPlaceholderApi;
    private String prevSortLocation = null;
    private String prevQueriedString = "";

    int visibleItemCount = 0;
    int totalItemCount = 0;
    int pastVisibleItem = 0;
    boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_all_posts, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference("/posts");

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mAdapter = new PostsAdapter(posts);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        searchBtn = v.findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(this);
//        sortBtn = v.findViewById(R.id.sortBtn);
//        sortBtn.setOnClickListener(this);
        searchBar = v.findViewById(R.id.searchBar);

        initialPopulatePostsFromDB();
        mAdapter.notifyDataSetChanged();

        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://us-central1-msapath-c1831.cloudfunctions.net/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        jsonPlaceholderApi = retrofit.create(JsonPlaceholderApi.class);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                System.out.println("Entered onScrolled");
                if (dy > 0) {
                    System.out.println("Entered dy > 0");
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                    if (!isLoading) {
                        System.out.println("Entered !isLoading");
                        if ((visibleItemCount + pastVisibleItem) >= totalItemCount) {
                            System.out.println("Entered last if");
                            if (prevSortLocation == null || searchBar.getText().toString().isEmpty()) {
                                populatePostsDB();
                                isLoading = true;
                                Toast.makeText(getActivity(), "From DB", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                filterPostsBasedOnString(searchBar.getText().toString(), prevSortLocation);
                                Toast.makeText(getActivity(), "From CF", Toast.LENGTH_SHORT).show();
                                isLoading = true;
                            }
                        }
                    }
                }
            }
        });

        return v;
    }

    private void initialPopulatePostsFromDB(){
        mDatabase.limitToFirst(4).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<IndividualPost> postsDB = new ArrayList<>();

                for(DataSnapshot postSnapShot : dataSnapshot.getChildren()){

                    List<PostContent> typePostContents = new ArrayList<>();

                    oldestPostId = postSnapShot.getKey();
                    Log.d("POSTIDS:", oldestPostId);
                    final IndividualPost indivPost = postSnapShot.getValue(IndividualPost.class);
                    indivPost.setPostId(oldestPostId);

                    postsDB.add(indivPost);

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

                    oldestPostId = postSnapShot.getKey();
                    final IndividualPost indivPost = postSnapShot.getValue(IndividualPost.class);
                    indivPost.setPostId(oldestPostId);

                    postsDB.add(indivPost);
                    Log.d("ShowPostId", oldestPostId);
                }

                isLoading = false;
                postsDB.remove(0);
                posts.addAll(postsDB);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchBtn: {
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getContext().
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }

                filterPostsBasedOnString(searchBar.getText().toString(), null);
                System.out.println(searchBar.getText().toString());
                break;
            }

//            case R.id.sortBtn: {
//                System.out.println("PREV IS: " + prevSortLocation);
//            }

            default:
                break;
        }
    }

    private void filterPostsBasedOnString(String queryString, String prevSortLocation_) {
        Call<FilteredPostsBySearch> call = jsonPlaceholderApi.searchByString(queryString, prevSortLocation_);

        call.enqueue(new Callback<FilteredPostsBySearch>() {
            @Override
            public void onResponse(Call<FilteredPostsBySearch> call, Response<FilteredPostsBySearch> response) {
                if(!response.isSuccessful()) {
                    System.out.println("Unsuccessful call");
                    return;
                }

                if(response.code() == 204) {
                    System.out.println("No posts");
                    return;
                }

                System.out.println("Successful call with code: " + response.code());
                FilteredPostsBySearch postsData = response.body();

                String tempPrevSortLocation = postsData.prevSortLocation();
                if (prevSortLocation != null && prevSortLocation.equals(tempPrevSortLocation)) {
                    System.out.println("same prevSortLocation");
                    return;
                }
                if (prevSortLocation == null || !prevQueriedString.equals(queryString))
                    posts.clear();

                prevQueriedString = queryString;
                posts.addAll(postsData.getPosts());
                System.out.println("Current prevPostId: " + postsData.prevSortLocation());
                prevSortLocation = tempPrevSortLocation;

                mAdapter.notifyDataSetChanged();
                isLoading = false;
            }

            @Override
            public void onFailure(Call<FilteredPostsBySearch> call, Throwable t) {
                System.out.println("Failed to perform GET");
            }
        });
    }
}
