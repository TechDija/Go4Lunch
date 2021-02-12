package com.dija.go4lunch.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dija.go4lunch.api.FirestoreUserHelper;
import com.dija.go4lunch.models.User;
import com.dija.go4lunch.repositories.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
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

    public void updateLunchplaceInFirestore(String restaurantId) {
        if (mUserRepository.getIsLunchPlaceLiveData(restaurantId).getValue()) {
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    mUserRepository.deleteLunchplaceFromFirestore();
                }
            });
        } else {
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    mUserRepository.updateLunchplaceInFirestore(restaurantId);
                }
            });
        }
    }

    public MutableLiveData<List<User>> getUsersFromLunchPlace(String restaurantId){
     return mUserRepository.getUserIdFromLunchPlace(restaurantId);
    }


}
