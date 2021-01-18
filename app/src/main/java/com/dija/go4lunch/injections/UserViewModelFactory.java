package com.dija.go4lunch.injections;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.dija.go4lunch.repositories.UserRepository;
import com.dija.go4lunch.viewmodel.UserViewModel;

import java.util.concurrent.Executor;

public class UserViewModelFactory implements ViewModelProvider.Factory {
    private final UserRepository mUserRepository;
    private final Executor mExecutor;

    public UserViewModelFactory(UserRepository userRepository, Executor executor) {
        mUserRepository = userRepository;
        mExecutor = executor;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(mUserRepository, mExecutor);
        }
        throw new IllegalArgumentException("Unknown ViewModel Class");
    }
}
