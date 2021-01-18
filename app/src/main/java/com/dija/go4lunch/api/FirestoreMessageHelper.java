package com.dija.go4lunch.api;

import com.dija.go4lunch.models.Message;
import com.dija.go4lunch.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FirestoreMessageHelper {

    private static final String COLLECTION_NAME = "chat";
    private static final String SECOND_COLLECTION_NAME = "messages";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getChatCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    //--- CREATE ---
    public static Task<DocumentReference> createMessageForChat(String textMessage, String chat, User userSender){
        Message message = new Message(textMessage, userSender);
        return FirestoreMessageHelper.getChatCollection()
                .document(chat)
                .collection(SECOND_COLLECTION_NAME)
                .add(message);
    }

    public static Task<DocumentReference> createMessageWithImageForChat(String urlImage, String textMessage, String chat, User userSender){
        Message message = new Message(textMessage, userSender, urlImage);
        return FirestoreMessageHelper.getChatCollection()
                .document(chat)
                .collection(COLLECTION_NAME)
                .add(message);
    }
    // --- GET ---

    public static Query getAllMessageForChat(String chat){
        return FirestoreMessageHelper.getChatCollection()
                .document(chat)
                .collection(SECOND_COLLECTION_NAME)
                .orderBy("dateCreated")
                .limit(50);
    }

}
