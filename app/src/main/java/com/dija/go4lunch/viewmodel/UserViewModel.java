package com.dija.go4lunch.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dija.go4lunch.models.User;
import com.dija.go4lunch.models.nearbyAPImodels.Result;
import com.dija.go4lunch.repositories.UserRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.concurrent.Executor;

public class UserViewModel extends ViewModel {
    private final UserRepository mUserRepository;
    private final Executor mExecutor;

    private MutableLiveData<Boolean> isLunchPlace;


    // region CONSTRUCTOR
    public UserViewModel(UserRepository userRepository, Executor executor) {
        mUserRepository = userRepository;
        mExecutor = executor;
    }
    // endregion

    public LiveData<FirebaseUser> getCurrentUser() {
        return mUserRepository.getCurrentUser();
    }

    public LiveData<String> getCurrentUserPicture() {
        return mUserRepository.getCurrentUserPicture();
    }

    public LiveData<String> getCurrentUserName() {
        return mUserRepository.getCurrentUserName();
    }

    public LiveData<String> getCurrentUserEmail() {
        return mUserRepository.getCurrentUserEmail();
    }

    public void createUserInFirestore() {
        mExecutor.execute(() -> mUserRepository.createUserInFirestore());
    }

    // TODO: store the last available value of islunchplace in the viewmodel
    public LiveData<Boolean> isLunchPlaceLiveData(String restaurantId) {
        return mUserRepository.getIsLunchPlaceLiveData(restaurantId);
    }

    public void updateLunchplaceInFirestore(String restaurantId, String restaurantName, String restaurantAdress) {
   mExecutor.execute(new Runnable() {
       @Override
       public void run() {
           mUserRepository.updateOrDeleteLunchplaceInFirestore(restaurantId, restaurantName, restaurantAdress);
       }
   });
    }

    public MutableLiveData<List<User>> getUsersFromLunchPlace(String restaurantId){
     return mUserRepository.getUserIdFromLunchPlaceListened(restaurantId);
    }

    public MutableLiveData<String> getLunchPlaceName(String userId){
        return mUserRepository.getLunchplaceName(userId);

    }

    public void saveLunchplaceLocally(Context context, Result result){
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mUserRepository.saveLunchplaceLocally(context, result);
            }
        });
    }
    public Result retrieveUsersLunchplace(Context context){
        return mUserRepository.retrieveUsersLunchplace(context);
    }


}
