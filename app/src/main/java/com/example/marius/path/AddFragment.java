package com.example.marius.path;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marius.path.user_data.PostData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddFragment extends Fragment {
    private EditText postTitle, postLocation, postNrOfTravelers;
    private TextView postDate;
    private Button submitDataBtn;
    private Button nextPageBtn;

    private PostData postData = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_add, container, false);

        postTitle = (EditText) v.findViewById(R.id.postTitle);
        postLocation = (EditText) v.findViewById(R.id.postLocation);
        postDate = (TextView) v.findViewById(R.id.postDate);
        postNrOfTravelers = (EditText) v.findViewById(R.id.postNrTravelers);

        /*nextPageBtn = (Button) v.findViewById(R.id.nextPage);
        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundleArgs = new Bundle();
                bundleArgs.putSerializable("PostData",postData);

                PostDataFragment postDataFragment = new PostDataFragment();
                postDataFragment.setArguments(bundleArgs);

                FragmentTransaction fragmentT = getFragmentManager().beginTransaction();
                fragmentT.replace(R.id.fragment_container, postDataFragment).commit();
            }
        });*/

        submitDataBtn = (Button) v.findViewById(R.id.submitData);
        submitDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = postTitle.getText().toString().trim();
                String location = postLocation.getText().toString().trim();
                String date = postDate.getText().toString().trim();
                String nrDays = postNrOfTravelers.getText().toString().trim();

                /*if(title.isEmpty()){
                    postTitle.setError("A title is required");
                    postTitle.requestFocus();
                    return;
                }
                if(location.isEmpty()){
                    postLocation.setError("Location is required");
                    postLocation.requestFocus();
                    return;
                }
                /*if(date.isEmpty()){
                    postDate.setError("Date is required");
                    postDate.requestFocus();
                    return;
                }//
                if(nrDays.isEmpty()){
                    postNrOfTravelers.setError("Number of travelers is required");
                    postNrOfTravelers.requestFocus();
                    return;
                }*/
                title="title";
                location="location";
                nrDays="5";

//                DateFormat dateF = new SimpleDateFormat("d MMM yyyy");
//                String creationDate = dateF.format(Calendar.getInstance().getTime());

                Long creationDateLong = System.currentTimeMillis() / 1000;
                String creationDate = "" + creationDateLong;

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                final String userKey = firebaseUser.getUid();
                postData = new PostData(userKey, title, location,"1 Jun 2018" , nrDays, creationDate);
                Log.d("addFragmentData", title + " " + location + " " + date + " " + nrDays
                );

                Bundle bundleArgs = new Bundle();
                bundleArgs.putSerializable("PostData",postData);

                PostDataFragment postDataFragment = new PostDataFragment();
                postDataFragment.setArguments(bundleArgs);

                FragmentTransaction fragmentT = getFragmentManager().beginTransaction();
                fragmentT.replace(R.id.fragment_container, postDataFragment).commit();

                //Toast.makeText(getActivity(), postTitle.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return v;
        //return inflater.inflate(R.layout.fragment_add, container, false);
    }
}
