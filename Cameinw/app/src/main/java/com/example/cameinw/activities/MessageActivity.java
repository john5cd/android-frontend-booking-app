package com.example.cameinw.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {
    private ListView messageListView;
    private MessageAdapter messageAdapter;
    private List<MessageDTO> messageDTOList;
    private List<Message> messages = new ArrayList<>();
    private String userToken;
    private Integer userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        initializeComponents();
    }

    private void initializeComponents() {
        MenuUtils.setMenus("Messages", MessageActivity.this);

        messageListView = findViewById(R.id.message_list_view);
        messageDTOList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messages, false);
        messageListView.setAdapter(messageAdapter);

        userToken = UserInfoUtils.getUserToken(this);
        userId = UserInfoUtils.getUserId(this);
        if (userId != null) {
            fetchMessages();
        } else {
            Toast.makeText(this, "Unable to fetch user messages", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchMessages() {
        MessageApi messageApi = RetrofitUtils.createRequest(MessageApi.class, userToken);
        Call<List<MessageDTO>> call = messageApi.getUsersMessages(userId);
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
        Log.e("MessageActivity", "Unsuccessful response: " + responseCode);
    }

    private void handleFailure(String errorMessage) {
        Log.e("MessageActivity", "Error occurred: " + errorMessage);
    }

    private void handleImageFetchError() {
        Toast.makeText(this, "Error fetching user image", Toast.LENGTH_SHORT).show();
    }


    private void updateUserImage(int userId, Bitmap imageBitmap) {
        for (Message message : messages) {
            if (message.getSender().getId() == userId) {
                message.setUserImage(imageBitmap);
            }
        }

        messageAdapter.notifyDataSetChanged();
    }

}