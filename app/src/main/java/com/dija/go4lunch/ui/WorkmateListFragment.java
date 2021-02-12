package com.dija.go4lunch.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.dija.go4lunch.R;
import com.dija.go4lunch.api.FirestoreMessageHelper;
import com.dija.go4lunch.api.FirestoreUserHelper;
import com.dija.go4lunch.databinding.FragmentWorkmateListBinding;
import com.dija.go4lunch.models.Message;
import com.dija.go4lunch.models.User;
import com.dija.go4lunch.ui.adapters.ChatAdapter;
import com.dija.go4lunch.ui.adapters.WorkmatesAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkmateListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkmateListFragment extends BaseFragment<FragmentWorkmateListBinding> implements WorkmatesAdapter.Listener {

    private WorkmatesAdapter mAdapter;


    public WorkmateListFragment() {
    }

    public static WorkmateListFragment newInstance() {
        WorkmateListFragment fragment = new WorkmateListFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configureRecyclerView();
    }

   private void  configureRecyclerView(){
       mAdapter = new WorkmatesAdapter (
               generateOptionsForAdapter(FirestoreUserHelper.getAllUsersquery()),
               Glide.with(this),
               FirebaseAuth.getInstance().getCurrentUser().getUid(),
               this);
       binding.workmateRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
       binding.workmateRecyclerview.setAdapter(mAdapter);
    }

    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }

    @Override
    public void onDataChanged() {

    }
}