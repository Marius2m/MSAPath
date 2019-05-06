package com.example.marius.path;

/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marius.path.adapters.GlobePostsAdapter;
import com.example.marius.path.data_model.CommentC;
import com.example.marius.path.data_model.GlobePosts;
import com.example.marius.path.data_model.IndividualPost;
import com.example.marius.path.data_model.Post;
import com.example.marius.path.services.JsonPlaceholderApi;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This shows how to create a simple activity with a raw MapView and add a marker to it. This
 * requires forwarding all the important lifecycle methods onto MapView.
 */
public class MapGlobeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mMapView;
    private TextView textView4, textView5;
    private Button showPathsFromHere;
    GoogleMap mapObj;
    Circle mapCircle;
    Double cameraLatitude = 0.0;
    Double cameraLongitude = 0.0;
    Double radiusInKm = null;

    int visibleItemCount = 0;
    int totalItemCount = 0;
    int pastVisibleItem = 0;
    boolean isLoading = false;
    boolean coordinatesChanged = false;
    boolean fetchedCountry = false;
    String foundCountry = null;
    String prevPostId = null;

    private RecyclerView recyclerView;
    private GlobePostsAdapter mAdapter;

    private List<IndividualPost> posts = new ArrayList<>();
    private List<String> postsIds = new ArrayList<>();
    private static final int NR_POSTS_TO_DOWNLOAD = 3;
    private String userId;
    private int currentPost = 0;
    private int nrPostsDownloaded = 0;

    private FirebaseFunctions mFunctions;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private JsonPlaceholderApi jsonPlaceholderApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_globe_screen);

        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = (MapView) findViewById(R.id.map_globe_view);
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
        mFunctions = FirebaseFunctions.getInstance();

        textView4 = findViewById(R.id.textView4);
        textView5 = findViewById(R.id.textView5);
        showPathsFromHere = findViewById(R.id.showPathsFromHere);

        recyclerView = (RecyclerView) findViewById(R.id.globe_recyclerView);
        mAdapter = new GlobePostsAdapter(posts);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        showPathsFromHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textView4.setText(" ");
                if(mapCircle != null){
                    mapCircle.remove();
                }

                VisibleRegion visibleRegion = mapObj.getProjection().getVisibleRegion();

                float[] distanceWidth = new float[1];
                float[] distanceHeight = new float[1];

                LatLng farRight = visibleRegion.farRight;
                LatLng farLeft = visibleRegion.farLeft;
                LatLng nearRight = visibleRegion.nearRight;
                LatLng nearLeft = visibleRegion.nearLeft;

                Location.distanceBetween(
                        (farLeft.latitude + nearLeft.latitude) / 2,
                        farLeft.longitude,
                        (farRight.latitude + nearRight.latitude) / 2,
                        farRight.longitude,
                        distanceWidth
                );

                Location.distanceBetween(
                        farRight.latitude,
                        (farRight.longitude + farLeft.longitude) / 2,
                        nearRight.latitude,
                        (nearRight.longitude + nearLeft.longitude) / 2,
                        distanceHeight
                );

                double radiusInMeters = Math.sqrt(Math.pow(distanceWidth[0], 2) + Math.pow(distanceHeight[0], 2)) / 2;
                radiusInKm = radiusInMeters / 1000;
                mapCircle = mapObj.addCircle(new CircleOptions()
                        //.center(new LatLng(latitude, longitude))
                        .center(mapObj.getCameraPosition().target)
                        .radius(radiusInMeters/2)
                        .strokeColor(0xFF303F9F)
                        .fillColor(Color.argb(70, 61, 81,181)));
                textView5.setText("KMs: " + Double.toString(radiusInKm/2));

                Double cameraTempLat = mapObj.getCameraPosition().target.latitude;
                Double cameraTempLng = mapObj.getCameraPosition().target.longitude;

                if(!cameraLongitude.equals(cameraTempLng) || !cameraLatitude.equals(cameraTempLat)) {
                    cameraLatitude = mapObj.getCameraPosition().target.latitude;
                    cameraLongitude = mapObj.getCameraPosition().target.longitude;

                    posts.clear();
                    mAdapter.notifyDataSetChanged();

                    getFirstPosts();
                    coordinatesChanged = true;

//                    textView4.setText(mapObj.getCameraPosition().toString());
                } else {
                    coordinatesChanged = false;
                }


