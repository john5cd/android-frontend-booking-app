package com.example.cameinw.api;

import com.example.cameinw.Response.NoContentResponse;
import com.example.cameinw.model.Facilities;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface FacilityApi {

    @POST("api/places/{place_id}/facilities")
    Call<NoContentResponse> createFacility(@Path("place_id") Integer placeId,
                                           @Body Facilities facilities);

    @PUT("api/places/{place_id}/facilities/{facility_id}")
    Call<NoContentResponse> updateFacility(@Path("place_id") Integer placeId,
                                           @Path("facility_id") Integer facilityId,
                                           @Body Facilities facilities);
}
