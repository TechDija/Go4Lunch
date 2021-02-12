package com.dija.go4lunch.models.DetailAPImodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResultAPIDetail implements Serializable {
    @SerializedName("formatted_phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("website")
    @Expose
    private String website;

    public ResultAPIDetail(String phoneNumber, String website) {
        this.phoneNumber = phoneNumber;
        this.website = website;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
