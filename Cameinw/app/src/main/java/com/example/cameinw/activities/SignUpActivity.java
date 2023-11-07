package com.example.cameinw.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.cameinw.R;
import com.example.cameinw.Response.RegisterResponse;
import com.example.cameinw.api.UserApi;
import com.example.cameinw.enums.Role;
import com.example.cameinw.model.User;
import com.example.cameinw.service.PatternService;
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

public class SignUpActivity extends AppCompatActivity  {

    private TextInputEditText inputUserName, inputEmail, inputFirstName, inputLastName, inputPhoneNumber, inputPassword, inputRepeatPassword;

    private MaterialButton buttonSignUp;
    private RegisterResponse registerResponse;
    private Spinner spinner;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initializeToolbar();
        initializeComponents();
        signUp();
    }

    private void initializeToolbar() {
        MenuUtils.setTopBar("Sign Up", SignUpActivity.this);
    }

    private void initializeComponents() {

        inputUserName = findViewById(R.id.userName);
        inputEmail = findViewById(R.id.email);
        inputFirstName = findViewById(R.id.firstName);
        inputLastName = findViewById(R.id.lastName);
        inputPhoneNumber = findViewById(R.id.phoneNumber);
        inputPassword = findViewById(R.id.password);
        inputRepeatPassword = findViewById(R.id.repeatPassword);
        buttonSignUp = findViewById(R.id.signUp2);
        spinner = findViewById(R.id.spinRole);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.role,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        inputUserName.addTextChangedListener(loginTextWatcher);
        inputEmail.addTextChangedListener(loginTextWatcher);
        inputFirstName.addTextChangedListener(loginTextWatcher);
        inputLastName.addTextChangedListener(loginTextWatcher);
        inputPhoneNumber.addTextChangedListener(loginTextWatcher);
        inputPassword.addTextChangedListener(loginTextWatcher);
        inputRepeatPassword.addTextChangedListener(loginTextWatcher);
    }

    private void signUp() {
        RetrofitService retrofitService = new RetrofitService();
        retrofitService.setRetrofit();
        UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);

        buttonSignUp.setOnClickListener(view -> {
            validateUser();

            userApi.signUp(user)
                    .enqueue(new Callback<RegisterResponse>() {
                        @Override
                        public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                            Integer code = response.code();

                            if (code.equals(403)) {
                                handleUnsuccessfulSignUp();
                            } else if (code.equals(409)) {
                                handleAlreadyExistingCredentials();
                            } else if (code.equals(200)) {
                                handleSuccessfulSignUp(response.body());
                            }
                        }

                        @Override
                        public void onFailure(Call<RegisterResponse> call, Throwable t) {
                            handleFailure(t);
                        }
                    });


        });
    }
    private void handleSuccessfulSignUp(RegisterResponse response) {
        Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
        registerResponse = response;
        saveData();

        Intent profilePicIntent = new Intent(SignUpActivity.this, UploadProfilePicActivity.class);
        profilePicIntent.putExtra("userRole", user.getRole().toString());
        startActivity(profilePicIntent);
    }

    private void handleAlreadyExistingCredentials() {
        Toast.makeText(SignUpActivity.this, "Sign up failed! try again with different credentials", Toast.LENGTH_SHORT).show();
    }

    private void handleUnsuccessfulSignUp() {
        Toast.makeText(SignUpActivity.this, "Access forbidden", Toast.LENGTH_SHORT).show();
    }

    private void handleFailure(Throwable t) {
        Toast.makeText(SignUpActivity.this, "Sign up failed! Try again later!", Toast.LENGTH_SHORT).show();
        Logger.getLogger(SignUpActivity.class.getName()).log(Level.SEVERE, "Error occurred", t);
    }

    private void saveData() {
        UserInfoUtils.saveUserData(SignUpActivity.this, registerResponse.getToken().toString(), registerResponse.getId());
    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String userName = inputUserName.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String firstName = inputFirstName.getText().toString().trim();
            String lastName = inputLastName.getText().toString().trim();
            String phoneNumber = inputPhoneNumber.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            String repeatPassword = inputRepeatPassword.getText().toString().trim();

            if (!PatternService.PASSWORD_PATTERN.matcher(password).matches() && !password.isEmpty()) {
                inputPassword.setError("Password too weak");
            }

            buttonSignUp.setEnabled(!userName.isEmpty() &&
                    !email.isEmpty() &&
                    !firstName.isEmpty() &&
                    !lastName.isEmpty() &&
                    !phoneNumber.isEmpty() &&
                    !password.isEmpty() &&
                    !repeatPassword.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    private void validateUser() {

        String userName = String.valueOf(inputUserName.getText());
        String email = String.valueOf(inputEmail.getText());
        String firstName = String.valueOf(inputFirstName.getText());
        String lastName = String.valueOf(inputLastName.getText());
        String phoneNumber = String.valueOf(inputPhoneNumber.getText());
        String password = String.valueOf(inputPassword.getText());
        String repeatPassword = String.valueOf(inputRepeatPassword.getText());

        user = new User();

        user.setTheUserName(userName.trim());
        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());

        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            user.setEmail(email.trim());
        } else {
            inputEmail.setError("Please enter a valid email address");
            changeColorWhenErrorOccurs(inputEmail, getResources().getColor(R.color.red, getTheme()));
        }

        if (PatternService.PASSWORD_PATTERN.matcher(password).matches() &&
                PatternService.PASSWORD_PATTERN.matcher(repeatPassword).matches() &&
                password.equals(repeatPassword)) {
            user.setPassword(password.trim());
        } else if (!PatternService.PASSWORD_PATTERN.matcher(password).matches()) {
            inputPassword.setError("Please enter a valid password");
            changeColorWhenErrorOccurs(inputPassword, getResources().getColor(R.color.red, getTheme()));
        } else if (!password.equals(repeatPassword)) {
            inputRepeatPassword.setError("Password does not match");
            changeColorWhenErrorOccurs(inputRepeatPassword, getResources().getColor(R.color.red, getTheme()));
        }

        if (PatternService.PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches()) {
            user.setPhoneNumber(phoneNumber.trim());
        } else {
            inputPhoneNumber.setError("Please enter a valid phone number");
            changeColorWhenErrorOccurs(inputPhoneNumber, getResources().getColor(R.color.red, getTheme()));
        }

        String spin = spinner.getSelectedItem().toString();
        if (spin.equals(Role.USER.toString())) {
            user.setRole(Role.USER);
        } else if (spin.equals(Role.OWNER.toString())) {
            user.setRole(Role.OWNER);
        }
    }

    private void changeColorWhenErrorOccurs(TextInputEditText textInputEditText, int color) {
        textInputEditText.setBackgroundTintList(ColorStateList.valueOf(color));
    }
}