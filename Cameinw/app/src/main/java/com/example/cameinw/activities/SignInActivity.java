package com.example.cameinw.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cameinw.R;
import com.example.cameinw.Response.RegisterResponse;
import com.example.cameinw.api.UserApi;
import com.example.cameinw.model.User;
import com.example.cameinw.service.RetrofitService;
import com.example.cameinw.util.MenuUtils;
import com.example.cameinw.util.UserInfoUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {
    private TextInputEditText inputEmail, inputPassword;
    private MaterialButton buttonSignIn;
    private  RegisterResponse registerResponse;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initializeComponents();
        signIn();
    }

    private void initializeComponents() {
        MenuUtils.setTopBar("Sign In", SignInActivity.this);

        inputEmail = findViewById(R.id.signInEmail);
        inputPassword = findViewById(R.id.signInPassword);
        buttonSignIn = findViewById(R.id.signIn);

        inputEmail.addTextChangedListener(loginTextWatcher);
        inputPassword.addTextChangedListener(loginTextWatcher);
    }

    private void signIn() {
        buttonSignIn.setOnClickListener(view -> {

            validateUser();

            RetrofitService retrofitService = new RetrofitService();
            retrofitService.setRetrofit();
            UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);

            userApi.signIn(user)
                    .enqueue(new Callback<RegisterResponse>() {
                        @Override
                        public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                            Integer code = response.code();

                            if (code.equals(403)) {
                                handleUnsuccessfulSignIn();
                            } else if (code.equals(200)) {
                                handleSuccessfulSignIn(response.body());
                            }
                        }

                        @Override
                        public void onFailure(Call<RegisterResponse> call, Throwable t) {
                            handleFailure(t);
                        }
                    });
        });
    }

    private void handleUnsuccessfulSignIn() {
        Toast.makeText(SignInActivity.this, "Wrong Credentials. Try again!", Toast.LENGTH_SHORT).show();
        clearData();
    }

    private void handleSuccessfulSignIn(RegisterResponse response) {
        Toast.makeText(SignInActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();

        registerResponse = response;
        saveData();

        Intent searchIntent = new Intent(SignInActivity.this, SearchActivity.class);
        startActivity(searchIntent);
    }

    private void handleFailure(Throwable t) {
        Toast.makeText(SignInActivity.this, "Sign in failed! Try again!", Toast.LENGTH_SHORT).show();
        Logger.getLogger(SignInActivity.class.getName()).log(Level.SEVERE, "Error occurred", t);
    }

    private void clearData() {
        UserInfoUtils.clearUserData(SignInActivity.this);
    }

    private void saveData() {
        UserInfoUtils.saveUserData(SignInActivity.this, registerResponse.getToken().toString(), registerResponse.getId());
    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            buttonSignIn.setEnabled(!email.isEmpty() && !password.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    private void validateUser() {
        user = new User();

        String email = String.valueOf(inputEmail.getText());
        String password = String.valueOf(inputPassword.getText());

        user.setEmail(email);
        user.setPassword(password);
    }
}