//                String text = "mariusTest";
//                Map<String, Object> data = new HashMap<>();
//                data.put("text", "Hi Firebase!");
//
//                addNumbers(3,5)
//                        .addOnCompleteListener(new OnCompleteListener<Integer>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Integer> task) {
//                                if (!task.isSuccessful()) {
//                                    Exception e = task.getException();
//                                    if (e instanceof FirebaseFunctionsException) {
//                                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
//
//                                        // Function error code, will be INTERNAL if the failure
//                                        // was not handled properly in the function call.
//                                        FirebaseFunctionsException.Code code = ffe.getCode();
//
//                                        // Arbitrary error details passed back from the function,
//                                        // usually a Map<String, Object>.
//                                        Object details = ffe.getDetails();
//                                    }
//
//                                    // [START_EXCLUDE]
//                                    Log.d("SOME", "addNumbers:onFailure", e);
//                                    return;
//                                    // [END_EXCLUDE]
//                                }
//
//                                // [START_EXCLUDE]
//                                Integer result = task.getResult();
//                                Log.d("SOME2",String.valueOf(result));
//                                // [END_EXCLUDE]
//                            }
//                        });

//                FirebaseFunctions.getInstance()
//                        .getHttpsCallable("helloWorld")
//                        .call(data)
//                        .continueWith(new Continuation<HttpsCallableResult, String>() {
//                            @Override
//                            public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
//                                // This continuation runs on either success or failure, but if the task
//                                // has failed then getResult() will throw an Exception which will be
//                                // propagated down.
//                                String result = (String) task.getResult().getData();
//                                Log.d("SUCCCES:", result);
//                                return result;
//                            }
//                        });

            }
        });

