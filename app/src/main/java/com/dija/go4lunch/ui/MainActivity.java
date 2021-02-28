package com.dija.go4lunch.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.dija.go4lunch.R;
import com.dija.go4lunch.databinding.ActivityMainBinding;
import com.dija.go4lunch.injections.Injection;
import com.dija.go4lunch.injections.UserViewModelFactory;
import com.dija.go4lunch.notifications.NotificationReceiver;
import com.dija.go4lunch.viewmodel.UserViewModel;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.FirebaseApp;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    ActivityMainBinding binding;
    final int RC_SIGN_IN = 123;
    UserViewModel mUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configureUserViewModel();
        myAlarm();
        binding.login.setOnClickListener(v ->
                startSignInActivity());
        }

//TODO in MainActivity2
    private void myAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTime().compareTo(new Date())<0)
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handleResponseAfterSignIn(requestCode, resultCode, data);
    }
// region ACTIONS
    //---------------------
    //ACTIONS
    //---------------------
private void configureUserViewModel() {
    UserViewModelFactory mUserViewModelFactory = Injection.provideUserViewModelFactory(this);
    this.mUserViewModel = new ViewModelProvider(this, mUserViewModelFactory).get(UserViewModel.class);
}

    // Launch Sign-In Activity
    private void startSignInActivity() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(
                                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                                        new AuthUI.IdpConfig.EmailBuilder().build(),
                                        new AuthUI.IdpConfig.TwitterBuilder().build(),
                                        new AuthUI.IdpConfig.FacebookBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN);
    }

    private void startMainActivity2() {
        Intent MainActivity2 = new Intent(this, com.dija.go4lunch.ui.MainActivity2.class);
        startActivity(MainActivity2);
    }

    private void showToast (int message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
//endregion
//region UTILS
    //------------------------
    //UTILS
    //-----------------------

    // Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                mUserViewModel.createUserInFirestore();
                startMainActivity2();
            } else { // ERRORS
                showToast(R.string.authentication_aborted);
            }
        }
    }
//endregion
}