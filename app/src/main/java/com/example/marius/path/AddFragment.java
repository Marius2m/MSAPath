package com.example.marius.path;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marius.path.user_data.PostData;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;
import static com.example.marius.path.FieldSelector.getPlaceFields;

public class AddFragment extends Fragment {
    private static final int PICK_IMG_REQ_CODE = 1;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 23487;

    private EditText postTitle, postLocation, postNrOfTravelers, postDate;
    private Button submitDataBtn;
    private Button nextPageBtn;
    private Button coverPhotoBtn;
    private ConstraintLayout headerConstraintLayout;
    private ImageView coverPhotoImgView;
    private TextView headerTitle;

    private String postDateCalendar;

    final Calendar myCalendar = Calendar.getInstance();
    ContentResolver contentResolver;
    MimeTypeMap mime;
    private Uri coverPhotoUri;
    private PlacesClient placesClient;

    private PostData postData = null;

    private String location = "";
    private String longitude = "";
    private String latitude = "";
    private String city = "";
    private String country = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_add, container, false);

        postTitle = (EditText) v.findViewById(R.id.postTitle);
        postLocation = v.findViewById(R.id.postLocation);
        postDate = (EditText) v.findViewById(R.id.postDate);
        postNrOfTravelers = (EditText) v.findViewById(R.id.postNrTravelers);
        coverPhotoBtn = (Button) v.findViewById(R.id.coverPhotoBtn);
        headerConstraintLayout = (ConstraintLayout) v.findViewById(R.id.headerConstraintLayout);
        coverPhotoImgView = (ImageView) v.findViewById(R.id.coverPhotoImgView);
        headerTitle = (TextView) v.findViewById(R.id.headerTitle);

        //START


        String apiKey = "AIzaSyD7TC1decqiw1iKCFjaoggsraaruxTXtPE";

//        if (apiKey.equals("")) {
//            Toast.makeText(this, getString(R.string.error_api_key), Toast.LENGTH_LONG).show();
//            return;
//        }

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(this.getContext(), apiKey);
        }
        // Retrieve a PlacesClient (previously initialized - see MainActivity)
        placesClient = Places.createClient(this.getContext());

        // Set up view objects
        Spinner typeFilterSpinner = v.findViewById(R.id.autocomplete_type_filter);
        typeFilterSpinner.setAdapter(
                new ArrayAdapter<>(
                        this.getContext(), android.R.layout.simple_list_item_1, Arrays.asList(TypeFilter.values())));

        CheckBox useTypeFilterCheckBox = v.findViewById(R.id.autocomplete_use_type_filter);
        useTypeFilterCheckBox.setOnCheckedChangeListener(
                (buttonView, isChecked) -> typeFilterSpinner.setEnabled(isChecked));

        // Set listeners for Autocomplete activity
        v.findViewById(R.id.postLocation)
                .setOnClickListener(view -> startAutocompleteActivity());

        // UI initialization
        typeFilterSpinner.setEnabled(false);



        ////DONE
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

//        postLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "Tapped", Toast.LENGTH_SHORT).show();
//                postLocation.setText("Romania!!!");
//                System.out.println("Tapped Where was this");
//            }
//        });


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
                setData();
            }
        });

        coverPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosePicture();
            }
        });

        return v;
        //return inflater.inflate(R.layout.fragment_add, container, false);
    }

    private void setData() {
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
        postData = new PostData(userKey, title, location, postDateCalendar , nrDays, creationDate, "");
        Log.d("addFragmentData", title + " " + location + " " + postDateCalendar + " " + nrDays);

        Bundle bundleArgs = new Bundle();
        bundleArgs.putSerializable("PostData", postData);
        bundleArgs.putSerializable("CoverImgUri", coverPhotoUri.toString());

        PostDataFragment postDataFragment = new PostDataFragment();
        postDataFragment.setArguments(bundleArgs);


        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, 0, 0)
                .replace(R.id.fragment_container, postDataFragment).commit();

//        FragmentTransaction fragmentT = getFragmentManager().beginTransaction();
//        fragmentT.replace(R.id.fragment_container, postDataFragment).commit();

        //Toast.makeText(getActivity(), postTitle.getText().toString(), Toast.LENGTH_SHORT).show();
    }

    private void updateTextDate(){
        String dateFormat = "dd MMM yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.UK);

        postDateCalendar = simpleDateFormat.format(myCalendar.getTime());
        postDate.setText(postDateCalendar);
    }

    private String getFileExtension(Uri uri){
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void chosePicture(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMG_REQ_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMG_REQ_CODE) {
            super.onActivityResult(requestCode, resultCode, data);
            coverPhotoBtn.setText("  Change cover image");
            headerTitle.setText("");

            if (requestCode == PICK_IMG_REQ_CODE && resultCode == RESULT_OK
                    && data != null && data.getData() != null) {
                coverPhotoUri = data.getData();

                Log.d("coverPhotoUri:", coverPhotoUri.toString());
                coverPhotoImgView.setColorFilter(Color.argb(67, 14, 13, 14));
                Picasso.get()
                        .load(coverPhotoUri)
                        .centerCrop()
                        .fit()
                        .into(coverPhotoImgView);
            }
        }
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                String locationData = StringUtil.stringifyAutocompleteWidget(place, true);
                Log.d("locationData", locationData);
                setLocationField(locationData);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(getContext(), "Please select the location again", Toast.LENGTH_SHORT).show();
                postLocation.setText(status.getStatusMessage());
            } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
            // Required because this class extends AppCompatActivity which extends FragmentActivity
            // which implements this method to pass onActivityResult calls to child fragments
            // (eg AutocompleteFragment).
            super.onActivityResult(requestCode, resultCode, data);
        }

     }

    private void setLocationField(String locationData) {
        String regex = "address=([A-Za-z,\\u00BF-\\u1FFF\\u2C00-\\uD7FF\\w -]*)attributions[a-zA-Z =\\[\\],0-9-]*lat/lng:\\W*([0-9\\.]*),([0-9\\.]*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(locationData);

        while(matcher.find()) {
            location = matcher.group(1);
            longitude = matcher.group(2);
            latitude = matcher.group(3);
        }

        location = location.replaceAll(" ", "");
        String[] res = location.split(",");

        if(res.length >= 2) {
            city = res[res.length - 2];
            country = res[res.length - 1];
            System.out.println(city);
            System.out.println(country);
            postLocation.setText(city + ", " + country);
        }else if(res.length == 1) {
            country = res[res.length - 1];
            System.out.println(country);
            postLocation.setText(country);
        }
    }

    // Google Maps Location Suggestions
    private void startAutocompleteActivity() {
        Intent autocompleteIntent =
                new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, getPlaceFields())
                        //.setTypeFilter(getTypeFilter())
                        .build(this.getContext());
        startActivityForResult(autocompleteIntent, AUTOCOMPLETE_REQUEST_CODE);
    }

}
