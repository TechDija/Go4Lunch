package com.dija.go4lunch.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dija.go4lunch.api.FirestoreLunchplaceHelper;
import com.dija.go4lunch.api.FirestoreUserHelper;
import com.dija.go4lunch.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    MutableLiveData<Boolean> isLunchplaceLiveData = new MutableLiveData<>();


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
                FirestoreUserHelper.createUser(uid, username, urlPicture);
            }
        }
    }

    /////////////////////LUNCHPLACES/////////////////////////
    public void updateLunchplaceInFirestore(String restaurantId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirestoreLunchplaceHelper.getLunchPlace(currentUserId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.getResult().exists()) {
                    FirestoreLunchplaceHelper.createLunchPlace(restaurantId, currentUserId);
                } else {
                    FirestoreLunchplaceHelper.deleteLunchPlace(currentUserId);
                    FirestoreLunchplaceHelper.createLunchPlace(restaurantId, currentUserId);
                }
            }
        });
    }

    public void isLunchplaceLiveData(String restaurantId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirestoreLunchplaceHelper.getLunchPlace(currentUserId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists() && document.get("resultId") != null
                        && document.get("resultId").toString().contains(restaurantId)) {
                    isLunchplaceLiveData.postValue(true);
                } else {
                    isLunchplaceLiveData.setValue(false);
                }
            }
        });
    }

    public LiveData<Boolean> getIsLunchPlaceLiveData(String restaurantId) {
        isLunchplaceLiveData(restaurantId);
        return isLunchplaceLiveData;
    }

    public void deleteLunchplaceFromFirestore() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirestoreLunchplaceHelper.deleteLunchPlace(currentUserId);
    }

    public MutableLiveData<List<User>> getUserIdFromLunchPlace(String restaurantId) {
        List<String> userIds = new ArrayList<>();
        MutableLiveData<List<String>> _userIds = new MutableLiveData();
        List<User> usersFromLunchPlaces = new ArrayList<>();
        MutableLiveData<List<User>> _usersFromLunchPlaces = new MutableLiveData<>();
        FirestoreLunchplaceHelper.getLunchPlacesCollection()
                .whereEqualTo("resultId", restaurantId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                String userId = (String) documentSnapshot.getId();
                                userIds.add(userId);
                                FirestoreUserHelper.getUser(userId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            User user = task.getResult().toObject(User.class);
                                            usersFromLunchPlaces.add(user);
                                            _usersFromLunchPlaces.postValue(usersFromLunchPlaces);
                                        }
                                    }
                                });
                            }
                        }
                    }
                });

        return _usersFromLunchPlaces;
    }
}
