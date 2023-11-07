package com.example.cameinw.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.cameinw.R;
import com.example.cameinw.adapter.PlaceAdapter;
import com.example.cameinw.adapter.PlaceRecyclerViewInt;
import com.example.cameinw.api.PlaceApi;
import com.example.cameinw.model.Place;
import com.example.cameinw.util.MenuUtils;
import com.example.cameinw.util.RetrofitUtils;
import com.example.cameinw.util.UserInfoUtils;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultsActivity extends AppCompatActivity implements PlaceRecyclerViewInt {

    private String token, checkIn, checkOut, city, country;
    private RecyclerView recyclerView;
    private List<Place> availablePlaces;
    private TextView loadPlaces;
    private Integer numberOfPlacesLoaded = 2, guests;
    private List<Place> placeSub;
    private PlaceAdapter placeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        initializeComponents();
        loadAvailablePlaces();
    }

    private void initializeComponents() {
        MenuUtils.setMenus("Search Results", SearchResultsActivity.this);

        token = UserInfoUtils.getUserToken(SearchResultsActivity.this);

        loadPlaces = findViewById(R.id.morePlaces);
        recyclerView = findViewById(R.id.search_results_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        checkIn = getIntent().getStringExtra("checkIn");
        checkOut = getIntent().getStringExtra("checkOut");
        guests = getIntent().getIntExtra("guests", 0);
        city = getIntent().getStringExtra("city");
        country = getIntent().getStringExtra("country");
    }

    private void loadAvailablePlaces() {

        PlaceApi placeApi = RetrofitUtils.createRequest(PlaceApi.class, token);

        placeApi.getAvailablePlaces(city,
                                    country,
                                    guests,
                                    checkIn,
                                    checkOut)
                .enqueue(new Callback<List<Place>>() {
                    @Override
                    public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                        Integer code = response.code();
                        if (code.equals(200)) {
                            handleSuccessfulSearch(response.body());
                        } else {
                            handleUnsuccessfulSearch(response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Place>> call, Throwable t) {
                        handleFailure(t);
                    }
                });
    }

    private void handleFailure(Throwable t) {
        Logger.getLogger(SearchResultsActivity.class.getName()).log(Level.SEVERE, "Error occurred", t);
    }

    private void handleUnsuccessfulSearch(int code) {
        Logger.getLogger(SearchResultsActivity.class.getName()).log(Level.SEVERE, "Error occurred with code: ", code);
    }

    private void handleSuccessfulSearch(List<Place> availablePlacesList) {
        availablePlaces = availablePlacesList;
        Logger.getLogger(SearchResultsActivity.class.getName()).log(Level.SEVERE, String.valueOf(availablePlacesList.size()));
        populateAvailablePlacesList(availablePlacesList);
    }

    private void populateAvailablePlacesList(List<Place> availablePlacesList) {
        loadPlaces.setVisibility(View.VISIBLE);
        if (numberOfPlacesLoaded<availablePlacesList.size()) {
            placeSub = availablePlacesList.subList(0, numberOfPlacesLoaded);
            placeAdapter = new PlaceAdapter(this, placeSub, SearchResultsActivity.this);
            recyclerView.setAdapter(placeAdapter);
        }

        loadPlaces.setOnClickListener(v -> {
            numberOfPlacesLoaded+=2;
            Logger.getLogger(SearchResultsActivity.class.getName()).log(Level.SEVERE, String.valueOf(numberOfPlacesLoaded));

            if (numberOfPlacesLoaded<availablePlacesList.size()) {
                placeSub = availablePlacesList.subList(0, numberOfPlacesLoaded);
                placeAdapter = new PlaceAdapter(this, placeSub, SearchResultsActivity.this);
                recyclerView.setAdapter(placeAdapter);
            } else {
                placeSub = availablePlacesList;
                placeAdapter = new PlaceAdapter(this, placeSub, SearchResultsActivity.this);
                recyclerView.setAdapter(placeAdapter);
                loadPlaces.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onPlaceClick(int position) {
        Place placeClicked = availablePlaces.get(position);

        Intent showPlaceIntent = new Intent(SearchResultsActivity.this, ShowPlaceActivity.class);
        showPlaceIntent.putExtra("id", placeClicked.getId());
        showPlaceIntent.putExtra("checkIn", checkIn);
        showPlaceIntent.putExtra("checkOut", checkOut);
        startActivity(showPlaceIntent);
    }

}