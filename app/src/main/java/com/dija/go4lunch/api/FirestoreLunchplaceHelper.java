package com.dija.go4lunch.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.dija.go4lunch.models.LunchPlace;
import com.dija.go4lunch.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

public class FirestoreLunchplaceHelper {

    private static final String COLLECTION_NAME = "lunchPlaces";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getLunchPlacesCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createLunchPlace(String resultId, String userId, String resultName, String resultAdress) {
        //LunchPlace lunchPlaceToCreate = new LunchPlace(resultId, userId, resultName, resultAdress);
        HashMap<String, Object> map = new HashMap<>();
        if (resultId != null && userId !=null) {
            map.put("resultId", resultId);
            map.put("userId", userId);
            map.put("lunchplaceName", resultName);
            map.put("lunchplaceAdress", resultAdress);
        }
        Task<Void> createLunchPlace = FirestoreLunchplaceHelper.getLunchPlacesCollection().document(userId).set(map);
        return createLunchPlace.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "ERROR ADDING DOCUMENT", e);
            }
        });

    }

    // --- GET ---

    public static Task<QuerySnapshot> getAllLunchPlaces(){
        return FirestoreLunchplaceHelper
                .getLunchPlacesCollection()
                .get();
    }

    public static Task<DocumentSnapshot> getLunchPlace(String userId) {
        if (FirestoreLunchplaceHelper.getLunchPlacesCollection().document(userId) != null) {
            return FirestoreLunchplaceHelper.getLunchPlacesCollection().document(userId).get();
        } else {
            return null;
        }
    }

    public static DocumentReference getLunchPlaceDocRef(String resultId) {
        if (FirestoreLunchplaceHelper.getLunchPlacesCollection().document(resultId) != null) {
            return FirestoreLunchplaceHelper.getLunchPlacesCollection().document(resultId);
        } else {
            return null;
        }
    }

    // --- UPDATE ---

    // --- DELETE ---


    public static Task<Void> deleteLunchPlace(String currentUser) {
        return FirestoreLunchplaceHelper.getLunchPlacesCollection().document(currentUser).delete();
    }


}
