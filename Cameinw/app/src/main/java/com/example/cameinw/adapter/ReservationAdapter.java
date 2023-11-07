package com.example.cameinw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cameinw.R;
import com.example.cameinw.Response.NoContentResponse;
import com.example.cameinw.Response.ReviewResponse;
import com.example.cameinw.api.ReservationApi;
import com.example.cameinw.api.ReviewApi;
import com.example.cameinw.enums.PropertyRating;
import com.example.cameinw.model.Place;
import com.example.cameinw.model.Reservation;
import com.example.cameinw.model.User;
import com.example.cameinw.util.RetrofitUtils;
import com.example.cameinw.util.UserInfoUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationHolder> {

    private List<Reservation> reservations;
    private Context context;
    private String userToken;
    private ReviewResponse review;
    private User user;
    private Integer clicked =0, placeId, userId;

    public ReservationAdapter(List<Reservation> reservations, Context context) {
        this.reservations = reservations;
        this.context = context;
        userToken = UserInfoUtils.getUserToken(context);
    }

    @NonNull
    @Override
    public ReservationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reserved_places, parent, false);
        return new ReservationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationHolder holder, int position) {
        Reservation reservation = reservations.get(position);

        userId = UserInfoUtils.getUserId(context);

        holder.checkIn.setText(reservation.getCheckIn());
        holder.checkOut.setText(reservation.getCheckOut());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.review,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.reviewStar.setAdapter(adapter);

        ReservationApi reservationApi = RetrofitUtils.createRequest(ReservationApi.class, userToken);
        reservationApi.getPlaceByReservationId(reservation.getId().toString())
                .enqueue(new Callback<Place>() {
                    @Override
                    public void onResponse(Call<Place> call, Response<Place> response) {
                        if (((Integer) response.code()).equals(200)) {
                            handleSuccessfulPlaceGrab(holder, response);
                        } else {
                            handleUnsuccessfulResponse(response.code(), "place id failed!");
                        }
                    }

                    @Override
                    public void onFailure(Call<Place> call, Throwable t) {
                        handleFailure(t);
                    }
                });


    }

    private void handleSuccessfulPlaceGrab(ReservationHolder holder, Response<Place> response) {
        placeId = response.body().getId();
        holder.address.setText(response.body().getAddress());
        holder.reviewButton.setOnClickListener(v -> {
            if (clicked % 2 == 0) {
                evenTimesClicked(holder, response);
            } else {
                oddTimesClicked(holder);
            }
        });
    }

    private void oddTimesClicked(ReservationHolder holder) {
        clicked += 1;
        holder.review.setVisibility(View.GONE);
        holder.saveReview.setVisibility(View.GONE);
        holder.reviewStar.setVisibility(View.GONE);
    }

    private void evenTimesClicked(ReservationHolder holder, Response<Place> response) {
        clicked += 1;
        holder.review.setVisibility(View.VISIBLE);
        holder.saveReview.setVisibility(View.VISIBLE);
        holder.reviewStar.setVisibility(View.VISIBLE);
        holder.saveReview.setOnClickListener(v1 -> {
            review = new ReviewResponse();
            user = new User();

            //όταν ο user γράφει το comment πρέπει να ελαχιστοποιεί το πληκτρολόγιο και όχι να πατάει το check κάτω δεξιά, αλλιώς βγάζει error
            review.setComment(holder.review.getText().toString());

            user.setId(userId);
            review.setUser(user);
            String spin = holder.reviewStar.getSelectedItem().toString();
            if (spin.equals(PropertyRating.ONE_STAR.toString())) {
                review.setRating(PropertyRating.ONE_STAR);
            } else if (spin.equals(PropertyRating.TWO_STARS.toString())) {
                review.setRating(PropertyRating.TWO_STARS);
            } else if (spin.equals(PropertyRating.THREE_STARS.toString())) {
                review.setRating(PropertyRating.THREE_STARS);
            } else if (spin.equals(PropertyRating.FOUR_STARS.toString())) {
                review.setRating(PropertyRating.FOUR_STARS);
            } else if (spin.equals(PropertyRating.FIVE_STARS.toString())) {
                review.setRating(PropertyRating.FIVE_STARS);
            }

            ReviewApi reviewApi = RetrofitUtils.createRequest(ReviewApi.class, userToken);
            reviewApi.createReview(placeId, review)
                    .enqueue(new Callback<NoContentResponse>() {
                        @Override
                        public void onResponse(Call<NoContentResponse> call, Response<NoContentResponse> response) {
                            Toast.makeText(context, review.getRating().toString(), Toast.LENGTH_SHORT).show();
                            if (((Integer)response.code()).equals(201)) {
                                handleCreatedReview(holder);
                            } else if (((Integer)response.code()).equals(403)) {
                                handleUnsuccessfulResponse(response.code(), "Review not created!");
                            } else if (((Integer)response.code()).equals(404)) {
                                handleUnsuccessfulResponse(response.code(), "Review not found!");
                            } else if (((Integer)response.code()).equals(400)) {
                                handleExistingReview(holder, response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<NoContentResponse> call, Throwable t) {
                            handleFailure(t);
                        }
                    });
        });
    }

    private void handleFailure(Throwable t) {
        Toast.makeText(context, "Review save failed!", Toast.LENGTH_SHORT).show();
    }

    private void handleExistingReview(ReservationHolder holder, int code) {
        handleUnsuccessfulResponse(code, "You have already reviewed this place!");
        holder.review.setVisibility(View.GONE);
        holder.saveReview.setVisibility(View.GONE);
        holder.reviewStar.setVisibility(View.GONE);
        holder.reviewButton.setEnabled(false);
    }

    private void handleUnsuccessfulResponse(int code, String message) {
        Toast.makeText(context, message+ " Code: "+code, Toast.LENGTH_SHORT).show();
    }

    private void handleCreatedReview(ReservationHolder holder) {
        Toast.makeText(context, "Review saved!", Toast.LENGTH_SHORT).show();
        holder.review.setVisibility(View.GONE);
        holder.saveReview.setVisibility(View.GONE);
        holder.reviewStar.setVisibility(View.GONE);
        holder.reviewButton.setEnabled(false);
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public static class ReservationHolder extends RecyclerView.ViewHolder {

        TextView checkIn, checkOut, address;
        TextInputEditText review;
        MaterialButton reviewButton, saveReview;
        Spinner reviewStar;
        public ReservationHolder(@NonNull View itemView) {
            super(itemView);
            checkIn = itemView.findViewById(R.id.checkIn);
            checkOut = itemView.findViewById(R.id.checkOut);
            address = itemView.findViewById(R.id.addressInput);
            reviewButton = itemView.findViewById(R.id.makeReview);
            saveReview = itemView.findViewById(R.id.saveReview);
            review = itemView.findViewById(R.id.leaveReview);
            reviewStar = itemView.findViewById(R.id.spinnerReview);
        }
    }
}
