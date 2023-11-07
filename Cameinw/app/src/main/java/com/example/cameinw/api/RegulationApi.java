package com.example.cameinw.api;

import com.example.cameinw.Response.NoContentResponse;
import com.example.cameinw.model.Regulations;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RegulationApi {

    @POST("/api/places/{place_id}/regulations")
    Call<NoContentResponse> createRegulation (@Path("place_id") Integer placeId,
                                              @Body Regulations regulations);

    @PUT("/api/places/{place_id}/regulations/{regulation_id}")
    Call<NoContentResponse> updateRegulation (@Path("place_id") Integer placeId,
                                              @Path("regulation_id") Integer regulationId,
                                              @Body Regulations regulations);
}
