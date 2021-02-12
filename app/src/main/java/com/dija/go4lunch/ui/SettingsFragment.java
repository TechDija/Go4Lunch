package com.dija.go4lunch.ui;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dija.go4lunch.R;
import com.dija.go4lunch.databinding.FragmentChatBinding;
import com.dija.go4lunch.databinding.FragmentSettingsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends BaseFragment<FragmentSettingsBinding> {

    String[] photoUrls = new String[]{"https://fr.web.img4.acsta.net/pictures/19/05/22/10/29/0914375.jpg",
            "https://upload.wikimedia.org/wikipedia/commons/a/ad/Angelina_Jolie_2_June_2014_%28cropped%29.jpg",
            "https://img.discogs.com/qXvuTkdyjRztoOxb3XNOPG9QjrI=/fit-in/300x300/filters:strip_icc():format(jpeg):mode_rgb():quality(40)/discogs-images/A-843707-1588467681-7412.png.jpg",
            "https://media.senscritique.com/media/000007398942/150_200/Godfrey_Quigley.jpg",
            "https://voi.img.pmdstatic.net/fit/http.3A.2F.2Fprd2-bone-image.2Es3-website-eu-west-1.2Eamazonaws.2Ecom.2Fvoi.2F2020.2F02.2F11.2F74f16c5c-7c10-4b2b-9188-c95ea7901465.2Ejpeg/2048x1152/quality/80/scarlett-johansson-le-prix-impressionnant-de-ses-boucles-d-oreilles-lors-des-oscars.jpeg",
            "https://www.avcesar.com/source/actualites/00/00/7C/7E/henry-cavill-tient-bond-la-route_09005715.jpg",
            "https://www.nautiljon.com/images/people/00/69/nana_after_school_19796.jpg"
    };

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.nameTextEditText.getText().toString();
                updateProfileOfWorkmates(name);
            }
        });
    }

    private void updateProfileOfWorkmates(String name ){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        switch (name){
            case ("Michelle"):
                setAvatarAndName("Michelle", "https://fr.web.img4.acsta.net/pictures/19/05/22/10/29/0914375.jpg");
                break;
            case("Angelina"):
                setAvatarAndName("Angelina", "https://upload.wikimedia.org/wikipedia/commons/a/ad/Angelina_Jolie_2_June_2014_%28cropped%29.jpg");
                break;
            case("Hugh"):
                setAvatarAndName("Hugh", "https://img.discogs.com/qXvuTkdyjRztoOxb3XNOPG9QjrI=/fit-in/300x300/filters:strip_icc():format(jpeg):mode_rgb():quality(40)/discogs-images/A-843707-1588467681-7412.png.jpg");
                break;
            case("Godfrey"):
                setAvatarAndName("Godfrey", "https://media.senscritique.com/media/000007398942/150_200/Godfrey_Quigley.jpg");
                break;
            case("Scarlett"):
                setAvatarAndName("Scarlett", "https://voi.img.pmdstatic.net/fit/http.3A.2F.2Fprd2-bone-image.2Es3-website-eu-west-1.2Eamazonaws.2Ecom.2Fvoi.2F2020.2F02.2F11.2F74f16c5c-7c10-4b2b-9188-c95ea7901465.2Ejpeg/2048x1152/quality/80/scarlett-johansson-le-prix-impressionnant-de-ses-boucles-d-oreilles-lors-des-oscars.jpeg");
                break;
            case("Henry"):
                setAvatarAndName("Henry", "https://www.avcesar.com/source/actualites/00/00/7C/7E/henry-cavill-tient-bond-la-route_09005715.jpg");
                break;
            case("Nana"):
                setAvatarAndName("Nana", "https://www.nautiljon.com/images/people/00/69/nana_after_school_19796.jpg");
                break;
            default:
        }
    }


    private void setAvatarAndName(String name, String photoUrl){
        if (name.length()>1) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(Uri.parse(photoUrl))
                    .build();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "User profile updated", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Set a valid name and retry", Toast.LENGTH_LONG).show();
        }

    }


}