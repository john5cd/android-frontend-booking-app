package com.example.cameinw.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.cameinw.R;
import com.example.cameinw.adapter.ReservationAdapter;
import com.example.cameinw.api.ReservationApi;
import com.example.cameinw.model.Reservation;
import com.example.cameinw.util.MenuUtils;
import com.example.cameinw.util.RetrofitUtils;
import com.example.cameinw.util.UserInfoUtils;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationActivity extends AppCompatActivity {
    private Integer userId;
    private ReservationAdapter reservationAdapter;
    private RecyclerView recyclerView;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        initializeComponents();
        getReservations();
    }

    private void initializeComponents() {
        MenuUtils.setMenus("Reservations", ReservationActivity.this);

        recyclerView = findViewById(R.id.reservation_recycler_view);
        userId = UserInfoUtils.getUserId(ReservationActivity.this);
        token = UserInfoUtils.getUserToken(ReservationActivity.this);
    }

    private void getReservations() {
        ReservationApi reservationApi = RetrofitUtils.createRequest(ReservationApi.class, token);
        reservationApi.getReservationsByUserId(userId)
                .enqueue(new Callback<List<Reservation>>() {
                    @Override
                    public void onResponse(Call<List<Reservation>> call, Response<List<Reservation>> response) {
                        if (((Integer) response.code()).equals(200)) {
                            Logger.getLogger(ReservationActivity.class.getName()).log(Level.SEVERE, response.body().get(0).getCheckIn());

                            setRecyclerView(response.body());
                        } else {
                            Logger.getLogger(ReservationActivity.class.getName()).log(Level.SEVERE, String.valueOf(response.code()));

                        }
                    }

                    @Override
                    public void onFailure(Call<List<Reservation>> call, Throwable t) {
                        Logger.getLogger(ReservationActivity.class.getName()).log(Level.SEVERE, "shiiiiiiiiiiiiit");
                    }
                });
    }
    private void setRecyclerView(List<Reservation> reservations) {
        reservationAdapter = new ReservationAdapter(reservations, ReservationActivity.this);
        recyclerView.setAdapter(reservationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ReservationActivity.this));
    }
}