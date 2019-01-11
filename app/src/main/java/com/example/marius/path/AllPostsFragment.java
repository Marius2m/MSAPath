package com.example.marius.path;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.marius.path.adapters.PostsAdapter;
import com.example.marius.path.data_model.IndividualPost;

import java.util.ArrayList;
import java.util.List;


public class AllPostsFragment extends Fragment {
    private View v;
    private List<IndividualPost> posts = new ArrayList<>();
    private RecyclerView recyclerView;
    private PostsAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_all_posts, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mAdapter = new PostsAdapter(posts);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        populatePosts();
        mAdapter.notifyDataSetChanged();

        return v;
    }

    private void populatePosts(){
        posts.add(new IndividualPost("07 JUN 2019", "13 FEB 2019", "Venice", "5","Venice", "Marius Mircea"));
        posts.add(new IndividualPost("12 JAN 2019", "15 MAR 2019", "France", "3","Francee", "Karina Ciupa"));
        posts.add(new IndividualPost("23 MAR 2019", "13 FEB 2019", "Tokyo", "2","Tokyyo", "Andrei Lazor"));
    }
}
