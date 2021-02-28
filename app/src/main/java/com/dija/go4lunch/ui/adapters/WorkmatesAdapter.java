package com.dija.go4lunch.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.dija.go4lunch.R;
import com.dija.go4lunch.databinding.WorkmateItemBinding;
import com.dija.go4lunch.models.User;
import com.dija.go4lunch.viewmodel.UserViewModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import static com.dija.go4lunch.R.color.black;
import static com.dija.go4lunch.R.color.grey;

public class WorkmatesAdapter extends FirestoreRecyclerAdapter<User, WorkmatesAdapter.WorkmatesViewHolder> {
    public interface Listener {
        void onDataChanged();
    }

    private final RequestManager glide;
    private final String idCurrentUser;
    private WorkmatesAdapter.Listener callback;
    private UserViewModel mUserViewModel;


    public WorkmatesAdapter(@NonNull FirestoreRecyclerOptions<User> options, RequestManager glide,
                            String idCurrentUser, WorkmatesAdapter.Listener callback, UserViewModel mUserViewModel) {
        super(options);
        this.glide = glide;
        this.idCurrentUser = idCurrentUser;
        this.callback = callback;
        this.mUserViewModel = mUserViewModel;
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutinflater = LayoutInflater.from(parent.getContext());
        WorkmateItemBinding itemBinding = WorkmateItemBinding.inflate(layoutinflater, parent, false);
        return new WorkmatesAdapter.WorkmatesViewHolder(itemBinding);
    }

    @Override
    protected void onBindViewHolder(@NonNull WorkmatesViewHolder holder, int position, @NonNull User model) {
        holder.updateWithUser(model, this.glide);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        this.callback.onDataChanged();
    }


    public class WorkmatesViewHolder extends RecyclerView.ViewHolder {
        private final WorkmateItemBinding binding;

        public WorkmatesViewHolder(@NonNull WorkmateItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @SuppressLint("ResourceAsColor")
        private void updateWithUser(User user, RequestManager glide) {
            if (user.getUrlPicture() != null) {
                glide.load(user.getUrlPicture())
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.workmatePortrait);
            }

            String sentence = user.getUserName() + " " + itemView.getContext().getString(R.string.hasnt_decided_yet);
            binding.workmateSentence.setText(sentence);
            binding.workmateSentence.setTextColor(itemView.getContext().getResources().getColor(grey));
            binding.workmateSentence.setTextSize(18);

            // get Lunchplace name
            if (user.getUid() != null) {
                mUserViewModel.getLunchPlaceName(user.getUid()).observe((LifecycleOwner) itemView.getContext(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        if (s != null && s != "") {
                            String sentence = String.format(itemView.getContext().getString(R.string._is_eating_at_), user.getUserName() + " ", " " + s);
                            binding.workmateSentence.setText(sentence);
                            binding.workmateSentence.setTextColor(itemView.getContext().getResources().getColor(black));
                            binding.workmateSentence.setTextSize(20);
                        }
                    }
                });
            }

        }
    }
}

