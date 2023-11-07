package com.example.cameinw.api;

import com.example.cameinw.Response.NoContentResponse;
import com.example.cameinw.Response.ReviewResponse;
import com.example.cameinw.model.Review;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ReviewApi {

    @POST("api/places/{place_id}/reviews")
    Call<NoContentResponse> createReview (@Path("place_id") Integer placeId,
                                          @Body ReviewResponse review);
}
