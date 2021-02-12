package com.dija.go4lunch.models.DetailAPImodel;

import com.dija.go4lunch.models.nearbyAPImodels.Result;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DetailResults implements Serializable {
    @SerializedName("html_attributions")
    @Expose
    private List<Object> htmlAttributions = new ArrayList<Object>();;
    @SerializedName("result")
    @Expose
    private ResultAPIDetail mResultAPIDetail;
    @SerializedName("status")
    @Expose
    private String status ;

    public DetailResults(List<Object> htmlAttributions, ResultAPIDetail resultAPIDetail, String status) {
        this.htmlAttributions = htmlAttributions;
        mResultAPIDetail = resultAPIDetail;
        this.status = status;
    }

    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public ResultAPIDetail getResultAPIDetail() {
        return mResultAPIDetail;
    }

    public void setResultAPIDetail(ResultAPIDetail resultAPIDetail) {
        mResultAPIDetail = resultAPIDetail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
