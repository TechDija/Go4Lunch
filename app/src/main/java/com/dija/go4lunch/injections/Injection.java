package com.dija.go4lunch.injections;

import android.content.Context;

import com.dija.go4lunch.repositories.MapRepository;
import com.dija.go4lunch.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Injection {
    public static UserRepository provideUserDataSource(Context context) {
        FirebaseAuth mFirebase = FirebaseAuth.getInstance();
        return new UserRepository(mFirebase);
    }

    public static MapRepository provideMapDataSource(Context context) {
        return new MapRepository();
    }

    public static Executor provideExecutor(){
        return Executors.newSingleThreadExecutor();
    }

    public static UserViewModelFactory provideUserViewModelFactory(Context context) {
        UserRepository dataSourceUser = provideUserDataSource(context);
        Executor executor = provideExecutor();
        return new UserViewModelFactory(dataSourceUser, executor);
    }

    public static MapViewModelFactory provideMapViewModelFactory(Context context) {
        MapRepository dataSourceMap = provideMapDataSource(context);
        Executor executor = provideExecutor();
        return new MapViewModelFactory(dataSourceMap, executor);
    }
}
