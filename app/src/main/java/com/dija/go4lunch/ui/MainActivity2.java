package com.dija.go4lunch.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dija.go4lunch.R;
import com.dija.go4lunch.SuggestionCursorAdapter;
import com.dija.go4lunch.databinding.ActivityMain2Binding;
import com.dija.go4lunch.databinding.HeaderBinding;
import com.dija.go4lunch.injections.Injection;
import com.dija.go4lunch.injections.MapViewModelFactory;
import com.dija.go4lunch.injections.UserViewModelFactory;
import com.dija.go4lunch.viewmodel.MapViewModel;
import com.dija.go4lunch.viewmodel.UserViewModel;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {
    private static final int AUTOCOMPLETE_REQUEST_CODE = 99;
    ActivityMain2Binding binding;
    HeaderBinding headerBinding;
    private UserViewModel mUserViewModel;
    private MapViewModel mMapViewModel;
    private static final int SIGN_OUT_TASK = 10;
    String searchViewString;
    private List<AutocompletePrediction> mPredictionList;
    AutocompleteSessionToken token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //binding headerView
        View headerView = binding.navigationView.getHeaderView(0);
        headerBinding = HeaderBinding.bind(headerView);

        if (!Places.isInitialized()) {
        Places.initialize(this, "AIzaSyAOzdywOxstlvYwyVPLwXlGlA42yb6p6uc");}
        configureToobar();
        configureNavigation();
        configureUserViewModel();
        configureMapViewModel();

        this.token = AutocompleteSessionToken.newInstance();
        //FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder();
    }
   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       MenuInflater menuInflater = getMenuInflater();
       menuInflater.inflate(R.menu.search_menu, menu);
       return true;
   }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                onSearchCalled();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }



        /** Inside OnCreateOptionMenu
         * Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //smth
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mMapViewModel.getPredictionsRequestLiveData(newText).observe(MainActivity2.this, new Observer<FindAutocompletePredictionsRequest>() {
                    @Override
                    public void onChanged(FindAutocompletePredictionsRequest findAutocompletePredictionsRequest) {
                        mMapViewModel.getPlacesClient().findAutocompletePredictions(findAutocompletePredictionsRequest)
                                .addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                                    @Override
                                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                                        if (task.isSuccessful()) {
                                            mMapViewModel.AutoCompletePredictionsResponse(task);
                                        }
                                        SuggestionCursorAdapter adapter = new SuggestionCursorAdapter(MainActivity2.this, mMapViewModel.getCursor(), false);
                                        searchView.setSuggestionsAdapter(adapter);
                                        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                                            @Override
                                            public boolean onSuggestionSelect(int position) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onSuggestionClick(int position) {
                                                if (position >= mPredictionList.size()) {
                                                    return false;
                                                }
                                                AutocompletePrediction selectedPrediction = mPredictionList.get(position);
                                                String suggestion = searchView.getSuggestionsAdapter().getItem(position).toString();
                                                searchView.setQuery(suggestion, false);
                                                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                                if (imm != null) {
                                                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                                                }
                                                String placeId = selectedPrediction.getPlaceId();
                                                List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS);
                                                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();
                                                mMapViewModel.getPlacesClient().fetchPlace(fetchPlaceRequest)
                                                        .addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                                                    @Override
                                                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                                                        Place place = fetchPlaceResponse.getPlace();
                                                        Log.i("TAG", "Place found" + place.getName());
                                                        LatLng latLngOfPlace = place.getLatLng();
                                                        if (latLngOfPlace != null) {
                                                            mMapViewModel.moveCameraTo(latLngOfPlace);
                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        if (e instanceof ApiException) {
                                                            ApiException apiException = (ApiException) e;
                                                            apiException.printStackTrace();
                                                            int statusCode = apiException.getStatusCode();
                                                            Log.i("TAG", "place not found" + e.getMessage());
                                                        }
                                                    }
                                                });
                                                return true;
                                            }
                                        });
                                    }

                                });
                    }

                });
                return true;
            }
        });
         */


        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    Log.i("TAG", "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
                    Toast.makeText(this, "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName(), Toast.LENGTH_LONG).show();
                    String address = place.getAddress();
                    mMapViewModel.moveCameraTo(place.getLatLng());

                } else{
                    // Handle the error.
                    Status status = Autocomplete.getStatusFromIntent(data);
                    Toast.makeText(this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                    Log.i("TAG", "an error occured");

                }
            }
        }
    @Override
    protected void onStart() {
        super.onStart();
        //updating UI in header view according to user
        updatePicture(headerBinding.headerUserPicture);
        updateName(headerBinding.headerName);
        updateEmail(headerBinding.headerEmail);

        // signing out
        binding.navigationView.getMenu().findItem(R.id.menu_logout).setOnMenuItemClickListener(MenuItem -> {
            logout();
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        if (binding.navigationLayout.isDrawerOpen(GravityCompat.START)) {
            binding.navigationLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
//region ACTIONS
//---------------------
//ACTIONS
//---------------------

    // Retrieving Data from ViewModels through Injection
    private void configureUserViewModel() {
        UserViewModelFactory mUserViewModelFactory = Injection.provideUserViewModelFactory(this);
        this.mUserViewModel = new ViewModelProvider(this, mUserViewModelFactory).get(UserViewModel.class);
    }

    private void configureMapViewModel() {
        MapViewModelFactory mMapViewModelFactory = Injection.provideMapViewModelFactory(this);
        this.mMapViewModel = new ViewModelProvider(this, mMapViewModelFactory).get(MapViewModel.class);
    }

    private void onSearchCalled(){
        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .setCountry("FR")
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    private void updatePicture(ImageView view) {
        mUserViewModel.getCurrentUserPicture().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null) {
                    Glide.with(getApplicationContext())
                            .load(s)
                            .apply(RequestOptions.circleCropTransform())
                            .into(view);
                }
            }
        });
    }

    private void configureToobar() {
        // configuring toolbar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void configureNavigation() {
        // configuring bottom navigation
        AppBarConfiguration mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_mapview, R.id.navigation_listview, R.id.navigation_workmates)
                .setDrawerLayout(binding.navigationLayout)
                .build();
        // configuring Navigation
        NavController navController = Navigation.findNavController(this, R.id.fragment_container);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.navigationView, navController);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
        //configuring drawer nav
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.navigationLayout,
                binding.toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        binding.navigationLayout.addDrawerListener(toggle);
        toggle.syncState();
        //Handle visibility of the application bottom navigation
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.navigation_lunch || destination.getId() == R.id.navigation_settings) {
                    binding.bottomNavigation.setVisibility(View.GONE);
                } else {
                    binding.bottomNavigation.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void updateName(TextView view) {
        mUserViewModel.getCurrentUserName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null) {
                    view.setText(s);
                    view.setMaxLines(1);
                    view.setEllipsize(TextUtils.TruncateAt.END);
                }
            }
        });
    }

    private void updateEmail(TextView view) {
        mUserViewModel.getCurrentUserEmail().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null) {
                    view.setText(s);
                }
            }
        });
    }

    private void logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIOnRequestCompleted(SIGN_OUT_TASK));
    }

    private OnSuccessListener<Void> updateUIOnRequestCompleted(final int origin) {
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (origin == SIGN_OUT_TASK) {
                    Intent mainActivity = new Intent(MainActivity2.this, MainActivity.class);
                    startActivity(mainActivity);
                }

//endregion

            }
        };
    }
}
