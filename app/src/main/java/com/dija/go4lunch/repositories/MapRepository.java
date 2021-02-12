package com.dija.go4lunch.repositories;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.location.Location;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dija.go4lunch.models.DetailAPImodel.DetailResults;
import com.dija.go4lunch.models.autocompleteAPImodels.AutocompleteResult;
import com.dija.go4lunch.models.autocompleteAPImodels.Prediction;
import com.dija.go4lunch.models.nearbyAPImodels.PlacesResult;
import com.dija.go4lunch.models.nearbyAPImodels.Result;
import com.dija.go4lunch.services.APIClient;
import com.dija.go4lunch.services.AutocompleteAPI;
import com.dija.go4lunch.services.DetailAPI;
import com.dija.go4lunch.services.GoogleMapAPI;

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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapRepository {

    private LocationCallback mLocationCallback;
    private final float DEFAULT_ZOOM = 12;
    private List<String> suggestionList = new ArrayList<>();
    MutableLiveData<Location> lastKnownLocationLiveData = new MutableLiveData<>();
    private Location deviceLocation;
    private String keyword;
    String currentLocation = "";
    private static final String[] sAutocompleteColNames = new String[]{
            BaseColumns._ID,                         // necessary for adapter
            SearchManager.SUGGEST_COLUMN_TEXT_1      // the full search term
    };

    public MapRepository() {
    }

    // getting the builder for the MapsFragment
    public MutableLiveData<LocationSettingsRequest.Builder> getLocationSettingsRequestBuilderLiveData() {
        MutableLiveData<LocationSettingsRequest.Builder> builderLiveData = new MutableLiveData<>();
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builderLiveData.setValue(builder);
        return builderLiveData;
    }

    // getting device location for the MapsFragment
    @SuppressLint("MissingPermission")
    public void getDeviceLocation(FusedLocationProviderClient mFusedLocationProviderClient, GoogleMap mMap) {
        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location mLastKnownLocation = task.getResult();
                    if (mLastKnownLocation != null) {
                        lastKnownLocationLiveData.postValue(mLastKnownLocation);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        currentLocation = mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude();
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


    // getting NearbySearch result for the ListFragment
    @SuppressLint("MissingPermission")
    public MutableLiveData<List<Result>> getNearBySearchResultsFromLocation(FusedLocationProviderClient mFusedLocationProviderClient, String key) {
        MutableLiveData<List<Result>> nearBySearchResultsLiveData = new MutableLiveData<>();
        int radius = 5000;
        String type = "restaurant";
        keyword = ""; // obtaining the search from
        GoogleMapAPI googleMapAPI = APIClient.getClient().create(GoogleMapAPI.class);
        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location mLastKnownLocation = task.getResult();
                    if (mLastKnownLocation != null) {
                        currentLocation = mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude();
                        googleMapAPI.getNearBy(currentLocation, radius, type, keyword, key)
                                .enqueue(new Callback<PlacesResult>() {
                                    @Override
                                    public void onResponse(Call<PlacesResult> call, Response<PlacesResult> response) {
                                        if (response.isSuccessful()) {
                                            List<Result> results = response.body().getResults();
                                            nearBySearchResultsLiveData.setValue(results);
                                        } else {
                                            Log.e("TAG", "something went wrong");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<PlacesResult> call, Throwable t) {
                                        Log.e("TAG", "something went wrong");
                                    }
                                });
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
                                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
                            }
                        };
                        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
                    }
                }
            }
        });
        return nearBySearchResultsLiveData;
    }

    @SuppressLint("MissingPermission")
    public MutableLiveData<Cursor> getAutocompleteCursor(FusedLocationProviderClient mFusedLocationProviderClient, String key, String query) {
        MutableLiveData<Cursor> cursorLiveData = new MutableLiveData<>();
        MatrixCursor cursor = new MatrixCursor(sAutocompleteColNames);
        int radius = 5000;
        String type = "establishment";
        AutocompleteAPI autocompleteAPI = APIClient.getClient().create(AutocompleteAPI.class);
        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location mLastKnownLocation = task.getResult();
                    if (mLastKnownLocation != null) {
                        currentLocation = mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude();
                        autocompleteAPI.getAutocomplete(currentLocation, radius, type, query, key)
                                .enqueue(new Callback<AutocompleteResult>() {
                                    @Override
                                    public void onResponse(Call<AutocompleteResult> call, Response<AutocompleteResult> response) {
                                        if (response.isSuccessful()) {
                                            List<Prediction> predictions = response.body().getPredictions();
                                            for (int i = 0; i < predictions.size(); i++) {
                                                String prediction = predictions.get(i).getStructuredFormatting().getMainText();
                                                Object[] row = new Object[]{i, prediction};
                                                if (predictions.get(i).getTypes().contains("restaurant")) {
                                                    cursor.addRow(row);
                                                }
                                            }
                                            cursorLiveData.setValue(cursor);
                                        } else {
                                            Log.e("TAG", "something went wrong");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<AutocompleteResult> call, Throwable t) {
                                        Log.e("TAG", "something went wrong");
                                    }
                                });
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
                                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
                            }
                        };
                        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
                    }
                }
            }
        });
        return cursorLiveData;
    }

    public void getQuery(String query) {
        if (query != null && query.length() > 0) {
            this.keyword = query;
        }
    }

    @SuppressLint("MissingPermission")
    public LiveData<Location> getLastKnownLocation(FusedLocationProviderClient mFusedLocationProviderClient) {
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        Location mLastKnownLocation = task.getResult();
                        lastKnownLocationLiveData.postValue(mLastKnownLocation);
                        if (mLastKnownLocation != null) {
                            lastKnownLocationLiveData.postValue(mLastKnownLocation);
                            currentLocation = mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude();
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
                                    lastKnownLocationLiveData.postValue(mLastKnownLocation);
                                    mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
                                }
                            };
                            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
                            lastKnownLocationLiveData.postValue(mLastKnownLocation);
                        }
                    }
                }
            });
        return lastKnownLocationLiveData;
    }

    public LiveData<String> getPhoneNumberOfPlace(String placeId, String key){
        MutableLiveData<String> phoneNumberLiveData = new MutableLiveData<>();
        DetailAPI detailAPI = APIClient.getClient().create(DetailAPI.class);
        String fields = "formatted_phone_number";
        detailAPI.getDetails(fields, placeId, key).enqueue(new Callback<DetailResults>() {
            @Override
            public void onResponse(Call<DetailResults> call, Response<DetailResults> response) {
                if(response.isSuccessful()){
                    String phoneNumber = response.body().getResultAPIDetail().getPhoneNumber();
                    phoneNumberLiveData.postValue(phoneNumber);
                }
            }

            @Override
            public void onFailure(Call<DetailResults> call, Throwable t) {
                Log.e("TAG", "something went wrong");
            }
        });
        return phoneNumberLiveData;
    }

    public LiveData<String> getWebsiteOfPlace(String placeId, String key){
        MutableLiveData<String> websiteLiveData = new MutableLiveData<>();
        DetailAPI detailAPI = APIClient.getClient().create(DetailAPI.class);
        String fields = "website";
        detailAPI.getDetails(fields, placeId, key).enqueue(new Callback<DetailResults>() {
            @Override
            public void onResponse(Call<DetailResults> call, Response<DetailResults> response) {
                if(response.isSuccessful()){
                    String website = response.body().getResultAPIDetail().getWebsite();
                    websiteLiveData.postValue(website);
                }
            }

            @Override
            public void onFailure(Call<DetailResults> call, Throwable t) {
                Log.e("TAG", "something went wrong");
            }
        });
        return websiteLiveData;
    }
}










