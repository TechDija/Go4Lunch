package com.dija.go4lunch.models;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.Exclude;

public class User {
    private String uid;
    private String username;
    private String lunch;
    @Nullable private String urlPicture;

    public User() { }

    public User(String uid, String username, String lunch, @Nullable String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.lunch = lunch;
        this.urlPicture = urlPicture;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }

    public String getLunch() {
        return lunch;
    }

    public void setLunch(String lunch) {
        this.lunch = lunch;
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }
}
