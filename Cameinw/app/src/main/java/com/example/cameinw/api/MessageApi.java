package com.example.cameinw.api;

import com.example.cameinw.dto.MessageDTO;
import com.example.cameinw.model.Message;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MessageApi {

    @GET("api/users/{user_id}/messages")
    Call<List<MessageDTO>> getUsersMessages(@Path("user_id") Integer userId);

    @GET("api/users/{user_id}/messages/{otherUser_id}")
    Call<List<MessageDTO>> getUsersChat(
            @Path("user_id") Integer userId,
            @Path("otherUser_id") Integer otherUserId
    );

    @FormUrlEncoded
    @POST("api/users/{user_id}/messages/{otherUser_id}")
    Call<Message> createMessage(
            @Path("user_id") Integer senderId,
            @Path("otherUser_id") Integer receiverId,
            @Field("message") String messageText
    );
}

