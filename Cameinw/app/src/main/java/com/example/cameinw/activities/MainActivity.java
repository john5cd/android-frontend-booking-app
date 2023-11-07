package com.example.cameinw.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.cameinw.R;
import com.example.cameinw.Response.RegisterResponse;
import com.example.cameinw.api.UserApi;
import com.example.cameinw.util.RetrofitUtils;
import com.example.cameinw.util.UserInfoUtils;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private MaterialButton signUpButton, signInButton;
    private String userToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Locale.setDefault(Locale.ENGLISH);

        checkIfUserIsLoggedIn();
        initializeComponents();
        signInUpButtons();
    }

    private void initializeComponents() {
        signUpButton = findViewById(R.id.signUpButton);
        signInButton = findViewById(R.id.signInButton);
    }

    private void signInUpButtons() {
        signUpButton.setOnClickListener(view -> {
            Intent signUpIntent = new Intent(this, SignUpActivity.class);
            startActivity(signUpIntent);
        });

        signInButton.setOnClickListener(view -> {
            Intent signInIntent = new Intent(this, SignInActivity.class);
            startActivity(signInIntent);
        });
    }

    private void checkIfUserIsLoggedIn() {
        userToken = UserInfoUtils.getUserToken(MainActivity.this);

        if (!userToken.equals("")) {
            UserApi userApi = RetrofitUtils.createRequest(UserApi.class, userToken);

            userApi.isTokenValid()
                    .enqueue(new Callback<RegisterResponse>() {
                        @Override
                        public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                            Integer code = response.code();
                            if (code.equals(200)) {
                                userIsValid();
                            } else if (code.equals(403)) {
                                userNotValid();
                            }
                        }

                        @Override
                        public void onFailure(Call<RegisterResponse> call, Throwable t) {
                            handleFailure(t);
                        }
                    });
        }
    }

    private void userIsValid() {
        Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, "user is valid");
        Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(searchIntent);
    }

    private void userNotValid() {
        Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, "expired token");
    }

    private void handleFailure(Throwable t) {
        Logger.getLogger(SearchActivity.class.getName()).log(Level.SEVERE, "Error occurred", t);
    }
}