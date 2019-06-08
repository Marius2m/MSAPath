package com.example.marius.path;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.marius.path.adapters.GlobePostsAdapter;
import com.example.marius.path.adapters.PostContentsAdapter;
import com.example.marius.path.adapters.PostsAdapter;
import com.example.marius.path.data_model.IndividualPost;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PostContentsAdapter mAdapter;

    private List<String> posts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

//        recyclerView = findViewById(R.id.test_recyler_view);
//        mAdapter = new PostContentsAdapter(posts);
//
//        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(mAdapter);
//
//        populatePostsMockData_1();
//        mAdapter.notifyDataSetChanged();
    }

    private void populatePostsMockData_1(){
        posts.add("Ana");
        posts.add("are");
        posts.add("multe");
        posts.add("mere");

        posts.add("Ana");
        posts.add("are");
        posts.add("multe");
        posts.add("mere");

        posts.add("Ana");
        posts.add("are");
        posts.add("multe");
        posts.add("mere");
//        posts.add(new IndividualPost(new String("1547220397"), "09 APR 2018", "JERULASEM", "5", "BeautifulMan", "11414fff", "xD"));
//        posts.add(new IndividualPost(new String("1547220397"), "09 APR 2018", "JERULASEM", "5", "BeautifulMan", "11414fff", "xD"));
//        posts.add(new IndividualPost(new String("1547220397"), "09 APR 2018", "JERULASEM", "5", "BeautifulMan", "11414fff", "xD"));
//        posts.add(new IndividualPost(new String("1547220397"), "09 APR 2018", "JERULASEM", "5", "BeautifulMan", "11414fff", "xD"));
//        posts.add(new IndividualPost(new String("1547220397"), "09 APR 2018", "JERULASEM", "5", "BeautifulMan", "11414fff", "xD"));
    }
}
