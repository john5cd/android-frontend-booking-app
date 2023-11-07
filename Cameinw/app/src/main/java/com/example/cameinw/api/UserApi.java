package com.example.cameinw.api;

import com.example.cameinw.Response.NoContentResponse;
import com.example.cameinw.projections.PlaceProjection;
import com.example.cameinw.Response.RegisterResponse;
import com.example.cameinw.dto.UserDTO;
import com.example.cameinw.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserApi {

    @POST("/api/auth/register")
    Call<RegisterResponse> signUp(@Body User user);

    @POST("/api/auth/login")
    Call<RegisterResponse> signIn(@Body User user);

    @Headers("Content-Type: application/json")
    @GET("/api/users/validUser")
    Call<RegisterResponse> isTokenValid();

    @Multipart
    @POST("/api/images/users/{user_id}/image")
    Call<NoContentResponse> uploadProfilePic(@Part MultipartBody.Part image,
                                             @Path("user_id") Integer userId);

    @GET("/api/users/{user_id}/places")
    Call<List<PlaceProjection>> getPlaceByUserId(@Path("user_id") Integer userId);

    @GET("/api/users/{user_id}")
    Call<UserDTO> getUserById(@Path("user_id") Integer userId);

    @PUT("/api/users/{user_id}")
    Call<NoContentResponse> updateUser(@Path("user_id") Integer userId , @Body User
            user);

    @GET("api/reviews/{review_id}/user")
    Call<UserDTO> getUserByReviewId(@Path("review_id") Integer reviewId);

    @GET("api/users/{user_id}/role")
    Call<NoContentResponse> getUserRole(@Path("user_id") Integer userId);

    @GET("/api/places/{place_id}/owner")
    Call<UserDTO> getOwner(@Path("place_id") Integer placeId);
}

