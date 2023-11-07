package com.example.cameinw.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.cameinw.R;
import com.example.cameinw.Response.NoContentResponse;
import com.example.cameinw.api.UserApi;
import com.example.cameinw.service.RetrofitService;
import com.example.cameinw.util.MenuUtils;
import com.example.cameinw.util.RetrofitUtils;
import com.example.cameinw.util.UserInfoUtils;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

    private MaterialButton configureButton;
    private TextView noSettings, configure;
    private String token;
    private Integer  userId;
    private UserApi userApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeComponents();
        getRole();
    }

    private void initializeComponents() {
        MenuUtils.setMenus("Settings", SettingsActivity.this);

        configureButton = findViewById(R.id.configureButton);
        configure = findViewById(R.id.configureText);
        noSettings = findViewById(R.id.noSettings);

        token = UserInfoUtils.getUserToken(SettingsActivity.this);
        userId = UserInfoUtils.getUserId(SettingsActivity.this);

        userApi = RetrofitUtils.createRequest(UserApi.class, token);
    }

    private void getRole() {
        userApi.getUserRole(userId)
                .enqueue(new Callback<NoContentResponse>() {
                    @Override
                    public void onResponse(Call<NoContentResponse> call, Response<NoContentResponse> response) {
                        if (((Integer) response.code()).equals(200)){
                            Log.e("Settings", response.body().getMessage());
                            setComponent(response.body().getMessage());
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

    private void setComponent(String role) {
        if (role.equals("USER")) {
            configureButton.setVisibility(View.INVISIBLE);
            configure.setVisibility(View.INVISIBLE);
            noSettings.setVisibility(View.VISIBLE);
        } else {
            configureButton.setOnClickListener(v -> {
                Intent newIntent = new Intent(SettingsActivity.this, LocationActivity.class);
                startActivity(newIntent);
            });
        }
    }
}