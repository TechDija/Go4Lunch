package com.dija.go4lunch.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.dija.go4lunch.R;
import com.dija.go4lunch.api.FirestoreSettingsHelper;
import com.dija.go4lunch.databinding.FragmentRadiusDialogBinding;
import com.google.firebase.auth.FirebaseAuth;


public class RadiusDialogFragment extends DialogFragment {

  private FragmentRadiusDialogBinding binding;
  private int radius;

    public RadiusDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
     binding = FragmentRadiusDialogBinding.inflate(inflater, container, false);
    return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.seekbarRadius.setOnSeekBarChangeListener(radius_seekbar);
        binding.seekbarRadius.setProgress(5);
        binding.enregistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radius != 0) {
                    FirestoreSettingsHelper.updateRadius(FirebaseAuth.getInstance().getCurrentUser().getUid(), radius);
                    Toast.makeText(getContext(),"Your new radius of "+ radius + " km will be applied", Toast.LENGTH_LONG);
                } else {
                    FirestoreSettingsHelper.updateRadius(FirebaseAuth.getInstance().getCurrentUser().getUid(), 5);
                    Toast.makeText(getContext(),"A default radius of 5km will be applied", Toast.LENGTH_LONG);
                }
                getDialog().dismiss();
            }
        });
    }

    SeekBar.OnSeekBarChangeListener radius_seekbar = new SeekBar.OnSeekBarChangeListener(){

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            radius = progress;
            binding.valeur1.setText( progress + " km");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}