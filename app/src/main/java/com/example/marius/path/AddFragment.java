package com.example.marius.path;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marius.path.user_data.PostData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddFragment extends Fragment {
    private EditText postTitle, postLocation, postNrOfTravelers, postDate;
    private Button submitDataBtn;
    private Button nextPageBtn;

    private String postDateCalendar;

    final Calendar myCalendar = Calendar.getInstance();

    private PostData postData = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_add, container, false);

        postTitle = (EditText) v.findViewById(R.id.postTitle);
        postLocation = (EditText) v.findViewById(R.id.postLocation);
        postDate = (EditText) v.findViewById(R.id.postDate);
        postNrOfTravelers = (EditText) v.findViewById(R.id.postNrTravelers);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateTextDate();
            }
        };

        postDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), date,
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

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

                Long creationDateLong = System.currentTimeMillis() / 1000;
                String creationDate = "" + creationDateLong;

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                final String userKey = firebaseUser.getUid();
                postData = new PostData(userKey, title, location, postDateCalendar , nrDays, creationDate);
                Log.d("addFragmentData", title + " " + location + " " + postDateCalendar + " " + nrDays);

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

    private void updateTextDate(){
        String dateFormat = "dd MMM yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.UK);

        postDateCalendar = simpleDateFormat.format(myCalendar.getTime());
        postDate.setText(postDateCalendar);
    }
}
