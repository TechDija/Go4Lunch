package com.dija.go4lunch.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dija.go4lunch.R;
import com.dija.go4lunch.databinding.FragmentChatBinding;
import com.dija.go4lunch.databinding.FragmentSettingsBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends BaseFragment<FragmentSettingsBinding> {

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
    /**
     * FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
     *
     * UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
     *         .setDisplayName("Jane Q. User")
     *         .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
     *         .build();
     *
     * user.updateProfile(profileUpdates)
     *         .addOnCompleteListener(new OnCompleteListener<Void>() {
     *             @Override
     *             public void onComplete(@NonNull Task<Void> task) {
     *                 if (task.isSuccessful()) {
     *                     Log.d(TAG, "User profile updated.");
     *                 }
     *             }
     *         });
    */
}