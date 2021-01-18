package com.dija.go4lunch.repositories;

import android.annotation.SuppressLint;
import android.database.MatrixCursor;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;

import java.util.ArrayList;
import java.util.List;

public class MapRepository {

    private LocationCallback mLocationCallback;
    private final float DEFAULT_ZOOM = 30;
    private AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
    private List<AutocompletePrediction> mPredictionList;
    private List<String> suggestionList = new ArrayList<>();

    public MapRepository() {
    }

    public static MutableLiveData<LocationSettingsRequest.Builder> getLocationSettingsRequestBuilderLiveData() {
        MutableLiveData<LocationSettingsRequest.Builder> builderLiveData = new MutableLiveData<>();
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builderLiveData.setValue(builder);
        return builderLiveData;
    }

    @SuppressLint("MissingPermission")
    public void getDeviceLocation(FusedLocationProviderClient mFusedLocationProviderClient, GoogleMap mMap) {
        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location mLastKnownLocation = task.getResult();
                    if (mLastKnownLocation != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                    } else {
                        final LocationRequest locationRequest = LocationRequest.create();
                        locationRequest.setInterval(10000);
                        locationRequest.setFastestInterval(5000);
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        mLocationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                if (locationResult == null) {
                                    return;
                                }
                                final Location mLastKnownLocation = locationResult.getLastLocation();
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
                            }
                        };
                        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
                    }
                }
            }
        });
    }

    public LiveData<FindAutocompletePredictionsRequest> getPredictionsRequestLiveData(String newText) {
        MutableLiveData<FindAutocompletePredictionsRequest> LiveDataPredictionRequest = new MutableLiveData<>();
        FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                .setCountry("FR")
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setSessionToken(token)
                .setQuery(newText)
                .build();
        LiveDataPredictionRequest.setValue(predictionsRequest);
        return LiveDataPredictionRequest;
    }

    public void AutoCompletePredictionsResponse(Task<FindAutocompletePredictionsResponse> task) {
        FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
        if (predictionsResponse != null) {
            mPredictionList = predictionsResponse.getAutocompletePredictions();
            for (int i = 0; i < mPredictionList.size(); i++) {
                AutocompletePrediction prediction = mPredictionList.get(i);
                if (prediction.getPlaceTypes().contains(Place.Type.RESTAURANT)) {
                    suggestionList.add(prediction.toString());
                }
            }

        }
    }

    public MatrixCursor getCursor() {
        String[] columnsNames = {"_id", "name"};
        MatrixCursor cursor = new MatrixCursor(columnsNames);
        String[] row = new String[2];
        int id = 0;
        for (String item : suggestionList) {
            row[0] = Integer.toString(id++);
            row[1] = item;
            cursor.addRow(row);
        }
        return cursor;
    }

}