//        populatePostsMockData_1();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dx > 0) {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && coordinatesChanged) {
                        if ((visibleItemCount + pastVisibleItem) >= totalItemCount) {
                                isLoading = true;
                                getMorePosts();
                        }
                    }
                }
            }
        });


        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://jsonplaceholder.typicode.com/")
                .baseUrl("https://us-central1-msapath-c1831.cloudfunctions.net/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        jsonPlaceholderApi = retrofit.create(JsonPlaceholderApi.class);
    }

    private void getMorePosts() {

//        if(fetchedCountry) {
//            Toast.makeText(this, "Before handler", Toast.LENGTH_LONG).show();
//
//            Handler handler = new Handler();
//            Runnable r = new Runnable() {
//                public void run() {
//                    Toast.makeText(getApplicationContext(), "Inside run" + addresses.size(), Toast.LENGTH_LONG).show();
//
//                    if (addresses.size() > 0) {
//                        Toast.makeText(getApplicationContext(), "Inside IF", Toast.LENGTH_LONG).show();
//                        textView4.append("Handler: " + addresses.get(0).getCountryName());
//                        fetchedCountry = false;
//                    }
//                }
//            };
//            handler.postDelayed(r, 10000);
//            x.append(" are mere");
//            Toast.makeText(this, "Outside handler", Toast.LENGTH_LONG).show();
//        }

        Call<GlobePosts> call = jsonPlaceholderApi.getMorePosts(
                foundCountry,
                prevPostId,
                radiusInKm/2,
                cameraLatitude,
                cameraLongitude
        );

        call.enqueue(new Callback<GlobePosts>() {
            @Override
            public void onResponse(Call<GlobePosts> call, Response<GlobePosts> response) {
                if(!response.isSuccessful()) {
                    textView4.setText("@ Code: " + response.code());
                    return;
                }

                if(response.code() == 204) {
                    textView4.setText("@ No posts from this region!");
                    return;
                }

                textView4.setText("@ RESP CODE: " + response.code());
                GlobePosts postsData = response.body();

                posts.addAll(postsData.getPosts());
                prevPostId = postsData.getPrevPostId();
                textView4.append("PREV POSTID: " + prevPostId);

                mAdapter.notifyDataSetChanged();
                currentPost += 1;
                isLoading = false;

                textView4.append("\n@ Loaded posts: " +posts.size()+"\n");
            }

            @Override
            public void onFailure(Call<GlobePosts> call, Throwable t) {
                textView4.setText("@ Failure: " + t.getMessage());
            }
        });
    }

    private void getCountryFromGeo() {
        final List<Address> addresses = new ArrayList<>();

        try {
            fetchedCountry = true;
            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            addresses.addAll(geo.getFromLocation(cameraLatitude, cameraLongitude, 1));

            if (addresses.isEmpty()) {
                textView4.append("Please pick a continent or a country\n");
                fetchedCountry = true;
            } else {
                if (addresses.size() > 0) {
                    fetchedCountry = false;
                    foundCountry = addresses.get(0).getCountryName();
                    textView4.append("Normal: " + addresses.get(0).getCountryName());
                }
            }

        } catch (Exception e) {
            Toast.makeText(this, "No Location Name Found", Toast.LENGTH_LONG).show();
        }
    }

    private void getFirstPosts() {

        getCountryFromGeo();
        if (fetchedCountry) {
            return;
        }

//        if(fetchedCountry) {
//            Toast.makeText(this, "Before handler", Toast.LENGTH_LONG).show();
//
//            Handler handler = new Handler();
//            Runnable r = new Runnable() {
//                public void run() {
//                    Toast.makeText(getApplicationContext(), "Inside run" + addresses.size(), Toast.LENGTH_LONG).show();
//
//                    if (addresses.size() > 0) {
//                        Toast.makeText(getApplicationContext(), "Inside IF", Toast.LENGTH_LONG).show();
//                        textView4.append("Handler: " + addresses.get(0).getCountryName());
//                        fetchedCountry = false;
//                    }
//                }
//            };
//            handler.postDelayed(r, 10000);
//            x.append(" are mere");
//            Toast.makeText(this, "Outside handler", Toast.LENGTH_LONG).show();
//        }

        Call<GlobePosts> call = jsonPlaceholderApi.getFirstPosts(
                foundCountry,
                radiusInKm/2,
                cameraLatitude,
                cameraLongitude
        );

        call.enqueue(new Callback<GlobePosts>() {
            @Override
            public void onResponse(Call<GlobePosts> call, Response<GlobePosts> response) {
                if(!response.isSuccessful()) {
                    textView4.setText("Code: " + response.code());
                    return;
                }

                if(response.code() == 204) {
                    textView4.setText("No more posts from this region!");
                    return;
                }

                GlobePosts postsData = response.body();

                posts.addAll(postsData.getPosts());
                mAdapter.notifyDataSetChanged();
                prevPostId = postsData.getPrevPostId();
                isLoading = false;
                currentPost += 1;

                textView4.append("\nLoaded posts: " +posts.size()+"\n");
            }

            @Override
            public void onFailure(Call<GlobePosts> call, Throwable t) {
                textView4.setText("Failure: " + t.getMessage());
            }
        });
    }

    private void getComments() {
        Call<List<CommentC>> call = jsonPlaceholderApi.getComments("posts/3/comments");

        call.enqueue(new Callback<List<CommentC>>() {
            @Override
            public void onResponse(Call<List<CommentC>> call, Response<List<CommentC>> response) {
                if(!response.isSuccessful()) {
                    textView4.setText("Code: " + response.code());
                    return;
                }

                List<CommentC> comments = response.body();
                textView4.append("getComments()\n\n");
                for(CommentC comment: comments) {
                    String content = "";
                    content += "ID: " + comment.getId() + "\n";
                    content += "Post ID: " + comment.getPostId() + "\n";
                    content += "Text: " + comment.getText() + "\n\n";

                    textView4.append(content);
                }
            }

            @Override
            public void onFailure(Call<List<CommentC>> call, Throwable t) {
                textView4.setText(t.getMessage());
            }
        });
    }

    private void getPosts() {
        Map<String, String> params = new HashMap<>();
        params.put("userId", "2");
        params.put("_sort", "id");
        params.put("_order", "asc");

        Call<List<Post>> call = jsonPlaceholderApi.getPosts(params);
//        Call<List<Post>> call = jsonPlaceholderApi.getPosts(new Integer[]{4,6}, null, null);

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (!response.isSuccessful()) {
                    textView4.setText("Code: " + response.code());
                    return;
                }

                List<Post> posts = response.body();

                for(Post post: posts) {
                    String content = "";
                    content += "ID: " + post.getId() + "\n";
                    content += "Title: " + post.getTitle() + "\n";
                    content += "Text: " + post.getText() + "\n\n";

                    textView4.append(content);
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                textView4.setText(t.getMessage());
            }
        });
    }

    private void populatePostsMockData_1(){
        posts.add(new IndividualPost("154722039", "09 APR 2018", "JERULASEM", "5", "BeautifulMan", "11414fff", "https://scontent.ftsr1-2.fna.fbcdn.net/v/t1.0-9/57726577_2302497156476353_893571416266375168_n.jpg?_nc_cat=105&_nc_ht=scontent.ftsr1-2.fna&oh=5d76faa5b6732ffc0bded1e2693a2131&oe=5D2EB124"));
        posts.add(new IndividualPost("154722039", "15 MAR 2019", "MOCK_DATA_2", "3","Francee", "Karina Ciupa", "https://scontent.ftsr1-2.fna.fbcdn.net/v/t1.0-9/57726577_2302497156476353_893571416266375168_n.jpg?_nc_cat=105&_nc_ht=scontent.ftsr1-2.fna&oh=5d76faa5b6732ffc0bded1e2693a2131&oe=5D2EB124"));
        posts.add(new IndividualPost("154722039", "13 FEB 2019", "MOCK_DATA_3", "2","Tokyyo", "Andrei Lazor", "https://scontent.ftsr1-2.fna.fbcdn.net/v/t1.0-9/57726577_2302497156476353_893571416266375168_n.jpg?_nc_cat=105&_nc_ht=scontent.ftsr1-2.fna&oh=5d76faa5b6732ffc0bded1e2693a2131&oe=5D2EB124"));
        posts.add(new IndividualPost("154722039", "13 FEB 2019", "10 10 10 10", "5","Venice", "Marius Mircea", "https://scontent.ftsr1-2.fna.fbcdn.net/v/t1.0-9/57726577_2302497156476353_893571416266375168_n.jpg?_nc_cat=105&_nc_ht=scontent.ftsr1-2.fna&oh=5d76faa5b6732ffc0bded1e2693a2131&oe=5D2EB124"));
        isLoading = false;
        currentPost += 1;
        mAdapter.notifyDataSetChanged();

    }

    private Task<Integer> addNumbers(int a, int b) {
        // Create the arguments to the callable function, which are two integers
        Map<String, Object> data = new HashMap<>();
        data.put("firstNumber", a);
        data.put("secondNumber", b);

        // Call the function and extract the operation from the result
        return mFunctions
                .getHttpsCallable("addNumbers")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, Integer>() {
                    @Override
                    public Integer then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        Map<String, Object> result = (Map<String, Object>) task.getResult().getData();
                        return (Integer) result.get("operationResult");
                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mapObj = map;
//        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        map.setMyLocationEnabled(true);

        mapObj.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                //Called when camera movement has ended, there are no pending animations and the user has stopped interacting with the map.
                Toast.makeText(getApplicationContext(), mapObj.getCameraPosition().toString(), Toast.LENGTH_LONG).show();
            }
        });

        LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        LatLng xa = new LatLng(-32, 150);
        map.addMarker(new MarkerOptions().position(xa).title("Marker in Xa"));

        LatLng x2;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GPSTracker gps = new GPSTracker(this);
        if(gps.canGetLocation()){
            x2 = new LatLng(gps.getLatitude(), gps.getLongitude());
            map.addMarker(new MarkerOptions().position(x2).title("HELLO!!!!"));
            map.moveCamera(CameraUpdateFactory.newLatLng(x2));

        }else {
            map.moveCamera(CameraUpdateFactory.newLatLng(xa));
        }
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}