package com.example.cameinw.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cameinw.activities.ChatActivity;
import com.example.cameinw.util.MessageUtils;
import com.example.cameinw.model.Message;
import com.example.cameinw.R;
import com.example.cameinw.util.UserInfoUtils;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends ArrayAdapter<Message> {

    private TextView usernameTextView, messageTextView, timestampTextView;
    private CircleImageView profileImageView;
    private Context context;
    private SharedPreferences sharedPreferences;
    private String userToken;
    private Integer userId;


    public MessageAdapter(Context context, List<Message> messages, boolean isChat) {
        super(context, 0, messages);
        this.context = context;

        userToken = UserInfoUtils.getUserToken(context);
        userId = UserInfoUtils.getUserId(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        final Message message = getItem(position);

        if (itemView == null) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.message_component, parent, false);
        }

        usernameTextView = itemView.findViewById(R.id.textview_chat_username);
        messageTextView = itemView.findViewById(R.id.textview_chat_text);
        timestampTextView = itemView.findViewById(R.id.textview_chat_timestamp);
        profileImageView = itemView.findViewById(R.id.imageview_chat_profile);

        if (message != null && message.getSender() != null) {
            String senderUsername = message.getSender().getTheUserName();
            usernameTextView.setText(senderUsername);

            Date messageTimestamp = MessageUtils.parseTimestamp(message.getTimestamp());
            String formattedTimestamp = MessageUtils.formatTimestamp(messageTimestamp);

            messageTextView.setText(message.getMessage());
            timestampTextView.setText(formattedTimestamp);
            if (message.getUserImage() != null)
                profileImageView.setImageBitmap(message.getUserImage());
        } else {
            usernameTextView.setText("User Unavailable");
            messageTextView.setText("Message Unavailable");
            timestampTextView.setText("Timestamp Unavailable");
            profileImageView.setImageResource(R.drawable.profile);
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message != null) {
                    Integer otherUserId = getOtherUserId(message.getSender().getId(), message);
                    startChatActivityWithOtherUserId(otherUserId);
                }
            }
        });

        return itemView;
    }

    private Integer getOtherUserId(Integer senderId, Message message) {
        return senderId.equals(userId) ? message.getReceiver().getId() : senderId;
    }

    private void startChatActivityWithOtherUserId(Integer otherUserId) {
        Intent chatIntent = new Intent(context, ChatActivity.class);
        chatIntent.putExtra("other_user_id", otherUserId);
        context.startActivity(chatIntent);
    }
}
