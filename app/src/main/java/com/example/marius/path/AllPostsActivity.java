package com.example.marius.path;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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


public class AllPostsActivity extends AppCompatActivity implements View.OnClickListener {
    private View v;
    private List<IndividualPost> posts = new ArrayList<>();
    private RecyclerView recyclerView;
    private PostsAdapter mAdapter;
    private DatabaseReference mDatabase;
    private String oldestPostId;

    private ImageButton searchBtn, clearTextBtn;
    private EditText searchBar;

    private JsonPlaceholderApi jsonPlaceholderApi;
    private String prevSortLocation = null;
    private String prevQueriedString = "";

    int visibleItemCount = 0;
    int totalItemCount = 0;
    int pastVisibleItem = 0;
    boolean isLoading = false;
    boolean hasFetchedFilteredPosts = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_all_posts);

        mDatabase = FirebaseDatabase.getInstance().getReference("/posts");

        recyclerView = findViewById(R.id.recyclerView);
        mAdapter = new PostsAdapter(posts);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(this);
        searchBar = findViewById(R.id.searchBar);
        clearTextBtn = findViewById(R.id.clearTextBtn);
        clearTextBtn.setOnClickListener(this);

        initialPopulatePostsFromDB();
        mAdapter.notifyDataSetChanged();

        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://us-central1-msapath-c1831.cloudfunctions.net/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        jsonPlaceholderApi = retrofit.create(JsonPlaceholderApi.class);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (clearTextBtn.getVisibility() == View.GONE) {
                    clearTextBtn.setVisibility(View.VISIBLE);
                } else {
                    if (searchBar.getText().toString().isEmpty()) {
                        clearTextBtn.setVisibility(View.GONE);
                    }
                }
            }
        });

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
                            }
                            else {
                                filterPostsBasedOnString(searchBar.getText().toString(), prevSortLocation);
                                isLoading = true;
                            }
                        }
                    }
                }
            }
        });

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
                    indivPost.setType(IndividualPost.PostType.USER_POST);

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

                String tempOldPostId = oldestPostId;
                for(DataSnapshot postSnapShot : dataSnapshot.getChildren()){

                    oldestPostId = postSnapShot.getKey();
                    final IndividualPost indivPost = postSnapShot.getValue(IndividualPost.class);
                    indivPost.setPostId(oldestPostId);
                    indivPost.setType(IndividualPost.PostType.USER_POST);

                    postsDB.add(indivPost);
                }

                if (!tempOldPostId.equals(oldestPostId)) {
                    isLoading = false;
                    postsDB.remove(0);
                    posts.addAll(postsDB);
                    mAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "All posts have been loaded!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void hideSoftKeyboard() {
        View view = getCurrentFocus();
        if(view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager != null)
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchBtn: {
                hideSoftKeyboard();

                if (searchBar.getText().toString().isEmpty()) return;
                filterPostsBasedOnString(searchBar.getText().toString(), null);
                System.out.println(searchBar.getText().toString());
                break;
            }

            case R.id.clearTextBtn: {
                searchBar.setText("");

                if (hasFetchedFilteredPosts) {
                    posts.clear();
                    initialPopulatePostsFromDB();
                    hasFetchedFilteredPosts = false;

                    prevSortLocation = null;
                    prevQueriedString = "";
                }
                clearTextBtn.setVisibility(View.GONE);
                System.out.println("PREV IS: " + prevSortLocation);
            }

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
                    Toast.makeText(AllPostsActivity.this, "No posts found.", Toast.LENGTH_SHORT).show();
                    System.out.println("No posts");
                    return;
                }

                System.out.println("Successful call with code: " + response.code());
                FilteredPostsBySearch postsData = response.body();

                String tempPrevSortLocation = postsData.prevSortLocation();
                if (prevSortLocation != null && prevSortLocation.equals(tempPrevSortLocation)) {
                    Toast.makeText(getApplicationContext(), "All posts have been loaded!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (prevSortLocation == null || !prevQueriedString.equals(queryString))
                    posts.clear();

                prevQueriedString = queryString;
                posts.addAll(postsData.getPosts());
                for (IndividualPost post: posts) {
                    post.setType(IndividualPost.PostType.USER_POST);
                }
                System.out.println("Current prevPostId: " + postsData.prevSortLocation());
                prevSortLocation = tempPrevSortLocation;
                hasFetchedFilteredPosts = true;

                mAdapter.notifyDataSetChanged();
                isLoading = false;
            }

            @Override
            public void onFailure(Call<FilteredPostsBySearch> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to load posts!\nMake sure you a working internet connection.", Toast.LENGTH_SHORT).show();
                System.out.println("Failed to perform GET");
            }
        });
    }
}
