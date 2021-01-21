package com.dija.go4lunch.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dija.go4lunch.R;
import com.dija.go4lunch.databinding.FragmentRestaurantDetailBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RestaurantDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestaurantDetailFragment extends BaseFragment<FragmentRestaurantDetailBinding> {
    String name;
    String photoUrl;

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
        name = getArguments().getString("name");
        photoUrl = getArguments().getString("photoUrl");

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.restaurantDetailTitle.setText(name);
        Glide.with(requireContext())
                .load(photoUrl)
                .into(binding.restaurantDetailPicture);
    }
}