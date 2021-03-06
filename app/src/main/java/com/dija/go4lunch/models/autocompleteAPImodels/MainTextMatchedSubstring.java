package com.dija.go4lunch.models.autocompleteAPImodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MainTextMatchedSubstring implements Serializable {

    @SerializedName("length")
    private Long mLength;
    @SerializedName("offset")
    private Long mOffset;

    public Long getLength() {
        return mLength;
    }

    public void setLength(Long length) {
        mLength = length;
    }

    public Long getOffset() {
        return mOffset;
    }

    public void setOffset(Long offset) {
        mOffset = offset;
    }
}
