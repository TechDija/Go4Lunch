package com.dija.go4lunch.viewmodel;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.dija.go4lunch.models.nearbyAPImodels.Result;
import com.dija.go4lunch.repositories.MapRepository;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.concurrent.Executor;

public class MapViewModel extends ViewModel {
    private GoogleMap mMap;
    private final MapRepository mMapRepository;
    private final Executor mExecutor;
    private String queryString;

    private LiveData<Location> lastKnownLocationLiveData = new MutableLiveData<>();


    private MutableLiveData<Bitmap> photoLiveData;

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

    public void getMap(GoogleMap map) {
        this.mMap = map;
    }

    public void moveCameraTo(LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 30));
    }

    public MutableLiveData<List<Result>> getNearBySearchResultsFromLocation(FusedLocationProviderClient mFusedLocationProviderClient, String key) {
        return mMapRepository.getNearBySearchResultsFromLocation(mFusedLocationProviderClient, key);
    }

    public LiveData<Cursor> getAutocompleteCursor(FusedLocationProviderClient mFusedLocationProviderClient, String key, String query) {
        return mMapRepository.getAutocompleteCursor(mFusedLocationProviderClient, key, query);
    }

    public void getQuery(String query) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mMapRepository.getQuery(query);
            }
        });
    }

    public LiveData<Location> lastKnownLocation(FusedLocationProviderClient mFusedLocationProviderClient) {
        return mMapRepository.getLastKnownLocation(mFusedLocationProviderClient);

    }

    public LiveData<String> getPhoneNumber (String placeId, String key ){
        return mMapRepository.getPhoneNumberOfPlace(placeId, key);
    }

    public LiveData<String> getWebsite (String placeId, String key ){
        return mMapRepository.getWebsiteOfPlace(placeId, key);
    }

    public LiveData<Boolean> isRestaurantInLunchplaces(String restaurantId){
        return mMapRepository.isRestaurantInLunchplaces(restaurantId);
    }

    public LiveData<Integer> getNumberOfWorkmates (String restaurantId){
        return mMapRepository.getNumberOfWorkmates(restaurantId);
    }

}








