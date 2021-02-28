package com.dija.go4lunch.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.dija.go4lunch.R;
import com.dija.go4lunch.databinding.WorkmateItemBinding;
import com.dija.go4lunch.models.User;

import java.util.ArrayList;
import java.util.List;

import static com.dija.go4lunch.R.color.black;


public class RestaurantDetailAdapter extends RecyclerView.Adapter<RestaurantDetailAdapter.RestaurantDetailViewHolder> {

    private final RequestManager glide;
    private List<User> users = null;

    public RestaurantDetailAdapter(RequestManager glide, List<User> users) {
        this.glide = glide;
        this.users = users;
    }

    @NonNull
    @Override
    public RestaurantDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutinflater = LayoutInflater.from(parent.getContext());
        WorkmateItemBinding itemBinding = WorkmateItemBinding.inflate(layoutinflater, parent, false);
        return new RestaurantDetailAdapter.RestaurantDetailViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantDetailViewHolder holder, int position) {
        holder.updateWithUser(users.get(position), glide);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class RestaurantDetailViewHolder extends RecyclerView.ViewHolder {
        private final WorkmateItemBinding binding;

        public RestaurantDetailViewHolder(@NonNull WorkmateItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @SuppressLint("ResourceAsColor")
        private void updateWithUser(User user, RequestManager glide) {
            if (user.getUrlPicture() != null) {
                glide.load(user.getUrlPicture())
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.workmatePortrait);
            } else {
                glide.load(R.drawable.fallout_avatar)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.workmatePortrait);
            }

            String sentence = user.getUserName() + " " + itemView.getContext().getString(R.string._is_joining);
            binding.workmateSentence.setText(sentence);
            binding.workmateSentence.setTextColor(itemView.getContext().getResources().getColor(black));
            binding.workmateSentence.setTextSize(18);

        }
    }
}

