package com.example.cameinw.api;

import com.example.cameinw.model.Place;
import com.example.cameinw.model.Reservation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ReservationApi {

    @POST("/api/places/{place_id}/reservations")
    Call<Reservation> makeReservation(@Path("place_id") String placeId,
                                       @Body Reservation reservation);

    @GET("/api/users/{user_id}/reservations")
    Call<List<Reservation>> getReservationsByUserId(@Path("user_id") Integer userId);

    @GET("/api/reservations/{reservation_id}/place")
    Call<Place> getPlaceByReservationId(@Path("reservation_id") String reservation_id);
}
