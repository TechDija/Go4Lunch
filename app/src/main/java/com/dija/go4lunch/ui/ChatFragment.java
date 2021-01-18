package com.dija.go4lunch.ui;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dija.go4lunch.R;
import com.dija.go4lunch.api.FirestoreMessageHelper;
import com.dija.go4lunch.api.FirestoreUserHelper;
import com.dija.go4lunch.databinding.FragmentChatBinding;
import com.dija.go4lunch.models.Message;
import com.dija.go4lunch.models.User;
import com.dija.go4lunch.ui.adapters.ChatAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

public class ChatFragment extends Fragment implements ChatAdapter.Listener {

    private FragmentChatBinding binding;
    private ChatAdapter mChatAdapter;
    @Nullable
    private User modelCurrentUser;
    private String chat = "chat";
    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private MutableLiveData<User> modelCurrentUserLiveData = new MutableLiveData<>();
    private static final int RC_iIMAGE_PERMS = 100;
    private Uri uriImageSelected;
    private static final int RC_CHOOSE_PHOTO = 200;

    public ChatFragment() {
    }

    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getCurrentUserFromFirestore().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                modelCurrentUser = user;
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        this.configureRecyclerView();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSendMessage();
            }
        });
        binding.activityMentorChatAddFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddFile();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handleResponse(requestCode, resultCode, data);
    }

    //region ACTIONS
    //------------------
    //ACTIONS
    //------------------

    private void configureRecyclerView() {
        mChatAdapter = new ChatAdapter(generateOptionsForAdapter(FirestoreMessageHelper.getAllMessageForChat(chat)), Glide.with(this),
                FirebaseAuth.getInstance().getCurrentUser().getUid(), this);
        mChatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                binding.chatRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount());
            }
        });
        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.chatRecyclerView.setAdapter(mChatAdapter);
    }

    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();
    }

    private MutableLiveData<User> getCurrentUserFromFirestore() {
        FirestoreUserHelper.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        modelCurrentUser = documentSnapshot.toObject(User.class);
                        modelCurrentUserLiveData.setValue(modelCurrentUser);
                    }
                });
        return modelCurrentUserLiveData;
    }

    public void onClickSendMessage() {
                if (!TextUtils.isEmpty(binding.chatMessageEditText.getText()) && modelCurrentUser != null) {
                    if (binding.chatImageChosenPreview.getDrawable() == null) {
                        FirestoreMessageHelper.createMessageForChat(binding.chatMessageEditText.getText().toString(), chat, modelCurrentUser);
                        binding.chatMessageEditText.setText("");
                    } else {
                        uploadPictureInFirebaseAndSendMessage(binding.chatMessageEditText.getText().toString());
                        binding.chatMessageEditText.setText("");
                        binding.chatImageChosenPreview.setImageDrawable(null);
                    }
                }
    }

    private void uploadPictureInFirebaseAndSendMessage(final String message) {
        String uuid = UUID.randomUUID().toString();
        StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
        mImageRef.putFile(this.uriImageSelected)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String pathImageSavedInFirebase = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                        FirestoreMessageHelper.createMessageWithImageForChat(pathImageSavedInFirebase, message, chat, modelCurrentUser);
                    }
                });
    }

    @AfterPermissionGranted(RC_iIMAGE_PERMS)
    public void onClickAddFile() {
        if (!EasyPermissions.hasPermissions(getContext(), PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_file_access), RC_iIMAGE_PERMS, PERMS);
            return;
        }
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RC_CHOOSE_PHOTO);
    }

    private void handleResponse(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) { //SUCCESS
                this.uriImageSelected = data.getData();
                Glide.with(this) //SHOWING PREVIEW OF IMAGE
                        .load(this.uriImageSelected)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.chatImageChosenPreview);
            } else {
                Toast.makeText(getContext(), getString(R.string.no_image_chosen), Toast.LENGTH_SHORT).show();
            }
        }
    }


//endregion

    // region CALLBACK
//-----------------
//CALLBACK
//-----------------
    @Override
    public void onDataChanged() {
        binding.recyclerViewEmpty.setVisibility(this.mChatAdapter.getItemCount()
                == 0 ? View.VISIBLE : View.GONE);
    }
    // endregion
}