package com.example.cameinw.api;

import com.example.cameinw.Response.NoContentResponse;
import com.example.cameinw.model.Image;
import com.example.cameinw.model.Place;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface PlaceApi {

    @POST("/api/places")
    Call<NoContentResponse> createPlace(@Body Place place);

    @GET("/api/places/availability/{city}/{country}/{guests}/{checkIn}/{checkOut}")
    Call<List<Place>> getAvailablePlaces(@Path("city") String city,
                                                        @Path("country") String country,
                                                        @Path("guests") Integer guests,
                                                        @Path("checkIn") String checkIn,
                                                        @Path("checkOut") String checkOut);

    @GET("/api/places/{place_id}")
    Call<Place> getPlace(@Path("place_id") Integer placeId);


    @Multipart
    @POST("/api/images/places/{place_id}/gallery")
    Call<NoContentResponse> uploadPlacePics(@Part MultipartBody.Part images,
                                             @Path("place_id") Integer placeId);

    @GET("api/images/places/{place_id}/gallery")
    Call<List<Image>> getPlaceImages(@Path("place_id") Integer placeId);

    @PUT("/api/places/{place_id}")
    Call<NoContentResponse> updatePlace(@Path("place_id") Integer placeId,
                                        @Body Place updatedPlace);

    @DELETE("/api/places/{place_id}")
    Call<NoContentResponse> deletePlace(@Path("place_id") Integer placeId);
}
