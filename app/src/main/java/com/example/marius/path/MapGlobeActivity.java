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


import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marius.path.adapters.GlobePostsAdapter;
import com.example.marius.path.data_model.GlobePosts;
import com.example.marius.path.data_model.IndividualPost;
import com.example.marius.path.data_model.ReverseGeocoding;
import com.example.marius.path.services.JsonPlaceholderApi;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapGlobeActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private MapView mMapView;
    private TextView centerOfScreenIndicator;
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

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private JsonPlaceholderApi jsonPlaceholderApi;
    private JsonPlaceholderApi jsonPlaceholderApi_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_globe_screen);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = (MapView) findViewById(R.id.map_globe_view);
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);

        centerOfScreenIndicator = findViewById(R.id.centerOfScreenIndicator);
        showPathsFromHere = findViewById(R.id.showPathsFromHere);
        showPathsFromHere.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.globe_recyclerView);
        mAdapter = new GlobePostsAdapter(posts);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        if (Geocoder.isPresent()) {
            System.out.println("isPresent: true");
        }else {
            System.out.println("isPresent: false");
        }

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

    private void calculateLocationOnScreen() {
        if(mapCircle != null){
            mapCircle.remove();
        }

        VisibleRegion visibleRegion = mapObj.getProjection().getVisibleRegion();

        float[] distanceDiagonally = new float[1];

        LatLng farLeft = visibleRegion.farLeft; // top-right corner
        LatLng nearRight = visibleRegion.nearRight; // bottom-right corner

        Location.distanceBetween(
                farLeft.latitude,
                farLeft.longitude,
                nearRight.latitude,
                nearRight.longitude,
                distanceDiagonally
        );

        double radiusInMeters = distanceDiagonally[0] / 2;
        radiusInKm = radiusInMeters / 1000;
        mapCircle = mapObj.addCircle(new CircleOptions()
                //.center(new LatLng(latitude, longitude))
                .center(mapObj.getCameraPosition().target)
                .radius(radiusInMeters/2)
                .strokeColor(0xFF303F9F)
                .fillColor(Color.argb(70, 61, 81,181)));
//        centerOfScreenIndicator.setText("KMs: " + Double.toString(radiusInKm/2));

        Double cameraTempLat = mapObj.getCameraPosition().target.latitude;
        Double cameraTempLng = mapObj.getCameraPosition().target.longitude;

        if(!cameraLongitude.equals(cameraTempLng) || !cameraLatitude.equals(cameraTempLat)) {
            cameraLatitude = mapObj.getCameraPosition().target.latitude;
            cameraLongitude = mapObj.getCameraPosition().target.longitude;

            posts.clear();
            mAdapter.notifyDataSetChanged();

            getFirstPosts();
            coordinatesChanged = true;

//                    centerOfScreenIndicator.setText(mapObj.getCameraPosition().toString());
        } else {
            Toast.makeText(getApplicationContext(), "This region was already loaded.\nPick a different location on the map!", Toast.LENGTH_SHORT).show();
            coordinatesChanged = false;
        }
    }

    private void getMorePosts() {
        Call<GlobePosts> call = jsonPlaceholderApi.getMorePosts(
                foundCountry,
                prevPostId,
                radiusInKm/2,
                cameraLatitude,
                cameraLongitude
        );

        enqueuePostsCall(call, "All posts have been displayed!");
    }

    private void enqueuePostsCall(Call<GlobePosts> call, String s) {
        call.enqueue(new Callback<GlobePosts>() {
            @Override
            public void onResponse(Call<GlobePosts> call, Response<GlobePosts> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Invalid response", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.code() == 204) {
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                    return;
                }

                GlobePosts postsData = response.body();
                posts.addAll(postsData.getPosts());
                prevPostId = postsData.getPrevPostId();

                mAdapter.notifyDataSetChanged();
                isLoading = false;

//                centerOfScreenIndicator.append("\n@ Loaded posts: " +posts.size()+"\n");
            }

            @Override
            public void onFailure(Call<GlobePosts> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFirstPosts() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://jsonplaceholder.typicode.com/")
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        jsonPlaceholderApi_2 = retrofit.create(JsonPlaceholderApi.class);

        String latLng = cameraLatitude.toString() + "," + cameraLongitude.toString();
        Call<ReverseGeocoding> call_2 = jsonPlaceholderApi_2.getCountry(
                latLng,
                "AIzaSyCOQ_ozH1XcGufyOqRnKjr3IUdU5YMdUl4"
        );

        call_2.enqueue(new Callback<ReverseGeocoding>() {
            @Override
            public void onResponse(Call<ReverseGeocoding> call, Response<ReverseGeocoding> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Invalid response", Toast.LENGTH_SHORT).show();
                    return;
                }

                ReverseGeocoding res = response.body();

                if (res.getStatus().equals("ZERO_RESULTS")) {
                    Toast.makeText(getApplicationContext(), "The center of the screen must be a country!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (res.getStatus().equals("OK")) {
                    System.out.println("res IS: " + res.getPlus_code().get("compound_code"));
                    if (res.getPlus_code().get("compound_code") == null) {
                        String[] parsedAddress = res.getResults().get(0).getFormatted_address().split(" ");
                        foundCountry = parsedAddress[parsedAddress.length - 1 ];
                        System.out.println("[1] Extracted country: " + "[" + foundCountry + "]");
                    } else {
                        String[] parsedLocation = res.getPlus_code().get("compound_code").split(" ");
                        foundCountry = parsedLocation[parsedLocation.length - 1];
                        System.out.println("[2] Extracted country: " + "[" + foundCountry + "]");
                    }

                    Call<GlobePosts> callGetFirstPosts = jsonPlaceholderApi.getFirstPosts(
                            foundCountry,
                            radiusInKm/2,
                            cameraLatitude,
                            cameraLongitude
                    );

                    enqueuePostsCall(callGetFirstPosts, "No posts found in this region!");
                    return;
                }
            }

            @Override
            public void onFailure(Call<ReverseGeocoding> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("FAILED: " + t.getMessage());
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
//                Toast.makeText(getApplicationContext(), mapObj.getCameraPosition().toString(), Toast.LENGTH_LONG).show();
            }
        });

//        LatLng sydney = new LatLng(-34, 151);
//        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//
//        LatLng xa = new LatLng(-32, 150);
//        map.addMarker(new MarkerOptions().position(xa).title("Marker in Xa"));

        LatLng x2;
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        GPSTracker gps = new GPSTracker(this);
//        if(gps.canGetLocation()){
//            x2 = new LatLng(gps.getLatitude(), gps.getLongitude());
//            map.addMarker(new MarkerOptions().position(x2).title("HELLO!!!!"));
//            map.moveCamera(CameraUpdateFactory.newLatLng(x2));
//
//        }else {
//            map.moveCamera(CameraUpdateFactory.newLatLng(xa));
//        }
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

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.showPathsFromHere:
                calculateLocationOnScreen();
                break;

            default:
                break;

        }
    }
}