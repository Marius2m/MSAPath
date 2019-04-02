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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

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

    private FirebaseFunctions mFunctions;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

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

        showPathsFromHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mapCircle != null){
                    mapCircle.remove();
                }

                textView4.setText(mapObj.getCameraPosition().toString());

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
                mapCircle = mapObj.addCircle(new CircleOptions()
                        //.center(new LatLng(latitude, longitude))
                        .center(mapObj.getCameraPosition().target)
                        .radius(radiusInMeters/2)
                        .strokeColor(0xFF303F9F)
                        .fillColor(Color.argb(70, 61, 81,181)));
                textView5.setText(Double.toString(radiusInMeters/2));

                String text = "mariusTest";
                Map<String, Object> data = new HashMap<>();
                data.put("text", "Hi Firebase!");

                addNumbers(3,5)
                        .addOnCompleteListener(new OnCompleteListener<Integer>() {
                            @Override
                            public void onComplete(@NonNull Task<Integer> task) {
                                if (!task.isSuccessful()) {
                                    Exception e = task.getException();
                                    if (e instanceof FirebaseFunctionsException) {
                                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;

                                        // Function error code, will be INTERNAL if the failure
                                        // was not handled properly in the function call.
                                        FirebaseFunctionsException.Code code = ffe.getCode();

                                        // Arbitrary error details passed back from the function,
                                        // usually a Map<String, Object>.
                                        Object details = ffe.getDetails();
                                    }

                                    // [START_EXCLUDE]
                                    Log.d("SOME", "addNumbers:onFailure", e);
                                    return;
                                    // [END_EXCLUDE]
                                }

                                // [START_EXCLUDE]
                                Integer result = task.getResult();
                                Log.d("SOME2",String.valueOf(result));
                                // [END_EXCLUDE]
                            }
                        });

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