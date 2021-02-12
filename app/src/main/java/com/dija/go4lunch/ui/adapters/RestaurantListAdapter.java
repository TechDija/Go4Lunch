package com.dija.go4lunch.ui.adapters;

import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dija.go4lunch.R;
import com.dija.go4lunch.databinding.RestaurantItemBinding;
import com.dija.go4lunch.models.nearbyAPImodels.Result;

import java.util.List;

public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.RestaurantViewHolder> {

    private List<Result> mResults;
    private Location lastKnownLocation;

    public RestaurantListAdapter(List<Result> mResults, Location lastKnownLocation) {
        this.mResults = mResults;
        this.lastKnownLocation = lastKnownLocation;
    }


    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutinflater = LayoutInflater.from(parent.getContext());
        RestaurantItemBinding itemBinding = RestaurantItemBinding.inflate(layoutinflater, parent, false);
        return new RestaurantListAdapter.RestaurantViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        holder.bind(mResults.get(position));
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }


    public class RestaurantViewHolder extends RecyclerView.ViewHolder {

        private RestaurantItemBinding itemBinding;

        public RestaurantViewHolder(@NonNull RestaurantItemBinding binding) {
            super(binding.getRoot());
            this.itemBinding = binding;
        }

        public void bind(Result result) {
            itemBinding.restaurantName.setText(result.getName());
            itemBinding.restaurantAdress.setText(result.getVicinity());
            if (result.getOpeningHours() != null) {
                if (result.getOpeningHours().getOpenNow()) {
                    itemBinding.restaurantOpenUntil.setText("currently open");
                } else {
                    itemBinding.restaurantOpenUntil.setText("currently closed");
                }
            } else {
                itemBinding.restaurantOpenUntil.setText("currently closed");
            }
            if (result.getRating() == null || result.getRating() < 2.0) {
                itemBinding.restaurantStars.setVisibility(View.GONE);
            } else {
                itemBinding.restaurantStars.setVisibility(View.VISIBLE);
            }
            //restaurant picture
            if (result.getPhotos().size() == 1) {
                String baseUrlPhoto = "https://maps.googleapis.com/maps/api/place/photo";
                String photoReference = result.getPhotos().get(0).getPhotoReference();
                final int MAX_WIDTH = 200;
                String key = itemView.getContext().getString(R.string.google_api_key);
                String urlPhoto = baseUrlPhoto + "?maxwidth=" + MAX_WIDTH + "&photoreference=" + photoReference + "&key=" + key;
                Glide.with(itemView.getContext())
                        .load(urlPhoto)
                        .into(itemBinding.restaurantPicture);
            }
            //distance
            if (lastKnownLocation != null) {
                Location userLocation = new Location("userLocation");
                userLocation.setLatitude(lastKnownLocation.getLatitude());
                userLocation.setLongitude(lastKnownLocation.getLongitude());

                Location resultLocation = new Location("resultLocation");
                resultLocation.setLatitude(result.getGeometry().getLocation().getLat());
                resultLocation.setLongitude(result.getGeometry().getLocation().getLng());

                int distance = (int) userLocation.distanceTo(resultLocation);

                itemBinding.restaurantDistance.setText(distance + "m");
            }
        }
        //TODO: the calculation of ratings & the number of colleagues who go there eating

    }
}

