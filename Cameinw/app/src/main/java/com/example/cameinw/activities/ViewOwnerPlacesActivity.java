package com.example.cameinw.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.cameinw.R;
import com.example.cameinw.Response.NoContentResponse;
import com.example.cameinw.adapter.UserPlaceAdapter;
import com.example.cameinw.projections.PlaceProjection;
import com.example.cameinw.adapter.PlaceRecyclerViewInt;
import com.example.cameinw.api.PlaceApi;
import com.example.cameinw.api.UserApi;
import com.example.cameinw.util.MenuUtils;
import com.example.cameinw.util.RetrofitUtils;
import com.example.cameinw.util.UserInfoUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewOwnerPlacesActivity extends AppCompatActivity implements PlaceRecyclerViewInt {

    private RecyclerView recyclerView;
    private String userToken;
    private Integer userId;
    private List<PlaceProjection> thePlaces;
    private PlaceProjection placeClicked;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_places);

        initializeComponents();
        getUserPlaces();
        getRole();
    }

    private void initializeComponents() {
        MenuUtils.setMenus("Your Places", ViewOwnerPlacesActivity.this);

        recyclerView = findViewById(R.id.recyclerPlaces);
        fab = findViewById(R.id.fabButton);

        userToken = UserInfoUtils.getUserToken(ViewOwnerPlacesActivity.this);
        userId = UserInfoUtils.getUserId(ViewOwnerPlacesActivity.this);

        fab.setOnClickListener(v -> {
            Intent locationIntent = new Intent(ViewOwnerPlacesActivity.this, LocationActivity.class);
            startActivity(locationIntent);
        });
    }

    private void getRole() {
        UserApi userApi = RetrofitUtils.createRequest(UserApi.class, userToken);
        userApi.getUserRole(userId)
                .enqueue(new Callback<NoContentResponse>() {
                    @Override
                    public void onResponse(Call<NoContentResponse> call, Response<NoContentResponse> response) {
                        if (((Integer) response.code()).equals(200)){
                            Log.e("Settings", response.body().getMessage());
                            if (response.body().getMessage().equals("USER")) {
                                fab.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            Log.e("Settings", String.valueOf(response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<NoContentResponse> call, Throwable t) {
                        Log.e("Settings", "problem");
                    }
                });

    }

    private void getUserPlaces() {
        UserApi userApi = RetrofitUtils.createRequest(UserApi.class, userToken);

        userApi.getPlaceByUserId(Integer.valueOf(String.valueOf(userId)))
                .enqueue(new Callback<List<PlaceProjection>>() {
                    @Override
                    public void onResponse(Call<List<PlaceProjection>> call, Response<List<PlaceProjection>> response) {
                        if (((Integer) response.code()).equals(200)) {
                            populateRecyclerView(response.body());
                            thePlaces = response.body();
                        } else {
                            handleUnsuccessfulCall(response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<PlaceProjection>> call, Throwable t) {
                        handleFailure(t);
                    }
                });
    }

    private void handleFailure(Throwable t) {
        Log.e("UpdatePlaceActivity", "Error: " + t.getMessage());
    }

    private void handleUnsuccessfulCall(int code) {
        Log.e("UpdatePlaceActivity", "Error code: " + String.valueOf(code));
    }

    private void populateRecyclerView(List<PlaceProjection> places) {
        UserPlaceAdapter userPlaceAdapter = new UserPlaceAdapter(places, ViewOwnerPlacesActivity.this, this);
        recyclerView.setAdapter(userPlaceAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onPlaceClick(int position) {
        placeClicked = thePlaces.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(ViewOwnerPlacesActivity.this);
        builder.setTitle("Choose action");
        builder.setPositiveButton("Delete",
                (dialog, id) -> {
                    Log.e("UpdatePlaces", "Delete");
                    PlaceApi placeApi = RetrofitUtils.createRequest(PlaceApi.class, userToken);
                    placeApi.deletePlace(placeClicked.getId())
                            .enqueue(new Callback<NoContentResponse>() {
                                @Override
                                public void onResponse(Call<NoContentResponse> call, Response<NoContentResponse> response) {
                                    if (((Integer) response.code()).equals(204)) {
                                        refreshActivity();
                                    }
                                }

                                @Override
                                public void onFailure(Call<NoContentResponse> call, Throwable t) {

                                }
                            });
                });

        builder.setNeutralButton("View",
                (dialog, id) -> {
                    Intent viewIntent = new Intent(ViewOwnerPlacesActivity.this, ShowPlaceActivity.class);
                    viewIntent.putExtra("id", placeClicked.getId());
                    startActivity(viewIntent);
                });

        builder.setNegativeButton("Update",
                (dialog, id) -> {
                    Intent updateIntent = new Intent(ViewOwnerPlacesActivity.this, UpdateSinglePlaceActivity.class);
                    updateIntent.putExtra("id", placeClicked.getId());
                    startActivity(updateIntent);
                });
        builder.create().show();
    }

    private void refreshActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}