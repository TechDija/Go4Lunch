package com.dija.go4lunch.viewmodel;

import android.app.Application;
import android.app.DownloadManager;
import android.content.Context;
import android.database.MatrixCursor;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dija.go4lunch.repositories.MapRepository;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.List;
import java.util.concurrent.Executor;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class MapViewModel extends ViewModel {
    private GoogleMap mMap;
    private final MapRepository mMapRepository;
    private final Executor mExecutor;
    private String queryString;
    private PlacesClient mPlacesClient;
    private List<AutocompletePrediction> mPredictionList;

    public MapViewModel(MapRepository mapRepository, Executor executor) {
        mMapRepository = mapRepository;
        mExecutor = executor;
    }

    public LiveData<String> getSearchString(String queryString) {
        this.queryString = queryString;
        MutableLiveData<String> queryStringLiveData = new MutableLiveData<>();
        queryStringLiveData.setValue(queryString);
        return queryStringLiveData;
    }


    public void setPredictionList(List<AutocompletePrediction> mPredictionList) {
        this.mPredictionList = mPredictionList;
    }




    //--------------------------

    public MutableLiveData<LocationSettingsRequest.Builder> getLocationSettingsRequestBuilderLiveData() {
        return mMapRepository.getLocationSettingsRequestBuilderLiveData();
    }

    public void getDeviceLocation(FusedLocationProviderClient mFusedLocation, GoogleMap mMap) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mMapRepository.getDeviceLocation(mFusedLocation, mMap);
            }
        });
    }

    public LiveData<FindAutocompletePredictionsRequest> getPredictionsRequestLiveData(String newText) {
        return mMapRepository.getPredictionsRequestLiveData(newText);
    }

    public void setPlacesClient(PlacesClient placesClient) {
        this.mPlacesClient = placesClient;
    }

    public PlacesClient getPlacesClient() {
        return mPlacesClient;
    }

    public void AutoCompletePredictionsResponse(Task<FindAutocompletePredictionsResponse> task) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mMapRepository.AutoCompletePredictionsResponse(task);
            }
        });
    }

    public MatrixCursor getCursor() {
        return mMapRepository.getCursor();
    }

    public void getMap(GoogleMap map) {
        this.mMap = map;
    }

    public void moveCameraTo(LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 30));
    }
}








