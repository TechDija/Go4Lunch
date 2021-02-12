package com.dija.go4lunch.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.dija.go4lunch.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import static android.content.ContentValues.TAG;

public class FirestoreUserHelper {

    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String username, String urlPicture) {
        // create User object
        User userToCreate = new User(uid, username, urlPicture);
        // add a new User Document to Firestore
        Task<Void> createUser = FirestoreUserHelper.getUsersCollection().document(uid).set(userToCreate);
        return createUser.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "ERROR ADDING DOCUMENT", e);
            }
        });

    }

    // --- GET ---

    public static Task<QuerySnapshot> getAllUsers(){
        return FirestoreUserHelper
                .getUsersCollection()
                .get();
    }

    public static Query getAllUsersquery(){
        return FirestoreUserHelper
                .getUsersCollection();
               // .orderBy("lunch", Query.Direction.DESCENDING);
    }
    public static Task<DocumentSnapshot> getUser(String uid) {
        return FirestoreUserHelper.getUsersCollection().document(uid).get();
    }

    // --- UPDATE ---

    public static Task<Void> updateUsername(String username, String uid) {
        return FirestoreUserHelper.getUsersCollection().document(uid).update("username", username);
    }

    public static Task<Void> updateLunch(String uid, String lunch) {
        return FirestoreUserHelper.getUsersCollection().document(uid).update("lunch", lunch);
    }

    public static Task<Void> updatePhotoUrl(String uid, String photoUrl) {
        return FirestoreUserHelper.getUsersCollection().document(uid).update("urlPicture", photoUrl);
    }
    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return FirestoreUserHelper.getUsersCollection().document(uid).delete();
    }

}

