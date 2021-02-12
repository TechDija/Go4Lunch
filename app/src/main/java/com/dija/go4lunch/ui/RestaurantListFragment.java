package com.dija.go4lunch.ui;

import android.location.Location;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dija.go4lunch.utils.ItemClickSupport;
import com.dija.go4lunch.R;
import com.dija.go4lunch.databinding.FragmentRestaurantListBinding;
import com.dija.go4lunch.injections.Injection;
import com.dija.go4lunch.injections.MapViewModelFactory;
import com.dija.go4lunch.models.nearbyAPImodels.Result;
import com.dija.go4lunch.ui.adapters.RestaurantListAdapter;
import com.dija.go4lunch.viewmodel.MapViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RestaurantListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestaurantListFragment extends BaseFragment<FragmentRestaurantListBinding> {

    private RestaurantListAdapter mAdapter;
    private MapViewModel mMapViewModel;
    private List<Result> results = new ArrayList<>();
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location lastKnownLocation;

    public RestaurantListFragment() {
    }

    public static RestaurantListFragment newInstance() {
        RestaurantListFragment fragment = new RestaurantListFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        configureMapViewModel();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        initializeLastKnownLocation();
        //configureRecyclerView();
        configureOnClickRecyclerView();
        super.onViewCreated(view, savedInstanceState);
        binding.progressBar.setVisibility(View.GONE);
    }

    private void configureMapViewModel() {
        MapViewModelFactory mMapViewModelFactory = Injection.provideMapViewModelFactory(getContext());
        this.mMapViewModel = new ViewModelProvider(this, mMapViewModelFactory).get(MapViewModel.class);
    }

    private void configureRecyclerView() {
        mMapViewModel.getNearBySearchResultsFromLocation(mFusedLocationProviderClient, getContext()
                .getString(R.string.google_api_key)).observe(getViewLifecycleOwner(), new Observer<List<Result>>() {
            @Override
            public void onChanged(List<Result> results) {
                mAdapter = new RestaurantListAdapter(results, lastKnownLocation);
                binding.restaurantList.setAdapter(mAdapter);
                binding.restaurantList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            }
        });
    }

    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(binding.restaurantList)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        mMapViewModel.getNearBySearchResultsFromLocation(mFusedLocationProviderClient, getContext()
                                .getString(R.string.google_api_key)).observe(getViewLifecycleOwner(), new Observer<List<Result>>() {
                            @Override
                            public void onChanged(List<Result> results) {
                                Result result = results.get(position);
                                RestaurantDetailFragment restaurantDetailFragment = new RestaurantDetailFragment();
                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, restaurantDetailFragment)
                                        .addToBackStack(RestaurantListFragment.class.getSimpleName())
                                        .commit();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("result", result);
                                restaurantDetailFragment.setArguments(bundle);

                                  }
                        });


                    }
                });

    }

    private void initializeLastKnownLocation(){
    mMapViewModel.lastKnownLocation(mFusedLocationProviderClient).observe(getViewLifecycleOwner(), new Observer<Location>() {
        @Override
        public void onChanged(Location location) {
            lastKnownLocation = location;
            configureRecyclerView();
        }
    });
    }


}