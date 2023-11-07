package com.example.cameinw.api;

import com.example.cameinw.Response.NoContentResponse;
import com.example.cameinw.model.Image;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ImageApi {

    @GET("/api/images/users/{user_id}/image")
    Call<ResponseBody> getUserImage(@Path("user_id") Integer userId);

    @GET("/api/images/places/{place_id}/gallery")
    Call<List<Image>> getImagesByPlaceId(@Path("place_id") Integer placeId);

    @GET("/api/images/places/{place_id}/gallery/{image_id}")
    Call<ResponseBody> getPlaceImageById(@Path("place_id") Integer placeId,
                                         @Path("image_id") Integer imageId);


    @Multipart
    @PUT("api/images/users/{user_id}/image")
    Call<NoContentResponse> updateProfilePic(@Path("user_id") Integer userId,
                                             @Part MultipartBody.Part image);
}