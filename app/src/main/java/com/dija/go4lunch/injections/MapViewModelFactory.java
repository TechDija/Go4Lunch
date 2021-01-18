package com.dija.go4lunch.injections;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.dija.go4lunch.repositories.MapRepository;
import com.dija.go4lunch.repositories.UserRepository;
import com.dija.go4lunch.viewmodel.MapViewModel;
import com.dija.go4lunch.viewmodel.UserViewModel;

import java.util.concurrent.Executor;

public class MapViewModelFactory implements ViewModelProvider.Factory {
    private final MapRepository mMapRepository;
    private final Executor mExecutor;

    public MapViewModelFactory(MapRepository mapRepository, Executor executor) {
        mMapRepository = mapRepository;
        mExecutor = executor;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MapViewModel.class)) {
            return (T) new MapViewModel(mMapRepository, mExecutor);
        }
        throw new IllegalArgumentException("Unknown ViewModel Class");
    }
}