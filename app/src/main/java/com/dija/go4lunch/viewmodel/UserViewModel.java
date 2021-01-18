package com.dija.go4lunch.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.dija.go4lunch.repositories.UserRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

public class UserViewModel extends ViewModel {
    private final UserRepository mUserRepository;
    private final Executor mExecutor;


    // region CONSTRUCTOR
    public UserViewModel(UserRepository userRepository, Executor executor) {
        mUserRepository = userRepository;
        mExecutor = executor;
    }
    // endregion

    public LiveData<FirebaseUser> getCurrentUser(){
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
}
