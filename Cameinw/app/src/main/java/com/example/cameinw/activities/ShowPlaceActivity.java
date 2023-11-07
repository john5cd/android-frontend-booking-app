package com.example.cameinw.activities;


import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;
import androidx.viewpager.widget.ViewPager;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cameinw.R;
import com.example.cameinw.adapter.ImageAdapter;
import com.example.cameinw.adapter.ReviewAdapter;
import com.example.cameinw.api.ImageApi;
import com.example.cameinw.api.PlaceApi;
import com.example.cameinw.api.ReservationApi;
import com.example.cameinw.api.UserApi;
import com.example.cameinw.dto.UserDTO;
import com.example.cameinw.enums.PaymentMethod;
import com.example.cameinw.model.Image;
import com.example.cameinw.model.Place;
import com.example.cameinw.model.Reservation;
import com.example.cameinw.model.User;
import com.example.cameinw.service.AverageRating;
import com.example.cameinw.util.ImageUtils;
import com.example.cameinw.util.MenuUtils;
import com.example.cameinw.util.RetrofitUtils;
import com.example.cameinw.util.UserInfoUtils;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowPlaceActivity extends AppCompatActivity {

    TextView name, rating, cost, bedrooms, beds, bathrooms, description, guests, area, openMaps,
                hasFreeParking, hasFreeWiFi, hasbreakfast, hasbalcony, hasSwimmingPool, nonSmoking,
                ageRestriction, arePetsAllowed, areEventsAllowed, smokingAllowed, arrival, departure,
                cancelPolicy, paymentMethod, quietHours;

    MaterialButton reservationButton, messageButton;
    ViewPager viewPlacePics;
    ImageView arrow1, arrow2, arrow3, arrow4, mapPin;
    String token, checkIn, checkOut;
    Integer placeId, userId;
    List<Bitmap> fetchedImagesList = new ArrayList<>();
    LinearLayout linearLayout, linearLayout2, linearLayout3, linearLayout4;
    GridLayout gridLayout, gridLayout2, gridLayout3;
    CardView cardView, cardView2, cardView3, cardView4;
    RecyclerView recyclerView;
    Integer timesClicked = 1, timesClicked2 = 1, timesClicked3 = 1, timesClicked4 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_place);

        initializeComponents();
        chat(placeId);
        cardAnimations();
        makeReservation();
        getPlaceInformation();
        setPlaceImages();
    }

    private void initializeComponents() {
        MenuUtils.setMenus("Search", ShowPlaceActivity.this);

        Intent intent = getIntent();
        placeId = intent.getIntExtra("id", 0);
        checkIn = intent.getStringExtra("checkIn");
        checkOut = intent.getStringExtra("checkOut");

        token = UserInfoUtils.getUserToken(ShowPlaceActivity.this);
        userId = UserInfoUtils.getUserId(ShowPlaceActivity.this);

        name = findViewById(R.id.nameShowPlace);
        cost = findViewById(R.id.costShowPlace);
        rating = findViewById(R.id.place_rating);
        bedrooms = findViewById(R.id.setBedroom_showPlace);
        beds = findViewById(R.id.setBeds_showPlace);
        bathrooms = findViewById(R.id.setBathrooms_showPlace);
        area = findViewById(R.id.setArea_showPlace);
        guests = findViewById(R.id.setGuests_showPlace);
        description = findViewById(R.id.descriptionShowPlace);
        linearLayout = findViewById(R.id.linear_showPlace);
        linearLayout2 = findViewById(R.id.linear2_showPlace);
        linearLayout3 = findViewById(R.id.linear3_showPlace);
        linearLayout4 = findViewById(R.id.linear4_showPlace);
        cardView = findViewById(R.id.card_showPlace);
        cardView2 = findViewById(R.id.card2_showPlace);
        cardView3 = findViewById(R.id.card3_showPlace);
        cardView4 = findViewById(R.id.card4_showPlace);
        gridLayout = findViewById(R.id.grid_showPlace);
        gridLayout2 = findViewById(R.id.grid2_showPlace);
        gridLayout3 = findViewById(R.id.grid3_showPlace);
        openMaps = findViewById(R.id.openMaps_ShowPlace);
        hasFreeParking = findViewById(R.id.setfreeParking_showPlace);
        hasbalcony = findViewById(R.id.setBalcony_showPlace);
        hasbreakfast = findViewById(R.id.setBreakfast_showPlace);
        hasSwimmingPool = findViewById(R.id.setSwimming_pool_showPlace);
        hasFreeWiFi = findViewById(R.id.setfree_WiFi_showPlace);
        nonSmoking = findViewById(R.id.setNonSmoking_showPlace);
        ageRestriction = findViewById(R.id.setAge_restriction_showPlace);
        arePetsAllowed = findViewById(R.id.setPets_allowed_showPlace);
        areEventsAllowed = findViewById(R.id.setEvents_allowed_showPlace);
        smokingAllowed = findViewById(R.id.setSmoking_allowed_showPlace);
        arrival = findViewById(R.id.setArrival_time_showPlace);
        departure = findViewById(R.id.setDeparture_time_showPlace);
        cancelPolicy = findViewById(R.id.setCancellation_policy_showPlace);
        paymentMethod = findViewById(R.id.setPayment_methods_showPlace);
        quietHours = findViewById(R.id.setQuiet_hours_showPlace);
        viewPlacePics = findViewById(R.id.placePics_viewPager);
        recyclerView = findViewById(R.id.reviewRecycler);
        arrow1 = findViewById(R.id.arrow_details);
        arrow2 = findViewById(R.id.arrow_facility);
        arrow3 = findViewById(R.id.arrow_regulation);
        arrow4 = findViewById(R.id.arrow_review);
        reservationButton = findViewById(R.id.reservationButton);
        mapPin = findViewById(R.id.map_pin_ShowPlace);
        messageButton = findViewById(R.id.messageButtonShowPlace);
    }

    private void chat(Integer placeId) {
        messageButton.setOnClickListener(v -> {

            UserApi userApi = RetrofitUtils.createRequest(UserApi.class, token);
            userApi.getOwner(placeId)
                    .enqueue(new Callback<UserDTO>() {
                        @Override
                        public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                            if (((Integer) response.code()).equals(200)) {
                                handleSuccessfulGetOwner(response.body());
                            } else {
                                handleUnsuccessfulGetOwner(response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<UserDTO> call, Throwable t) {
                            handleFailedCall(t);
                        }
                    });
        });

    }

    private void handleFailedCall(Throwable t) {
        Log.d("Show Place", String.valueOf(t));
    }

    private void handleUnsuccessfulGetOwner(Integer code) {
        Log.d("Show Place", String.valueOf(code));
    }

    private void handleSuccessfulGetOwner(UserDTO user) {
        if (!user.getUserId().equals(userId)){
            Intent chatIntent = new Intent(ShowPlaceActivity.this, ChatActivity.class);
            chatIntent.putExtra("other_user_id", user.getUserId());
            startActivity(chatIntent);
        } else {
            Toast.makeText(ShowPlaceActivity.this, "You are the owner!", Toast.LENGTH_SHORT).show();
        }
    }

    private void cardAnimations() {
        cardView.setOnClickListener(v -> {
            if (timesClicked%2==0) {
                gridLayout.setVisibility(View.GONE);
                arrow1.setImageResource(R.drawable.arrow_right);
            } else {
                gridLayout.setVisibility(View.VISIBLE);
                arrow1.setImageResource(R.drawable.arrow_down);
            }
            timesClicked+=1;

            TransitionManager.beginDelayedTransition(linearLayout, new AutoTransition());
        });
        linearLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        cardView2.setOnClickListener(v -> {

            if (timesClicked2%2==0) {
                gridLayout2.setVisibility(View.GONE);
                arrow2.setImageResource(R.drawable.arrow_right);
            } else {
                gridLayout2.setVisibility(View.VISIBLE);
                arrow2.setImageResource(R.drawable.arrow_down);
            }
            timesClicked2+=1;

            TransitionManager.beginDelayedTransition(linearLayout2, new AutoTransition());
        });
        linearLayout2.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        cardView3.setOnClickListener(v -> {

            if (timesClicked3%2==0) {
                gridLayout3.setVisibility(View.GONE);
                arrow3.setImageResource(R.drawable.arrow_right);
            } else {
                gridLayout3.setVisibility(View.VISIBLE);
                arrow3.setImageResource(R.drawable.arrow_down);
            }
            timesClicked3+=1;

            TransitionManager.beginDelayedTransition(linearLayout3, new AutoTransition());
        });
        linearLayout3.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        cardView4.setOnClickListener(v -> {
            Log.d("click reviews", "clicked");
            if (timesClicked4%2==0) {
                recyclerView.setVisibility(View.GONE);
                arrow4.setImageResource(R.drawable.arrow_right);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                arrow4.setImageResource(R.drawable.arrow_down);
            }
            timesClicked4+=1;

            TransitionManager.beginDelayedTransition(linearLayout4, new AutoTransition());
        });
        linearLayout4.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    }

    private void makeReservation() {
        reservationButton.setOnClickListener(v -> {
            User user = new User();
            user.setId(userId);

            Reservation reservation = new Reservation();
            reservation.setCheckIn(checkIn);
            reservation.setCheckOut(checkOut);
            reservation.setUser(user);

            ReservationApi reservationApi = RetrofitUtils.createRequest(ReservationApi.class, token);
            reservationApi.makeReservation(String.valueOf(placeId), reservation)
                    .enqueue(new Callback<Reservation>() {
                        @Override
                        public void onResponse(Call<Reservation> call, Response<Reservation> response) {
                            if (response.code()==201) {
                                handleSuccessfulReservation();
                            } else {
                                handleUnsuccessfulReservation();
                            }
                            reservationButton.setEnabled(false);
                        }

                        @Override
                        public void onFailure(Call<Reservation> call, Throwable t) {
                            handleFailedCall(t);
                        }
                    });
        });

    }

    private void handleUnsuccessfulReservation() {
        Toast.makeText(ShowPlaceActivity.this, "Reservation already made!", Toast.LENGTH_SHORT).show();
    }

    private void handleSuccessfulReservation() {
        Toast.makeText(ShowPlaceActivity.this, "Reservation made!", Toast.LENGTH_SHORT).show();
    }

    private void getPlaceInformation() {
        PlaceApi placeApi = RetrofitUtils.createRequest(PlaceApi.class, token);

        placeApi.getPlace(placeId)
                .enqueue(new Callback<Place>() {
                    @Override
                    public void onResponse(Call<Place> call, Response<Place> response) {
                        if (((Integer) response.code()).equals(200)) {
                            setPlaceInformation(response.body());
                        } else {
                            handleUnsuccessfulPlaceCall(response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Place> call, Throwable t) {
                        handleFailedCall(t);
                    }
                });
    }

    private void handleUnsuccessfulPlaceCall(int code) {
        Log.e("Get Place Call", "Unsuccessful response: " + code);
    }

    private void setPlaceInformation(Place place) {
        name.setText(place.getName());
        cost.setText(place.getCost().toString() + " per day");

        rating.setText(AverageRating.getAveragePlaceRating(place));
        openMaps.setText(place.getAddress()+", "+place.getCity()+", "+place.getCountry());
        description.setText(place.getDescription());
        guests.setText(place.getGuests().toString());
        beds.setText(place.getBeds().toString());
        bedrooms.setText(place.getBedrooms().toString());
        bathrooms.setText(place.getBathrooms().toString());
        area.setText(place.getArea().toString());

        arrival.setText(place.getRegulations().getArrivalTime());
        departure.setText(place.getRegulations().getDepartureTime());
        cancelPolicy.setText(place.getRegulations().getCancellationPolicy());
        quietHours.setText(place.getRegulations().getQuietHours());

        ReviewAdapter reviewAdapter = new ReviewAdapter(place.getReviews(), ShowPlaceActivity.this);
        recyclerView.setAdapter(reviewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (place.getRegulations().getPaymentMethod().equals(PaymentMethod.CARD_ONLY)) {
            paymentMethod.setText("Card Only");
        } else if (place.getRegulations().getPaymentMethod().equals(PaymentMethod.CASH_ONLY)) {
            paymentMethod.setText("Cash Only");
        } else if (place.getRegulations().getPaymentMethod().equals(PaymentMethod.CASH_AND_CARD)) {
            paymentMethod.setText("Cash & Card");
        }

        if (place.getFacilities().getHasFreeParking()) {
            hasFreeParking.setText(" ✓ ");
            hasFreeParking.setTextColor(getResources().getColor(R.color.green));
        }
        if (place.getFacilities().getHasbalcony()) {
            hasbalcony.setText(" ✓ ");
            hasbalcony.setTextColor(getResources().getColor(R.color.green));
        }
        if (place.getFacilities().getHasbreakfast()) {
            hasbreakfast.setText(" ✓ ");
            hasbreakfast.setTextColor(getResources().getColor(R.color.green));
        }
        if (place.getFacilities().getHasSwimmingPool()) {
            hasSwimmingPool.setText(" ✓ ");
            hasSwimmingPool.setTextColor(getResources().getColor(R.color.green));
        }
        if (place.getFacilities().getHasFreeWiFi()) {
            hasFreeWiFi.setText(" ✓ ");
            hasFreeWiFi.setTextColor(getResources().getColor(R.color.green));
        }
        if (place.getFacilities().getNonSmoking()) {
            nonSmoking.setText(" ✓ ");
            nonSmoking.setTextColor(getResources().getColor(R.color.green));
        }
        if (place.getRegulations().isAgeRestriction()) {
            ageRestriction.setText(" ✓ ");
            ageRestriction.setTextColor(getResources().getColor(R.color.green));
        }
        if (place.getRegulations().isArePetsAllowed()) {
            arePetsAllowed.setText(" ✓ ");
            arePetsAllowed.setTextColor(getResources().getColor(R.color.green));
        }
        if (place.getRegulations().isAreEventsAllowed()) {
            areEventsAllowed.setText(" ✓ ");
            areEventsAllowed.setTextColor(getResources().getColor(R.color.green));
        }
        if (place.getRegulations().isSmokingAllowed()) {
            smokingAllowed.setText(" ✓ ");
            smokingAllowed.setTextColor(getResources().getColor(R.color.green));
        }

        mapPin.setOnClickListener(v -> {
            String myLatitude = place.getLatitude().toString();
            String myLongitude = place.getLongitude().toString();
            String label = "my address";
            Uri urlAddress =
                    Uri.parse("geo:"+myLatitude+","+myLongitude+"?q="+myLatitude+","+myLongitude+
                            "(" + label + ")");
            Intent mapsIntent = new Intent(Intent.ACTION_VIEW, urlAddress);
            mapsIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapsIntent);
        });
    }

    private void setPlaceImages() {
        ImageApi imageApi = RetrofitUtils.createRequest(ImageApi.class, token);
        Call<List<Image>> call = imageApi.getImagesByPlaceId(placeId);
        call.enqueue(new Callback<List<Image>>() {
            @Override
            public void onResponse(Call<List<Image>> call, Response<List<Image>>
                    response) {
                if (response.isSuccessful()) {
                    handleSuccessfulPlaceImageResponse(response.body());

                } else {
                    handleUnsuccessfulResponse(response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Image>> call, Throwable t) {
                handleFailure(t.getMessage());
            }

        });
    }

    private void handleSuccessfulPlaceImageResponse(List<Image> placeImages) {
        List<Integer> imageIdsToFetchImagesFor = new ArrayList<>();
        Log.d("PlaceAdapter", "placeImages Size: " + placeImages.size());
        for (Image image : placeImages) {
            imageIdsToFetchImagesFor.add(image.getId());
            Log.d("PlaceAdapter", "Image ID: " + image.getId());

        }

        Log.d("PlaceAdapter", "imageIdsToFetchImagesFor Size: " + imageIdsToFetchImagesFor.size());

        // Clear the existing data (if exists) in fetchedImagesList
        fetchedImagesList.clear();

        // Fetch images for the current place
        ImageUtils.fetchMultiplePlacesImageWithRetry(placeId,
                imageIdsToFetchImagesFor, token, 5, new ImageUtils.ImageFetchCallback()
                {
                    @Override
                    public void onImageFetchSuccess(int imageId, Bitmap placeImageBitmap) {
                        if (placeImageBitmap != null) {
                            fetchedImagesList.add(placeImageBitmap);
                            Log.d("PlaceAdapter", "fetchedImagesList Size: " +
                                    fetchedImagesList.size());


                            // Check if all images have been fetched
                            if (fetchedImagesList.size() == placeImages.size()) {
                                // Create the adapter after fetching all images
                                ImageAdapter adapter = new ImageAdapter(ShowPlaceActivity.this,
                                        fetchedImagesList);

                                // Set the adapter to the ViewPager
                                viewPlacePics.setAdapter(adapter);

                                // Notify the adapter that data has changed
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onImageFetchError(int imageId) {
                        handleImageFetchError();
                    }
                });
    }

    private void handleUnsuccessfulResponse(int responseCode) {
        Log.e("PlaceAdapter", "Unsuccessful response: " + responseCode);
    }

    private void handleFailure(String errorMessage) {
        Log.e("PlaceAdapter", "Error occurred: " + errorMessage);
    }

    private void handleImageFetchError() {
        //Toast.makeText(ShowPlaceActivity.this, "Error fetching place image", Toast.LENGTH_SHORT).show();
    }
}
