package com.dija.go4lunch.models.autocompleteAPImodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AutocompleteResult implements Serializable {

    @SerializedName("predictions")
    private List<Prediction> mPredictions;
    @SerializedName("status")
    private String mStatus;

    public List<Prediction> getPredictions() {
        return mPredictions;
    }

    public void setPredictions(List<Prediction> predictions) {
        mPredictions = predictions;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }
}
