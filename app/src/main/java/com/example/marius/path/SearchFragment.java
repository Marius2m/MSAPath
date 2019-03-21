package com.example.marius.path;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SearchFragment extends Fragment {
    private View v;
    private CardView cardView;
    private CardView cardViewMap;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_search, container, false);

        cardView = (CardView) v.findViewById(R.id.cardViewAll);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentT = getFragmentManager().beginTransaction();
                fragmentT.replace(R.id.fragment_container, new AllPostsFragment()).commit();
            }
        });

        cardViewMap = v.findViewById(R.id.cardViewMap);
        cardViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), MapGlobeActivity.class);
                startActivity(intent);
//                FragmentTransaction fragmentT = getFragmentManager().beginTransaction();
//                fragmentT.replace(R.id.map, new AllPostsFragment()).commit();
            }
        });


        //return inflater.inflate(R.layout.fragment_search, container, false);
        return v;
    }

}
