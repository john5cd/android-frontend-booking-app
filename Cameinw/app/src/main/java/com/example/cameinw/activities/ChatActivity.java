package com.example.cameinw.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.cameinw.util.ImageUtils;
import com.example.cameinw.util.MenuUtils;
import com.example.cameinw.util.MessageUtils;
import com.example.cameinw.util.RetrofitUtils;
import com.example.cameinw.util.UserInfoUtils;
import com.example.cameinw.adapter.MessageAdapter;
import com.example.cameinw.api.MessageApi;
import com.example.cameinw.dto.MessageDTO;
import com.example.cameinw.model.Message;
import com.example.cameinw.R;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private EditText messageEditText;
    private ImageButton sendButton;
    private ListView messageListView;
    private MessageAdapter messageAdapter;
    private List<MessageDTO> messageDTOList;
    private List<Message> messages = new ArrayList<>();
    private String userToken;
    private Integer currentUserId;
    private Integer otherUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setUpSideMenu();
        initializeComponents();
    }

    private void setUpSideMenu() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tool = findViewById(R.id.toolbarText);
        tool.setText("Chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        MenuUtils.setSideMenu(drawer, navigationView, toolbar, ChatActivity.this);
        MenuUtils.setTopBar("Chat", ChatActivity.this);
    }

    private void initializeComponents() {
        messageListView = findViewById(R.id.message_list_view);
        messageDTOList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messages, false);
        messageListView.setAdapter(messageAdapter);

        messageEditText = findViewById(R.id.edittext_chat_message);
        sendButton = findViewById(R.id.button_send_message);

        userToken = UserInfoUtils.getUserToken(this);
        currentUserId = UserInfoUtils.getUserId(this);
        otherUserId = getIntent().getIntExtra("other_user_id", 0);

        if (currentUserId != null) {
            fetchChatMessages();
        } else {
            Toast.makeText(this, "Unable to fetch user messages", Toast.LENGTH_SHORT).show();
        }

        // Set a click listener for the send button
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString();
                if (!message.isEmpty()) {
                    sendMessage(message);
                } else {
                    Toast.makeText(ChatActivity.this, "Text is empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchChatMessages() {
        MessageApi messageApi = RetrofitUtils.createRequest(MessageApi.class, userToken);
        Call<List<MessageDTO>> call = messageApi.getUsersChat(currentUserId, otherUserId);
        call.enqueue(new Callback<List<MessageDTO>>() {
            @Override
            public void onResponse(Call<List<MessageDTO>> call, Response<List<MessageDTO>> response) {
                if (response.isSuccessful()) {
                    handleSuccessfulMessageResponse(response.body());
                } else {
                    handleUnsuccessfulResponse(response.code());
                }
            }

            @Override
            public void onFailure(Call<List<MessageDTO>> call, Throwable t) {
                handleFailure(t.getMessage());
            }
        });
    }

    private void handleSuccessfulMessageResponse(List<MessageDTO> messageDTOs) {
        List<Integer> userIdsToFetchImagesFor = new ArrayList<>();
        MessageUtils.processMessageDTOs(messageDTOs, messages, userIdsToFetchImagesFor);

        messageAdapter.notifyDataSetChanged();
        messageListView.smoothScrollToPosition(messageAdapter.getCount());

        ImageUtils.fetchMultipleUsersImageWithRetry(userIdsToFetchImagesFor, userToken, 5, new ImageUtils.ImageFetchCallback() {
            @Override
            public void onImageFetchSuccess(int userId, Bitmap imageBitmap) {
                updateUserImage(userId, imageBitmap);
            }

            @Override
            public void onImageFetchError(int userId) {
                handleImageFetchError();
            }
        });
    }

    private void handleUnsuccessfulResponse(int responseCode) {
        Log.e("ChatActivity", "Unsuccessful response: " + responseCode);
    }

    private void handleFailure(String errorMessage) {
        Log.e("ChatActivity", "Error occurred: " + errorMessage);
    }

    private void handleImageFetchError() {
        Toast.makeText(this, "Error fetching user image", Toast.LENGTH_SHORT).show();
    }

    private void updateUserImage(int userId, Bitmap userImageBitmap) {
        for (Message message : messages) {
            if (message.getSender().getId() == userId) {
                message.setUserImage(userImageBitmap);
            }
        }
        messageAdapter.notifyDataSetChanged();
    }

    private void sendMessage(String messageContent) {
        MessageApi messageApi = RetrofitUtils.createRequest(MessageApi.class, userToken);
        Call<Message> call = messageApi.createMessage(currentUserId, otherUserId, messageContent);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful()) {
                    Message newMessage = response.body();
                    messages.add(newMessage);
                    messageAdapter.notifyDataSetChanged();
                    messageListView.smoothScrollToPosition(messageAdapter.getCount());
                    messageEditText.setText("");

                    refreshActivity();
                } else {
                    handleSendMessageError();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                handleSendMessageFailure(t);
            }
        });
    }

    private void refreshActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void handleSendMessageError() {
        Toast.makeText(this, "Error sending message", Toast.LENGTH_SHORT).show();
    }

    private void handleSendMessageFailure(Throwable t) {
        Toast.makeText(this, "Failure on message send", Toast.LENGTH_SHORT).show();
        Log.e("ChatActivity", "Error: " + t.getMessage());
    }
}