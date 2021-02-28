package com.dija.go4lunch.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import com.dija.go4lunch.databinding.ActivityMain2Binding;
import com.dija.go4lunch.databinding.HeaderBinding;
import com.dija.go4lunch.injections.Injection;
import com.dija.go4lunch.injections.MapViewModelFactory;
import com.dija.go4lunch.injections.UserViewModelFactory;
import com.dija.go4lunch.models.nearbyAPImodels.Result;
import com.dija.go4lunch.viewmodel.MapViewModel;
import com.dija.go4lunch.viewmodel.UserViewModel;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity2 extends AppCompatActivity {
    private static final int AUTOCOMPLETE_REQUEST_CODE = 99;
    ActivityMain2Binding binding;
    HeaderBinding headerBinding;
    private UserViewModel mUserViewModel;
    private MapViewModel mMapViewModel;
    private static final int SIGN_OUT_TASK = 10;
    FusedLocationProviderClient mFusedLocationProviderClient;
    String searchViewString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(true);
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //binding headerView
        View headerView = binding.navigationView.getHeaderView(0);
        headerBinding = HeaderBinding.bind(headerView);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        configureToolbar();
        configureNavigation();
        configureUserViewModel();
        configureMapViewModel();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setSuggestionsAdapter(new SimpleCursorAdapter(
                this, android.R.layout.simple_list_item_1, null,
                new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1},
                new int[]{android.R.id.text1}
        ));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //get Restaurant Detail fragment
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= 3)
                    mMapViewModel.getAutocompleteCursor(mFusedLocationProviderClient, getString(R.string.google_api_key), newText)
                            .observe(MainActivity2.this, new Observer<Cursor>() {
                                @Override
                                public void onChanged(Cursor cursor) {
                                    searchView.getSuggestionsAdapter().changeCursor(cursor);
                                    searchView.setOnSearchClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    });
                                }
                            });
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //updating UI in header view according to user
        updatePicture(headerBinding.headerUserPicture);
        updateName(headerBinding.headerName);
        updateEmail(headerBinding.headerEmail);

        //navigating to the lunch fragment chosen by the user
        binding.navigationView.getMenu().findItem(R.id.navigation_lunch).setOnMenuItemClickListener(MenuItem -> {
           navigateToLunch();
           return true;
        });

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
            getSupportFragmentManager().popBackStack();
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

    private void configureToolbar() {
        // configuring toolbar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.I_am_hungry);

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
      //  setProgressBarIndeterminateVisibility(false);
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
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Log out");
        alertDialog.setMessage("Do you want to logout ?");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(alertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AuthUI.getInstance()
                                .signOut(MainActivity2.this)
                                .addOnSuccessListener(MainActivity2.this, MainActivity2.this.updateUIOnRequestCompleted(SIGN_OUT_TASK));
                    }
                });
        alertDialog.show();

    }

    private OnSuccessListener<Void> updateUIOnRequestCompleted(final int origin) {
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (origin == SIGN_OUT_TASK) {
                    Intent mainActivity = new Intent(MainActivity2.this, MainActivity.class);
                    startActivity(mainActivity);
                }



            }
        };
    }

    private void navigateToLunch(){
        Result result = mUserViewModel.retrieveUsersLunchplace(this);
        if (result instanceof Result) {
            binding.navigationLayout.closeDrawer(GravityCompat.START);
            getSupportFragmentManager().popBackStack();
            RestaurantDetailFragment restaurantDetailFragment = new RestaurantDetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, restaurantDetailFragment)
                    .commitAllowingStateLoss();
            Bundle bundle = new Bundle();
            bundle.putSerializable("result", result);
            restaurantDetailFragment.setArguments(bundle);

        } else {
            Toast.makeText(this, "You haven't registered your lunch yet", Toast.LENGTH_LONG).show();
        }
    }


    //endregion
}
