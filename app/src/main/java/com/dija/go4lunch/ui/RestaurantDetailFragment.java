package com.dija.go4lunch.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.dija.go4lunch.R;
import com.dija.go4lunch.databinding.FragmentRestaurantDetailBinding;
import com.dija.go4lunch.injections.Injection;
import com.dija.go4lunch.injections.MapViewModelFactory;
import com.dija.go4lunch.injections.UserViewModelFactory;
import com.dija.go4lunch.models.User;
import com.dija.go4lunch.models.nearbyAPImodels.Result;
import com.dija.go4lunch.ui.adapters.RestaurantDetailAdapter;
import com.dija.go4lunch.viewmodel.MapViewModel;
import com.dija.go4lunch.viewmodel.UserViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RestaurantDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestaurantDetailFragment extends BaseFragment<FragmentRestaurantDetailBinding> {
    Result result;
    private UserViewModel mUserViewModel;
    private MapViewModel mMapViewModel;
    private RestaurantDetailAdapter mAdapter;

    public RestaurantDetailFragment() {
        // Required empty public constructor
    }

    public static RestaurantDetailFragment newInstance() {
        RestaurantDetailFragment fragment = new RestaurantDetailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        result = (Result) bundle.getSerializable("result");
        configureUserViewModel();
        configureMapViewModel();
        configureToolbar();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.restaurantDetailTitle.setText(result.getName());
        binding.restaurantDetailAdress.setText(result.getVicinity());
        setPhoto();
        setCheckButton();
        configureRecyclerView();
        setRateStars();
        binding.restaurantCheckPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserViewModel.updateLunchplaceInFirestore(result.getPlaceId(), result.getName(), result.getVicinity());
                mUserViewModel.saveLunchplaceLocally(getContext(), result);
                updateCheckButton();
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        binding.restaurantDetailCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPhoneNumber();
            }
        });

        binding.restaurantDetailWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWebsite();
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    private void configureUserViewModel() {
        UserViewModelFactory mUserViewModelFactory = Injection.provideUserViewModelFactory(getContext());
        this.mUserViewModel = new ViewModelProvider(this, mUserViewModelFactory).get(UserViewModel.class);
    }

    private void configureMapViewModel() {
        MapViewModelFactory mMapViewModelFactory = Injection.provideMapViewModelFactory(getContext());
        this.mMapViewModel = new ViewModelProvider(this, mMapViewModelFactory).get(MapViewModel.class);
    }

    private void setCheckButton() {
        mUserViewModel.isLunchPlaceLiveData(result.getPlaceId()).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    binding.restaurantCheckPicture.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.pastel_green)));
                } else {
                    binding.restaurantCheckPicture.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.orange)));
                }
            }
        });
    }

    private void updateCheckButton() {
        mUserViewModel.isLunchPlaceLiveData(result.getPlaceId()).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    binding.restaurantCheckPicture.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.orange)));
                } else {
                    binding.restaurantCheckPicture.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.pastel_green)));
                }
            }
        });
    }

    private void setPhoto() {
        if (!result.getPhotos().isEmpty()) {
            String baseUrlPhoto = "https://maps.googleapis.com/maps/api/place/photo";
            String photoReference = result.getPhotos().get(0).getPhotoReference();
            final int MAX_WIDTH = 800;
            String key = getContext().getString(R.string.google_api_key);
            String urlPhoto = baseUrlPhoto + "?maxwidth=" + MAX_WIDTH + "&photoreference=" + photoReference + "&key=" + key;
            Glide.with(requireContext())
                    .load(urlPhoto)
                    .into(binding.restaurantDetailPicture);
        }
    }

    private void getPhoneNumber() {
        String key = getContext().getString(R.string.google_api_key);
        mMapViewModel.getPhoneNumber(result.getPlaceId(), key).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + s));
                startActivity(intent);
            }
        });
    }

    private void getWebsite() {
        String key = getContext().getString(R.string.google_api_key);
        mMapViewModel.getWebsite(result.getPlaceId(), key).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                startActivity(browserIntent);
            }
        });
    }

    private void configureRecyclerView() {

        mUserViewModel.getUsersFromLunchPlace(result.getPlaceId()).observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                mAdapter = new RestaurantDetailAdapter(Glide.with(RestaurantDetailFragment.this), users);
                binding.restaurantDetailRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.restaurantDetailRecyclerview.setAdapter(mAdapter);
            }
        });
    }

    private void setRateStars() {
        if (result.getRating() != null && result.getRating() < 3.0f) {
            binding.starScore2.setVisibility(View.INVISIBLE);
            binding.starScore3.setVisibility(View.INVISIBLE);
        } else if (result.getRating() != null && result.getRating() < 4.2f && result.getRating() >= 3.0f) {
            binding.starScore3.setVisibility(View.INVISIBLE);
        } else if (result.getRating() == null) {
            binding.starScore1.setVisibility(View.INVISIBLE);
            binding.starScore2.setVisibility(View.INVISIBLE);
            binding.starScore3.setVisibility(View.INVISIBLE);
        }
    }

    private void configureToolbar() {
        setHasOptionsMenu(true);
    }

}

