package com.dija.go4lunch.repositories;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dija.go4lunch.api.FirestoreLunchplaceHelper;
import com.dija.go4lunch.api.FirestoreSettingsHelper;
import com.dija.go4lunch.api.FirestoreUserHelper;
import com.dija.go4lunch.models.User;
import com.dija.go4lunch.models.nearbyAPImodels.Result;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class UserRepository {
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    MutableLiveData<Boolean> isLunchplaceLiveData = new MutableLiveData<>();
    private final int DEFAULT_RADIUS = 5;



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
            FirestoreSettingsHelper.createSetting(firebaseUser.getUid(), DEFAULT_RADIUS);
            if (firebaseUser.getPhotoUrl() != null) {
                FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        String urlPicture = firebaseUser.getPhotoUrl().toString();
                        String username = firebaseUser.getDisplayName();
                        String uid = firebaseUser.getUid();
                        FirestoreUserHelper.createUser(uid, username, urlPicture, s);
                    }
                });
            }

        }
    }

    /////////////////////LUNCHPLACES/////////////////////////

    public void updateOrDeleteLunchplaceInFirestore(String restaurantId, String restaurantName, String restaurantAdress) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirestoreLunchplaceHelper.getLunchPlace(currentUserId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().get("resultId") != null && task.getResult().get("resultId").toString().contains(restaurantId)) {
                    FirestoreLunchplaceHelper.deleteLunchPlace(currentUserId);
                    getUserIdFromLunchPlaceListened(restaurantId);

                } else {
                    FirestoreLunchplaceHelper.createLunchPlace(restaurantId, currentUserId, restaurantName, restaurantAdress);
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

    public MutableLiveData<List<User>> getUserIdFromLunchPlaceListened(String restaurantId) {
        List<User> usersFromLunchPlaces = new ArrayList<>();
        MutableLiveData<List<User>> _usersFromLunchPlaces = new MutableLiveData<>();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirestoreLunchplaceHelper.getLunchPlacesCollection()
                    .whereEqualTo("resultId", restaurantId)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            usersFromLunchPlaces.clear();
                            if (value.isEmpty()){
                                    _usersFromLunchPlaces.postValue(new ArrayList<>());
                            } else {
                                for (DocumentSnapshot documentSnapshot : value) {

                                    String userId = documentSnapshot.getId();
                                    FirestoreUserHelper.getUser(userId).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            User user = task.getResult().toObject(User.class);
                                            usersFromLunchPlaces.add(user);
                                            _usersFromLunchPlaces.postValue(usersFromLunchPlaces);
                                        }
                                    });

                                }
                            }
                        }
                    });
            return _usersFromLunchPlaces;
        } else {
            return null;
        }
    }


    public MutableLiveData<String> getLunchplaceName (String userId) {
        MutableLiveData<String> _getlunchplaceName = new MutableLiveData<>();
        FirestoreLunchplaceHelper.getLunchPlace(userId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().get("lunchplaceName") != null) {
                   _getlunchplaceName.postValue((String) task.getResult().get("lunchplaceName"));
                }
            }
        });
        return _getlunchplaceName;
    }

    public void saveLunchplaceLocally(Context context, Result result){
        SharedPreferences mPrefs = context.getSharedPreferences("", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(result); // myObject - instance of MyObject
        prefsEditor.putString("MyResult", json);
        prefsEditor.commit();
    }

    public Result retrieveUsersLunchplace(Context context){
        SharedPreferences mPrefs = context.getSharedPreferences("", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyResult", "error");
        Result result = gson.fromJson(json, Result.class);
        return result;
    }


}
