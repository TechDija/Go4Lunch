package com.dija.go4lunch.models;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.Exclude;

public class User {
    private String uid;
    private String username;
    @Nullable private String urlPicture;
    private String token;

    public User() { }

    public User(String uid, String username, @Nullable String urlPicture, String token) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.token = token;
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


    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
