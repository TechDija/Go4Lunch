package com.dija.go4lunch.services;

import com.dija.go4lunch.models.nearbyAPImodels.PlacesResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleMapAPI {
    @GET ("place/nearbysearch/json")
    Call<PlacesResult> getNearBy(
            @Query("location") String location,
            @Query("radius") int radius,
            @Query("type") String type,
            @Query("keyword") String keyword,
            @Query("key") String key
    );
}
