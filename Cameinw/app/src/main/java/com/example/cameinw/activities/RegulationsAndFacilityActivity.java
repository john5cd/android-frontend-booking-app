package com.example.cameinw.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.RadioGroup;

import com.example.cameinw.R;
import com.example.cameinw.Response.NoContentResponse;
import com.example.cameinw.projections.PlaceProjection;
import com.example.cameinw.api.FacilityApi;
import com.example.cameinw.api.RegulationApi;
import com.example.cameinw.api.UserApi;
import com.example.cameinw.enums.PaymentMethod;
import com.example.cameinw.model.Facilities;
import com.example.cameinw.model.Regulations;
import com.example.cameinw.model.User;
import com.example.cameinw.util.MenuUtils;
import com.example.cameinw.util.RetrofitUtils;
import com.example.cameinw.util.UserInfoUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegulationsAndFacilityActivity extends AppCompatActivity {

    private RadioGroup radioGroup1, radioGroup2, radioGroup3, radioGroup4 , radioGroup5, radioGroup6, radioGroup7, radioGroup8, radioGroup9, radioGroup10, radioGroup11;
    private TextInputEditText arrival, departure, cancelPolicy, quietHours;
    private MaterialButton saveButton;
    private String token;
    private Integer placeId, userId;
    private UserApi userApi;
    private User user;
    private Regulations regulations;
    private Facilities facilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regulations_and_facility);

        initializeComponents();
        getClickedId();
        saveFacilityAndRegulations();
    }

    private void initializeComponents() {
        MenuUtils.setTopBar("Facility & Regulations", RegulationsAndFacilityActivity.this);

        token = UserInfoUtils.getUserToken(RegulationsAndFacilityActivity.this);
        userId = UserInfoUtils.getUserId(RegulationsAndFacilityActivity.this);

        saveButton = findViewById(R.id.saveFacReg);

        radioGroup1 = findViewById(R.id.radioGroup);
        radioGroup2 = findViewById(R.id.radioGroup2);
        radioGroup3 = findViewById(R.id.radioGroup3);
        radioGroup4 = findViewById(R.id.radioGroup4);
        radioGroup5 = findViewById(R.id.radioGroup5);
        radioGroup6 = findViewById(R.id.radioGroup6);
        radioGroup7 = findViewById(R.id.radioGroup7);
        radioGroup8 = findViewById(R.id.radioGroup8);
        radioGroup9 = findViewById(R.id.radioGroup9);
        radioGroup10 = findViewById(R.id.radioGroup10);
        radioGroup11 = findViewById(R.id.radioGroup11);

        arrival = findViewById(R.id.getArrivalTime);
        departure = findViewById(R.id.getDepartureTime);
        cancelPolicy = findViewById(R.id.getcancelPolicy);
        quietHours = findViewById(R.id.getQuietHours);

        arrival.addTextChangedListener(loginTextWatcher);
        departure.addTextChangedListener(loginTextWatcher);
        cancelPolicy.addTextChangedListener(loginTextWatcher);
        quietHours.addTextChangedListener(loginTextWatcher);
    }

    private void getClickedId() {
        radioGroup1.getCheckedRadioButtonId();
        radioGroup2.getCheckedRadioButtonId();
        radioGroup3.getCheckedRadioButtonId();
        radioGroup4.getCheckedRadioButtonId();
        radioGroup5.getCheckedRadioButtonId();
        radioGroup6.getCheckedRadioButtonId();
        radioGroup7.getCheckedRadioButtonId();
        radioGroup8.getCheckedRadioButtonId();
        radioGroup9.getCheckedRadioButtonId();
        radioGroup10.getCheckedRadioButtonId();
        radioGroup11.getCheckedRadioButtonId();
    }

    private void saveFacilityAndRegulations() {
        saveButton.setOnClickListener(v -> {
            user = new User();
            user.setId(userId);

            saveFacility();
            saveRegulations();

            userApi = RetrofitUtils.createRequest(UserApi.class, token);

            userApi.getPlaceByUserId(userId)
                    .enqueue(new Callback<List<PlaceProjection>>() {
                        @Override
                        public void onResponse(Call<List<PlaceProjection>> call, Response<List<PlaceProjection>> response) {
                            if (((Integer) response.code()).equals(200)) {
                                handleSuccessfulPlaceByUserId(response);
                            } else {
                                handleUnsuccessfulPlaceByUserId(response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<List<PlaceProjection>> call, Throwable t) {
                            handleFailure(t, "place id");
                        }
                    });
        });
    }

    private void saveRegulations() {
        regulations = new Regulations();

        regulations.setUser(user);

        if (radioGroup7.getCheckedRadioButtonId()==R.id.age_restriction_yes) {
            regulations.setAgeRestriction(true);
        } else {
            regulations.setAgeRestriction(false);
        }

        if (radioGroup8.getCheckedRadioButtonId()==R.id.pets_yes) {
            regulations.setArePetsAllowed(true);
        } else {
            regulations.setArePetsAllowed(false);
        }

        if (radioGroup9.getCheckedRadioButtonId()==R.id.events_yes) {
            regulations.setAreEventsAllowed(true);
        } else {
            regulations.setAreEventsAllowed(false);
        }

        if (radioGroup10.getCheckedRadioButtonId()==R.id.smoke_yes) {
            regulations.setSmokingAllowed(true);
        } else {
            regulations.setSmokingAllowed(false);
        }

        if (radioGroup11.getCheckedRadioButtonId()==R.id.pay_cash) {
            regulations.setPaymentMethod(PaymentMethod.CASH_ONLY);
        } else if (radioGroup11.getCheckedRadioButtonId()==R.id.pay_card) {
            regulations.setPaymentMethod(PaymentMethod.CARD_ONLY);
        } else {
            regulations.setPaymentMethod(PaymentMethod.CASH_AND_CARD);
        }

        regulations.setArrivalTime(arrival.getText().toString().trim());
        regulations.setDepartureTime(departure.getText().toString().trim());
        regulations.setQuietHours(quietHours.getText().toString().trim());
        regulations.setCancellationPolicy(cancelPolicy.getText().toString().trim());
    }

    private void saveFacility() {
        facilities = new Facilities();

        facilities.setUser(user);

        if (radioGroup1.getCheckedRadioButtonId()==R.id.freeParking_yes) {
            facilities.setHasFreeParking(true);
        } else {
            facilities.setHasFreeParking(false);
        }

        if (radioGroup2.getCheckedRadioButtonId()==R.id.freeWifi_yes) {
            facilities.setHasFreeWiFi(true);
        } else {
            facilities.setHasFreeWiFi(false);
        }

        if (radioGroup3.getCheckedRadioButtonId()==R.id.breakfast_yes) {
            facilities.setHasbreakfast(true);
        } else {
            facilities.setHasbreakfast(false);
        }

        if (radioGroup4.getCheckedRadioButtonId()==R.id.balcony_yes) {
            facilities.setHasbalcony(true);
        } else {
            facilities.setHasbalcony(false);
        }

        if (radioGroup5.getCheckedRadioButtonId()==R.id.swpool_yes) {
            facilities.setHasSwimmingPool(true);
        } else {
            facilities.setHasSwimmingPool(false);
        }

        if (radioGroup6.getCheckedRadioButtonId()==R.id.nonsm_yes) {
            facilities.setNonSmoking(true);
        } else {
            facilities.setNonSmoking(false);
        }
    }

    private void handleUnsuccessfulPlaceByUserId(Integer code) {
        Logger.getLogger(RegulationsAndFacilityActivity.class.getName()).log(Level.SEVERE, String.valueOf(code));
    }

    private void handleSuccessfulPlaceByUserId(Response<List<PlaceProjection>> response) {
        placeId = response.body().get(response.body().size()-1).getId();
        FacilityApi facilityApi = RetrofitUtils.createRequest(FacilityApi.class, token);
        facilityApi.createFacility(placeId, facilities)
                .enqueue(new Callback<NoContentResponse>() {
                    @Override
                    public void onResponse(Call<NoContentResponse> call, Response<NoContentResponse> response) {
                        if (((Integer) response.code()).equals(201)) {
                            handleSuccessfulFacilityCreation();
                        } else {
                            handleUnsuccessfulFacilityCreation(response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<NoContentResponse> call, Throwable t) {
                        handleFailure(t, "facilities");
                    }
                });
    }

    private void handleUnsuccessfulFacilityCreation(Integer code) {
        Logger.getLogger(RegulationsAndFacilityActivity.class.getName()).log(Level.SEVERE, String.valueOf(code)+ " reg");
    }

    private void handleSuccessfulFacilityCreation() {
        RegulationApi regulationApi = RetrofitUtils.createRequest(RegulationApi.class, token);
        regulationApi.createRegulation(placeId, regulations)
                .enqueue(new Callback<NoContentResponse>() {
                    @Override
                    public void onResponse(Call<NoContentResponse> call, Response<NoContentResponse> response) {
                        if (((Integer) response.code()).equals(201)) {
                            handleSuccessfulRegulationCreation();
                        } else {
                            handleUnsuccessfulRegulationCreation(response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<NoContentResponse> call, Throwable t) {
                        handleFailure(t, "regulations");
                    }
                });
    }

    private void handleFailure(Throwable t, String failedObject) {
        Logger.getLogger(RegulationsAndFacilityActivity.class.getName()).log(Level.SEVERE, "Error at "+failedObject, t);
    }

    private void handleUnsuccessfulRegulationCreation(Integer code) {
        Logger.getLogger(RegulationsAndFacilityActivity.class.getName()).log(Level.SEVERE, String.valueOf(code));
    }

    private void handleSuccessfulRegulationCreation() {
        Intent searchIntent = new Intent(RegulationsAndFacilityActivity.this, SearchActivity.class);
        startActivity(searchIntent);
    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String quietHoursInput = quietHours.getText().toString().trim();
            String cancelPolicyInput = cancelPolicy.getText().toString().trim();
            String departureInput = departure.getText().toString().trim();
            String arrivalInput = arrival.getText().toString().trim();

            saveButton.setEnabled(!quietHoursInput.isEmpty() &&
                    !cancelPolicyInput.isEmpty() &&
                    !departureInput.isEmpty() &&
                    !arrivalInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };
}