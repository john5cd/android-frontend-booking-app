package com.example.cameinw.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.cameinw.R;
import com.example.cameinw.Response.NoContentResponse;
import com.example.cameinw.api.FacilityApi;
import com.example.cameinw.api.PlaceApi;
import com.example.cameinw.api.RegulationApi;
import com.example.cameinw.enums.PaymentMethod;
import com.example.cameinw.model.Facilities;
import com.example.cameinw.model.Place;
import com.example.cameinw.model.Regulations;
import com.example.cameinw.model.User;
import com.example.cameinw.util.MenuUtils;
import com.example.cameinw.util.RetrofitUtils;
import com.example.cameinw.util.UserInfoUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateSinglePlaceActivity extends AppCompatActivity {

    private RadioGroup[] radioGroups;
    private RadioButton[] radioButtons;
    private TextInputEditText[] textInputs;
    private MaterialButton updateButton;
    private Integer userId, placeId, facilityId, regulationId;
    private User user;
    private String userToken;
    private PlaceApi placeApi;
    private RegulationApi regulationApi;
    private FacilityApi facilityApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_single_place);

        initializeUIComponents();
        initializeComponents();
    }

    private void initializeComponents() {
        MenuUtils.setMenus("Update Place", UpdateSinglePlaceActivity.this);

        userToken = UserInfoUtils.getUserToken(this);
        userId = UserInfoUtils.getUserId(this);

        user = new User();
        user.setId(userId);

        Intent intent = getIntent();
        placeId = intent.getIntExtra("id", 0);

        if (placeId != null) {
            setupRetrofitApis();
            retrievePlaceInfo();
        } else {
            Toast.makeText(this, "Unable to fetch place's details", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeUIComponents() {
        radioGroups = new RadioGroup[]{
                findViewById(R.id.parkingRadioGroup),
                findViewById(R.id.wifiRadioGroup),
                findViewById(R.id.breakfastRadioGroup),
                findViewById(R.id.balconyRadioGroup),
                findViewById(R.id.poolRadioGroup),
                findViewById(R.id.nonSmokingRadioGroup),
                findViewById(R.id.ageResRadioGroup),
                findViewById(R.id.petsRadioGroup),
                findViewById(R.id.eventsRadioGroup),
                findViewById(R.id.smokingAllowedRadioGroup),
                findViewById(R.id.paymentMethRadioGroup)
        };

        radioButtons = new RadioButton[]{
                findViewById(R.id.freeParking_yes),
                findViewById(R.id.freeParking_no),
                findViewById(R.id.freeWifi_yes),
                findViewById(R.id.freeWifi_no),
                findViewById(R.id.breakfast_yes),
                findViewById(R.id.breakfast_no),
                findViewById(R.id.balcony_yes),
                findViewById(R.id.balcony_no),
                findViewById(R.id.swpool_yes),
                findViewById(R.id.swpool_no),
                findViewById(R.id.nonsmoke_yes),
                findViewById(R.id.nonsmoke_no),
                findViewById(R.id.age_restriction_yes),
                findViewById(R.id.age_restriction_no),
                findViewById(R.id.pets_yes),
                findViewById(R.id.pets_no),
                findViewById(R.id.events_yes),
                findViewById(R.id.events_no),
                findViewById(R.id.smoke_yes),
                findViewById(R.id.smoke_no),
                findViewById(R.id.pay_cash),
                findViewById(R.id.pay_card),
                findViewById(R.id.pay_both)
        };

        textInputs = new TextInputEditText[]{
                findViewById(R.id.nameShowPlace),
                findViewById(R.id.costShowPlace),
                findViewById(R.id.descriptionShowPlace),
                findViewById(R.id.getArrivalTime),
                findViewById(R.id.getDepartureTime),
                findViewById(R.id.getcancelPolicy),
                findViewById(R.id.getQuietHours)
        };

        for (TextInputEditText input : textInputs) {
            input.addTextChangedListener(updatePlaceTextWatcher);
        }

        updateButton = findViewById(R.id.updateButton);
//        updateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                updatePlaceInfo();
//                updateRegulations();
//                updateFacilities();
//            }
//        });
    }

    private void setupRetrofitApis() {
        placeApi = RetrofitUtils.createRequest(PlaceApi.class, userToken);
        regulationApi = RetrofitUtils.createRequest(RegulationApi.class, userToken);
        facilityApi = RetrofitUtils.createRequest(FacilityApi.class, userToken);
    }

    private void retrievePlaceInfo() {
        placeApi.getPlace(placeId)
                .enqueue(new Callback<Place>() {
                    @Override
                    public void onResponse(Call<Place> call, Response<Place> response) {
                        if (response.isSuccessful()) {

                            Place place = response.body();
                            facilityId = place.getFacilities().getId();
                            regulationId = place.getRegulations().getId();
                            populatePlace(place);
                            populateFacility(place);
                            populateRegulations(place);
                            getClickedId();
                            savePreferences();
                        } else {
                            handleUnsuccessfulCall(response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Place> call, Throwable t) {
                        handleFailure(t);
                    }
                });
    }

    private void handleFailure(Throwable t) {
        Log.e("UpdateSinglePlace", "Error: " + t.getMessage());
    }

    private void handleUnsuccessfulCall(int code) {
        Log.e("UpdateSinglePlace", "Error code: " + String.valueOf(code));
    }

    private void populatePlace(Place place) {
        textInputs[0].setHint(String.valueOf(place.getName()));
        textInputs[1].setHint(String.valueOf(place.getCost()));
        textInputs[2].setHint(String.valueOf(place.getDescription()));
    }

    private void populateFacility(Place place) {
        Facilities facilities = place.getFacilities();

        if (facilities.getHasFreeParking()) {
            radioButtons[0].setChecked(true);
            radioButtons[1].setChecked(false);
        } else {
            radioButtons[0].setChecked(false);
            radioButtons[1].setChecked(true);
        }

        if (facilities.getHasFreeWiFi()) {
            radioButtons[2].setChecked(true);
            radioButtons[3].setChecked(false);
        } else {
            radioButtons[2].setChecked(false);
            radioButtons[3].setChecked(true);
        }

        if (facilities.getHasbreakfast()) {
            radioButtons[4].setChecked(true);
            radioButtons[5].setChecked(false);
        } else {
            radioButtons[4].setChecked(false);
            radioButtons[5].setChecked(true);
        }

        if (facilities.getHasbalcony()) {
            radioButtons[6].setChecked(true);
            radioButtons[7].setChecked(false);
        } else {
            radioButtons[6].setChecked(false);
            radioButtons[7].setChecked(true);
        }

        if (facilities.getHasSwimmingPool()) {
            radioButtons[8].setChecked(true);
            radioButtons[9].setChecked(false);
        } else {
            radioButtons[8].setChecked(false);
            radioButtons[9].setChecked(true);
        }

        if (facilities.getNonSmoking()) {
            radioButtons[10].setChecked(true);
            radioButtons[11].setChecked(false);
        } else {
            radioButtons[10].setChecked(false);
            radioButtons[11].setChecked(true);
        }
    }

    private void populateRegulations(Place place) {
        Regulations regulations = place.getRegulations();

        if (regulations.isAgeRestriction()) {
            radioButtons[12].setChecked(true);
            radioButtons[13].setChecked(false);
        } else {
            radioButtons[12].setChecked(false);
            radioButtons[13].setChecked(true);
        }

        if (regulations.isArePetsAllowed()) {
            radioButtons[14].setChecked(true);
            radioButtons[15].setChecked(false);
        } else {
            radioButtons[14].setChecked(false);
            radioButtons[15].setChecked(true);
        }

        if (regulations.isAreEventsAllowed()) {
            radioButtons[16].setChecked(true);
            radioButtons[17].setChecked(false);
        } else {
            radioButtons[16].setChecked(false);
            radioButtons[17].setChecked(true);
        }

        if (regulations.isSmokingAllowed()) {
            radioButtons[18].setChecked(true);
            radioButtons[19].setChecked(false);
        } else {
            radioButtons[18].setChecked(false);
            radioButtons[19].setChecked(true);
        }

        textInputs[3].setHint(regulations.getArrivalTime());
        textInputs[4].setHint(regulations.getDepartureTime());
        textInputs[5].setHint(regulations.getCancellationPolicy());
        textInputs[6].setHint(regulations.getQuietHours());

        if (regulations.getPaymentMethod().equals(PaymentMethod.CASH_ONLY)) {
            radioButtons[20].setChecked(true);
            radioButtons[21].setChecked(false);
            radioButtons[22].setChecked(false);
        } else if (place.getRegulations().getPaymentMethod().equals(PaymentMethod.CARD_ONLY)) {
            radioButtons[20].setChecked(false);
            radioButtons[21].setChecked(true);
            radioButtons[22].setChecked(false);
        } else {
            radioButtons[20].setChecked(false);
            radioButtons[21].setChecked(false);
            radioButtons[22].setChecked(true);
        }
    }

    private void getClickedId() {
        radioGroups[0].getCheckedRadioButtonId();
        radioGroups[1].getCheckedRadioButtonId();
        radioGroups[2].getCheckedRadioButtonId();
        radioGroups[3].getCheckedRadioButtonId();
        radioGroups[4].getCheckedRadioButtonId();
        radioGroups[5].getCheckedRadioButtonId();
        radioGroups[6].getCheckedRadioButtonId();
        radioGroups[7].getCheckedRadioButtonId();
        radioGroups[8].getCheckedRadioButtonId();
        radioGroups[9].getCheckedRadioButtonId();
        radioGroups[10].getCheckedRadioButtonId();
    }

    private void savePreferences() {
        updateButton.setOnClickListener(v -> {
            updatePlaceInfo();
        });
    }

    private TextWatcher updatePlaceTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String nameInput = textInputs[0].getText().toString().trim();
            String costInput = textInputs[1].getText().toString().trim();
            String descriptionInput = textInputs[2].getText().toString().trim();
            String arrivalInput = textInputs[3].getText().toString().trim();
            String departureInput = textInputs[4].getText().toString().trim();
            String cancelPolicyInput = textInputs[5].getText().toString().trim();
            String quietHoursInput = textInputs[6].getText().toString().trim();

            updateButton.setEnabled(!quietHoursInput.isEmpty() &&
                    !cancelPolicyInput.isEmpty() &&
                    !departureInput.isEmpty() &&
                    !arrivalInput.isEmpty() &&
                    !descriptionInput.isEmpty() &&
                    !costInput.isEmpty() &&
                    !nameInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    private void updatePlaceInfo() {
        String updatedName = textInputs[0].getText().toString().trim();
        String costString = textInputs[1].getText().toString().trim();
        Integer updatedCost = Integer.parseInt(costString);

        String updatedDescription = textInputs[2].getText().toString().trim();

        Place updatedPlace = new Place();
        updatedPlace.setId(placeId);
        updatedPlace.setName(updatedName);
        updatedPlace.setCost(updatedCost);
        updatedPlace.setDescription(updatedDescription);
        updatedPlace.setUser(user);

        Call<NoContentResponse> updatePlaceCall = placeApi.updatePlace(placeId, updatedPlace);
        updatePlaceCall.enqueue(new Callback<NoContentResponse>() {
            @Override
            public void onResponse(Call<NoContentResponse> call, Response<NoContentResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("UpdateSinglePlace", "updatePlaceCall: onResponse - Success");
                    updateFacilities();
                } else {
                    Log.e("UpdateSinglePlace", "updatePlaceCall: onResponse - Error code: " + response.code());
                    handleUnsuccessfulCall(response.code());
                }
            }

            @Override
            public void onFailure(Call<NoContentResponse> call, Throwable t) {
                Log.e("UpdateSinglePlace", "updatePlaceCall: onFailure - Error: " + t.getMessage());
                handleFailure(t);
                Log.e("UpdateSinglePlace", "Request failed: " + t.getMessage());
            }
        });

    }

    private void updateFacilities() {

        Facilities updatedFacilities = new Facilities();
        updatedFacilities.setUser(user);

        if (radioGroups[0].getCheckedRadioButtonId()==R.id.freeParking_yes) {
            updatedFacilities.setHasFreeParking(true);
        } else {
            updatedFacilities.setHasFreeParking(false);
        }

        if (radioGroups[1].getCheckedRadioButtonId()==R.id.freeWifi_yes) {
            updatedFacilities.setHasFreeWiFi(true);
        } else {
            updatedFacilities.setHasFreeWiFi(false);
        }

        if (radioGroups[2].getCheckedRadioButtonId()==R.id.breakfast_yes) {
            updatedFacilities.setHasbreakfast(true);
        } else {
            updatedFacilities.setHasbreakfast(false);
        }

        if (radioGroups[3].getCheckedRadioButtonId()==R.id.balcony_yes) {
            updatedFacilities.setHasbalcony(true);
        } else {
            updatedFacilities.setHasbalcony(false);
        }

        if (radioGroups[4].getCheckedRadioButtonId()==R.id.swpool_yes) {
            updatedFacilities.setHasSwimmingPool(true);
        } else {
            updatedFacilities.setHasSwimmingPool(false);
        }

        if (radioGroups[5].getCheckedRadioButtonId()==R.id.nonsmoke_yes) {
            updatedFacilities.setNonSmoking(true);
        } else {
            updatedFacilities.setNonSmoking(false);
        }

        Log.d("UpdateSinglePlace", "Updated Facilities:");
        Log.d("UpdateSinglePlace", "User: " + updatedFacilities.getUser());
        Log.d("UpdateSinglePlace", "Has Free Parking: " + updatedFacilities.getHasFreeParking());
        Log.d("UpdateSinglePlace", "Has Free Wifi: " + updatedFacilities.getHasFreeWiFi());
        Log.d("UpdateSinglePlace", "Has Breakfast: " + updatedFacilities.getHasbreakfast());
        Log.d("UpdateSinglePlace", "Has Balcony: " + updatedFacilities.getHasbalcony());
        Log.d("UpdateSinglePlace", "Has Swimming Pool: " + updatedFacilities.getHasSwimmingPool());
        Log.d("UpdateSinglePlace", "Is Non Smoking: " + updatedFacilities.getNonSmoking());

        Call<NoContentResponse> updateFacilitiesCall = facilityApi.updateFacility(placeId, facilityId, updatedFacilities);
        updateFacilitiesCall.enqueue(new Callback<NoContentResponse>() {
            @Override
            public void onResponse(Call<NoContentResponse> call, Response<NoContentResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("UpdateSinglePlace", "updateFacilitiesCall: onResponse - Success");
                    updateRegulations();
                } else {
                    Log.e("UpdateSinglePlace", "updateFacilitiesCall: onResponse - Error code: " + response.code());
                    handleUnsuccessfulCall(response.code());
                }
            }

            @Override
            public void onFailure(Call<NoContentResponse> call, Throwable t) {
                Log.e("UpdateSinglePlace", "updateFacilitiesCall: onFailure - Error: " + t.getMessage());
                handleFailure(t);
            }
        });
    }

    private void updateRegulations() {

        Regulations updatedRegulations = new Regulations();
        updatedRegulations.setUser(user);

        if (radioGroups[6].getCheckedRadioButtonId()==R.id.age_restriction_yes) {
            updatedRegulations.setAgeRestriction(true);
        } else {
            updatedRegulations.setAgeRestriction(false);
        }

        if (radioGroups[7].getCheckedRadioButtonId()==R.id.pets_yes) {
            updatedRegulations.setArePetsAllowed(true);
        } else {
            updatedRegulations.setArePetsAllowed(false);
        }

        if (radioGroups[8].getCheckedRadioButtonId()==R.id.events_yes) {
            updatedRegulations.setAreEventsAllowed(true);
        } else {
            updatedRegulations.setAreEventsAllowed(false);
        }

        if (radioGroups[9].getCheckedRadioButtonId()==R.id.smoke_yes) {
            updatedRegulations.setSmokingAllowed(true);
        } else {
            updatedRegulations.setSmokingAllowed(false);
        }

        String updatedArrivalTime = textInputs[3].getText().toString().trim();
        String updatedDepartureTime = textInputs[4].getText().toString().trim();
        String updatedCancellationPolicy = textInputs[5].getText().toString().trim();
        String updatedQuietHours = textInputs[6].getText().toString().trim();

        updatedRegulations.setArrivalTime(updatedArrivalTime);
        updatedRegulations.setDepartureTime(updatedDepartureTime);
        updatedRegulations.setCancellationPolicy(updatedCancellationPolicy);
        updatedRegulations.setQuietHours(updatedQuietHours);

        if (radioGroups[10].getCheckedRadioButtonId()==R.id.pay_cash) {
            updatedRegulations.setPaymentMethod(PaymentMethod.CASH_ONLY);
        } else if (radioGroups[10].getCheckedRadioButtonId()==R.id.pay_card) {
            updatedRegulations.setPaymentMethod(PaymentMethod.CARD_ONLY);
        } else {
            updatedRegulations.setPaymentMethod(PaymentMethod.CASH_AND_CARD);
        }

        Log.d("UpdateSinglePlace", "Updated Regulations:");
        Log.d("UpdateSinglePlace", "User: " + updatedRegulations.getUser());
        Log.d("UpdateSinglePlace", "Arrival Time: " + updatedRegulations.getArrivalTime());
        Log.d("UpdateSinglePlace", "Departure time: " + updatedRegulations.getDepartureTime());
        Log.d("UpdateSinglePlace", "Cancellation Policy: " + updatedRegulations.getCancellationPolicy());
        Log.d("UpdateSinglePlace", "Quiet Hours: " + updatedRegulations.getQuietHours());
        Log.d("UpdateSinglePlace", "Has Age Restriction: " + updatedRegulations.isAgeRestriction());
        Log.d("UpdateSinglePlace", "Are Events Allowed: " + updatedRegulations.isAreEventsAllowed());
        Log.d("UpdateSinglePlace", "Are Pets Allowed: " + updatedRegulations.isArePetsAllowed());
        Log.d("UpdateSinglePlace", "Is smoking Allowed: " + updatedRegulations.isSmokingAllowed());

        Call<NoContentResponse> updateRegulationsCall = regulationApi.updateRegulation(placeId, regulationId, updatedRegulations);
        updateRegulationsCall.enqueue(new Callback<NoContentResponse>() {
            @Override
            public void onResponse(Call<NoContentResponse> call, Response<NoContentResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("UpdateSinglePlace", "updateRegulationsCall: onResponse - Success");
                    Toast.makeText(UpdateSinglePlaceActivity.this, "Updated successfully!", Toast.LENGTH_SHORT).show();
                    Intent updateIntent = new Intent(UpdateSinglePlaceActivity.this, ViewOwnerPlacesActivity.class);
                    startActivity(updateIntent);
                } else {
                    Log.e("UpdateSinglePlace", "updateRegulationsCall: onResponse - Error code: " + response.code());
                    handleUnsuccessfulCall(response.code());
                }
            }

            @Override
            public void onFailure(Call<NoContentResponse> call, Throwable t) {
                Log.e("UpdateSinglePlace", "updateRegulationsCall: onFailure - Error: " + t.getMessage());
                handleFailure(t);
            }
        });
    }
}