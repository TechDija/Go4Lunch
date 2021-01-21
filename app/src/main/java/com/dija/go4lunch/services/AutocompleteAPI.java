package com.dija.go4lunch.services;

import com.dija.go4lunch.models.autocompleteAPImodels.AutocompleteResult;
import com.dija.go4lunch.models.nearbyAPImodels.PlacesResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AutocompleteAPI {
    @GET("place/autocomplete/json")
     Call<AutocompleteResult> getAutocomplete(
            @Query("location") String location,
            @Query("radius") int radius,
            @Query("type") String type,
            @Query("input") String input,
            @Query("key") String key);
}
