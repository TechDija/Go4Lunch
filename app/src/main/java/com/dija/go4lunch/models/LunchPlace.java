package com.dija.go4lunch.models;

import com.dija.go4lunch.models.nearbyAPImodels.Result;

import java.util.List;

public class LunchPlace {

    private String resultId;
    private String userId;

    public LunchPlace() {
    }

    public LunchPlace(String resultId, String userId) {
        resultId = resultId;
        userId = userId;
    }

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        resultId = resultId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        userId = userId;
    }


}