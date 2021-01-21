package com.dija.go4lunch.models.autocompleteAPImodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Term implements Serializable {

    @SerializedName("offset")
    private Long mOffset;
    @SerializedName("value")
    private String mValue;

    public Long getOffset() {
        return mOffset;
    }

    public void setOffset(Long offset) {
        mOffset = offset;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }
}
