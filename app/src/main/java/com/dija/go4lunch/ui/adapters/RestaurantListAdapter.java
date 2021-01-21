package com.dija.go4lunch.ui.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dija.go4lunch.databinding.RestaurantItemBinding;
import com.dija.go4lunch.models.nearbyAPImodels.Result;

import java.io.InputStream;
import java.util.List;

public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.RestaurantViewHolder> {

    private List<Result> mResults;

    public RestaurantListAdapter(List<Result> mResults) {
        this.mResults = mResults;
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
        RestaurantViewHolder.bind(mResults.get(position));
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }


    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {

        private static RestaurantItemBinding itemBinding;

        public RestaurantViewHolder(@NonNull RestaurantItemBinding binding) {
            super(binding.getRoot());
            this.itemBinding = binding;
        }

        public static void bind(Result result) {
            itemBinding.restaurantName.setText(result.getName());
            itemBinding.restaurantAdress.setText(result.getVicinity());
            if (result.getOpeningHours() != null) {
                if (result.getOpeningHours().getOpenNow()){
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
            if (result.getPhotos().size() != 0) {
                Bitmap bitmap = new ImageAsyncTask().doInBackground(result.getPhotos().get(0).getPhotoReference());
                itemBinding.restaurantPicture.setImageBitmap(bitmap);
            }
            //TODO: distance & the calculation of ratings & the number of colleagues who go there eating

        }

        public static class ImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

            @Override
            protected Bitmap doInBackground(String... params) {
                try {
                    InputStream inputStream = new java.net.URL(params[0]).openStream();
                    return BitmapFactory.decodeStream(inputStream);
                } catch (Exception e) {
                    return null;
                }
            }

        }
    }
}
