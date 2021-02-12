package com.dija.go4lunch.services;

import com.dija.go4lunch.models.DetailAPImodel.DetailResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DetailAPI {

    @GET("place/details/json")
    Call<DetailResults> getDetails(
            @Query("fields") String fields,
            @Query("place_id") String placeId,
            @Query("key") String key);
}

