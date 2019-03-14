package com.example.marius.path;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.marius.path.adapters.PostsAdapter;
import com.example.marius.path.data_model.IndividualPost;
import com.example.marius.path.user_data.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private View v;
    private RecyclerView recyclerView;
    private PostsAdapter mAdapter;

    int visibleItemCount = 0;
    int totalItemCount = 0;
    int pastVisibleItem = 0;
    boolean isLoading = false;

    private List<IndividualPost> posts = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.home_recyclerView2);
        mAdapter = new PostsAdapter(posts);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        populatePosts();
        mAdapter.notifyDataSetChanged();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                    if (!isLoading) {
                        if ((visibleItemCount + pastVisibleItem) >= totalItemCount) {
                            isLoading = true;
                            populatePosts2();
                            mAdapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), "Last", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

//        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
//            @Override
//            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                populatePosts2();
//                mAdapter.notifyDataSetChanged();
//                Toast.makeText(getActivity(), "Last", Toast.LENGTH_SHORT).show();
//            }
//        });
        return v;
    }

    private void populatePosts(){
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
    private void populatePosts2(){
        posts.add(new IndividualPost("1547220397", "09 APR 2018", "FAKEEEEEEEE", "5", "BeautifulMan", "11414fff", "xD"));
        isLoading = false;

    }

}
