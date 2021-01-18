package com.dija.go4lunch.ui;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.dija.go4lunch.R;
import com.dija.go4lunch.databinding.FragmentMapsBinding;
import com.dija.go4lunch.injections.Injection;
import com.dija.go4lunch.injections.MapViewModelFactory;
import com.dija.go4lunch.viewmodel.MapViewModel;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class MapsFragment extends BaseFragment<FragmentMapsBinding> {

    private MapViewModel mMapViewModel;
    private static final int REQ_PERMISSION = 103;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMapViewModel.getMap(mMap);
            if (checkPermission()) {
                mMap.setMyLocationEnabled(true);
            } else askPermission();
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            setActualLocation();
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        configureMapViewModel();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        if (!Places.isInitialized()) {
        Places.initialize(requireContext(), "AIzaSyAOzdywOxstlvYwyVPLwXlGlA42yb6p6uc");}
        PlacesClient placesClient = Places.createClient(requireContext());
        mMapViewModel.setPlacesClient(placesClient);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // Permission granted
                if (checkPermission())
                    mMap.setMyLocationEnabled(true);
            } else { // Permission denied
                Toast.makeText(getContext(), "You denied this service", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                mMapViewModel.getDeviceLocation(mFusedLocationProviderClient, mMap);
            }
        }
    }

    // region ACTIONS
    //----------------------
    //ACTIONS
    //----------------------

    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQ_PERMISSION
        );
    }

    private void configureMapViewModel() {
        MapViewModelFactory mMapViewModelFactory = Injection.provideMapViewModelFactory(requireContext());
        this.mMapViewModel = new ViewModelProvider(this, mMapViewModelFactory).get(MapViewModel.class);
    }

    public void setActualLocation() {
        SettingsClient settingsClient = LocationServices.getSettingsClient(requireContext());
        mMapViewModel.getLocationSettingsRequestBuilderLiveData().observe(this, new Observer<LocationSettingsRequest.Builder>() {
            @Override
            public void onChanged(LocationSettingsRequest.Builder builder) {
                Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
                task.addOnSuccessListener(requireActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        mMapViewModel.getDeviceLocation(mFusedLocationProviderClient, mMap);
                    }
                });

                task.addOnFailureListener(requireActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ResolvableApiException) {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            try {
                                startIntentSenderForResult(resolvable.getResolution().getIntentSender(), 51,
                                        null, 0, 0, 0, null);
                            } catch (IntentSender.SendIntentException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }
    //endregion action
}