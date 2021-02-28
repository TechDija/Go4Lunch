package com.dija.go4lunch.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.dija.go4lunch.models.LunchPlace;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class FirestoreSettingsHelper {
    private static final String COLLECTION_NAME = "settings";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getSettingsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createSetting(String userId, int radius) {
        HashMap<String, Object> map = new HashMap<>();
        if (userId !=null) {
            map.put("userId", userId);
            map.put("radius", radius);
        }
        Task<Void> createSettings = FirestoreSettingsHelper.getSettingsCollection().document(userId).set(map);
        return createSettings.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "ERROR ADDING DOCUMENT", e);
            }
        });

    }

    // --- GET ---

    public static Task<DocumentSnapshot> getRadius(String userId) {
        if (FirestoreSettingsHelper.getSettingsCollection().document(userId) != null) {
            return FirestoreSettingsHelper.getSettingsCollection().document(userId).get();
        } else {
            return null;
        }
    }

    // --- UPDATE ---

    public static Task<Void> updateRadius(String uid, int radius) {
        return FirestoreSettingsHelper.getSettingsCollection().document(uid).update("radius", radius);
    }

    // --- DELETE ---


    public static Task<Void> deleteSetting(String currentUser) {
        return FirestoreSettingsHelper.getSettingsCollection().document(currentUser).delete();
    }
}
