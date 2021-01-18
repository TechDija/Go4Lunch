package com.dija.go4lunch.ui.adapters;

import android.annotation.SuppressLint;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.dija.go4lunch.R;
import com.dija.go4lunch.databinding.ChatItemBinding;
import com.dija.go4lunch.models.Message;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatAdapter extends FirestoreRecyclerAdapter<Message, ChatAdapter.MessageViewHolder> {

    public interface Listener {
        void onDataChanged();
    }

    private final RequestManager glide;
    private final String idCurrentUser;
    private Listener callback;

    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Message> options, RequestManager glide, String idCurrentUser, Listener callback) {
        super(options);
        this.glide = glide;
        this.idCurrentUser = idCurrentUser;
        this.callback = callback;
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull Message model) {
            holder.updateWithMessage(model, this.idCurrentUser, this.glide);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutinflater = LayoutInflater.from(parent.getContext());
        ChatItemBinding itemBinding = ChatItemBinding.inflate(layoutinflater, parent, false);
        return new MessageViewHolder(itemBinding);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        this.callback.onDataChanged();
    }

    // region MessageViewHolder
    public class MessageViewHolder extends RecyclerView.ViewHolder {
        private final int colorCurrentUser;
        private final int colorRemoteUser;
        private final ChatItemBinding binding;

        public MessageViewHolder(@NonNull ChatItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            colorCurrentUser = ContextCompat.getColor(itemView.getContext(), R.color.orange);
            colorRemoteUser = ContextCompat.getColor(itemView.getContext(), R.color.design_default_color_primary);
        }

        public void updateWithMessage(Message message, String currentUserId, RequestManager glide) {
                boolean isCurrentUser = message.getUserSender().getUid().equals(currentUserId);
                //update message textview
                binding.textMessageTextView.setText(message.getMessage());
                binding.textMessageTextView.setTextAlignment(isCurrentUser ?
                        View.TEXT_ALIGNMENT_TEXT_END : View.TEXT_ALIGNMENT_TEXT_START);
                //update date textview
                binding.textViewDate.setText(this.convertDateToHour(message.getDateCreated()));
                //update profile picture
                if (message.getUserSender().getUrlPicture() != null) {
                    glide.load(message.getUserSender().getUrlPicture())
                            .apply(RequestOptions.circleCropTransform())
                            .into(binding.itemProfilePicture);
                }
                //update sent image
                if (message.getUrlImage() != null) {
                    glide.load(message.getUrlImage())
                            .into(binding.containerImageSentCardviewImage);
                    binding.containerImageSentCardviewImage.setVisibility(View.VISIBLE);
                } else {
                    binding.containerImageSentCardviewImage.setVisibility(View.GONE);
                }
                //update message bubble color
                ((GradientDrawable) binding.textMessageContainer.getBackground()).setColor(isCurrentUser ?
                        colorCurrentUser : colorRemoteUser);
                //update alignment
                updateDesignDependingOnUser(isCurrentUser);
            }


        private void updateDesignDependingOnUser(boolean isSender) {

            // PROFILE CONTAINER
            RelativeLayout.LayoutParams paramsLayoutHeader = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramsLayoutHeader.addRule(isSender ? RelativeLayout.ALIGN_PARENT_RIGHT : RelativeLayout.ALIGN_PARENT_LEFT);
            binding.itemProfileContainer.setLayoutParams(paramsLayoutHeader);

            // MESSAGE CONTAINER
            RelativeLayout.LayoutParams paramsLayoutContent = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramsLayoutContent.addRule(isSender ? RelativeLayout.LEFT_OF : RelativeLayout.RIGHT_OF, R.id.item_profile_container);
            binding.textMessageContainer.setLayoutParams(paramsLayoutContent);

            // CARDVIEW IMAGE SEND
            RelativeLayout.LayoutParams paramsImageView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramsImageView.addRule(isSender ? RelativeLayout.ALIGN_LEFT : RelativeLayout.ALIGN_RIGHT, R.id.text_message_container);
            binding.imageSentCardview.setLayoutParams(paramsImageView);

            binding.chatItemRootView.requestLayout();
        }

        private String convertDateToHour(Date date) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dfTime = new SimpleDateFormat("HH:mm");
            return dfTime.format(date);
        }
    }
    //endregion
}
