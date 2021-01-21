package com.dija.go4lunch.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dija.go4lunch.api.FirestoreUserHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class UserRepository {
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    // region CONSTRUCTOR
    public UserRepository(FirebaseAuth firebaseAuth) {
        this.mFirebaseAuth = firebaseAuth;
    }
    // endregion

    public LiveData<FirebaseUser> getCurrentUser() {
        MutableLiveData<FirebaseUser> currentUserLiveData = new MutableLiveData<>();
        currentUserLiveData.setValue(mFirebaseAuth.getCurrentUser());
        return currentUserLiveData;
    }

    public LiveData<String> getCurrentUserPicture() {
        String pictureUrl = null;
        MutableLiveData<String> currentUserPictureLiveData = new MutableLiveData<>();
        for (UserInfo userInfo : mFirebaseAuth.getCurrentUser().getProviderData()) {
            if (userInfo.getPhotoUrl() != null) {
                pictureUrl = userInfo.getPhotoUrl().toString();
            }
        }
        currentUserPictureLiveData.setValue(pictureUrl);
        return currentUserPictureLiveData;
    }

    public LiveData<String> getCurrentUserName() {
        MutableLiveData<String> currentUserNameLiveData = new MutableLiveData<>();
        currentUserNameLiveData.setValue(mFirebaseAuth.getCurrentUser().getDisplayName());
        return currentUserNameLiveData;
    }

    public LiveData<String> getCurrentUserEmail() {
        String email = null;
        MutableLiveData<String> currentUserEmailLiveData = new MutableLiveData<>();
        for (UserInfo userInfo : mFirebaseAuth.getCurrentUser().getProviderData()) {
            email = userInfo.getEmail();
        }
        currentUserEmailLiveData.setValue(email);
        return currentUserEmailLiveData;
    }

    public void createUserInFirestore() {
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            if (firebaseUser.getPhotoUrl() != null) {
                String urlPicture = firebaseUser.getPhotoUrl().toString();
                String username = firebaseUser.getDisplayName();
                String uid = firebaseUser.getUid();
                FirestoreUserHelper.createUser(uid, username, "", urlPicture);
            }
        }
    }


}
