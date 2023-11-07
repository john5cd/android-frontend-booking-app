package com.example.cameinw.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.cameinw.R;
import com.example.cameinw.Response.NoContentResponse;
import com.example.cameinw.api.PlaceApi;
import com.example.cameinw.enums.PropertyType;
import com.example.cameinw.model.Place;
import com.example.cameinw.model.User;
import com.example.cameinw.util.MenuUtils;
import com.example.cameinw.util.RetrofitUtils;
import com.example.cameinw.util.UserInfoUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceActivity extends AppCompatActivity {

    private TextInputEditText inputPlaceName, inputDescription, inputGuests, inputBathrooms, inputBedrooms, inputCost, inputBeds, inputArea;
    private MaterialButton describePlaceButton;
    private Spinner propertySpinner;
    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        initializeComponents();
        handleButtonSave();
    }

    private void initializeComponents() {
        MenuUtils.setTopBar("Describe Your Place!", PlaceActivity.this);

        inputPlaceName = findViewById(R.id.nameOfPlace);
        inputDescription = findViewById(R.id.placeDescription);
        inputBathrooms = findViewById(R.id.bathrooms);
        inputBedrooms = findViewById(R.id.bedrooms);
        inputCost = findViewById(R.id.cost);
        inputGuests = findViewById(R.id.guests);
        inputBeds = findViewById(R.id.beds);
        inputArea = findViewById(R.id.area);
        describePlaceButton = findViewById(R.id.describePlaceButton);
        propertySpinner = findViewById(R.id.spinProperty);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.property,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        propertySpinner.setAdapter(adapter);

        inputPlaceName.addTextChangedListener(loginTextWatcher);
        inputDescription.addTextChangedListener(loginTextWatcher);
        inputBathrooms.addTextChangedListener(loginTextWatcher);
        inputBedrooms.addTextChangedListener(loginTextWatcher);
        inputCost.addTextChangedListener(loginTextWatcher);
        inputGuests.addTextChangedListener(loginTextWatcher);
        inputBeds.addTextChangedListener(loginTextWatcher);
    }

    private void handleButtonSave() {
        describePlaceButton.setOnClickListener(v -> {
            String token = UserInfoUtils.getUserToken(PlaceActivity.this);

            savePlace();

            PlaceApi placeApi = RetrofitUtils.createRequest(PlaceApi.class, token);
            placeApi.createPlace(place)
                    .enqueue(new Callback<NoContentResponse>() {
                        @Override
                        public void onResponse(Call<NoContentResponse> call, Response<NoContentResponse> response) {
                            Integer code = response.code();

                            if (code.equals(403)) {
                                handleUnsuccessfulCreation();
                            } else if (code.equals(201)) {
                                handleCreation();
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
        Toast.makeText(PlaceActivity.this, "Saving your place failed! Try again!", Toast.LENGTH_SHORT).show();
        Logger.getLogger(PlaceActivity.class.getName()).log(Level.SEVERE, "Error occurred", t);
    }

    private void handleCreation() {
        Toast.makeText(PlaceActivity.this, "Nice place!", Toast.LENGTH_SHORT).show();
        Intent picIntent = new Intent(PlaceActivity.this, UploadPlacePicsActivity.class);
        startActivity(picIntent);
    }

    private void handleUnsuccessfulCreation() {
        Toast.makeText(PlaceActivity.this, "There is something wrong with your input. Try again!", Toast.LENGTH_SHORT).show();
        Intent locationIntent = new Intent(PlaceActivity.this, LocationActivity.class);
        startActivity(locationIntent);
    }

    private void savePlace() {

        String name = String.valueOf(inputPlaceName.getText());
        String description = String.valueOf(inputDescription.getText());
        Integer bathrooms = Integer.valueOf(String.valueOf(inputBathrooms.getText()));
        Integer bedrooms = Integer.valueOf(String.valueOf(inputBedrooms.getText()));
        Integer beds = Integer.valueOf(String.valueOf(inputBeds.getText()));
        Integer cost = Integer.valueOf(String.valueOf(inputCost.getText()));
        Integer guests = Integer.valueOf(String.valueOf(inputGuests.getText()));
        Integer area = Integer.valueOf(String.valueOf(inputArea.getText()));

        place = new Place();

        place.setName(name);
        place.setDescription(description);
        place.setBathrooms(bathrooms);
        place.setBedrooms(bedrooms);
        place.setBeds(beds);
        place.setCost(cost);
        place.setGuests(guests);
        place.setArea(area);

        place.setAddress(getIntent().getStringExtra("address"));
        place.setCity(getIntent().getStringExtra("city"));
        place.setCountry(getIntent().getStringExtra("country"));
        place.setLatitude(getIntent().getDoubleExtra("lat", 0));
        place.setLongitude(getIntent().getDoubleExtra("lng", 0));

        Integer userId = UserInfoUtils.getUserId(PlaceActivity.this);
        User user = new User();
        user.setId(userId);
        place.setUser(user);

        String thePropertyType = propertySpinner.getSelectedItem().toString();

        if (thePropertyType.equals(PropertyType.HOTEL.toString())) {
            place.setPropertyType(PropertyType.HOTEL);
        } else if (thePropertyType.equals(PropertyType.APARTMENT.toString())) {
            place.setPropertyType(PropertyType.APARTMENT);
        } else if (thePropertyType.equals(PropertyType.CAMPSITE.toString())) {
            place.setPropertyType(PropertyType.CAMPSITE);
        } else if (thePropertyType.equals(PropertyType.RESORT.toString())) {
            place.setPropertyType(PropertyType.RESORT);
        } else if (thePropertyType.equals(PropertyType.VILLA.toString())) {
            place.setPropertyType(PropertyType.VILLA);
        }
    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String name = inputPlaceName.getText().toString().trim();
            String description = inputDescription.getText().toString().trim();
            String bathrooms = inputBathrooms.getText().toString().trim();
            String bedrooms = inputBedrooms.getText().toString().trim();
            String cost = inputCost.getText().toString().trim();
            String guests = inputGuests.getText().toString().trim();
            String beds = inputBeds.getText().toString().trim();
            String area = inputArea.getText().toString().trim();

            describePlaceButton.setEnabled(!name.isEmpty() &&
                    !description.isEmpty() &&
                    !bathrooms.isEmpty() &&
                    !bedrooms.isEmpty() &&
                    !cost.isEmpty() &&
                    !beds.isEmpty() &&
                    !area.isEmpty() &&
                    !guests.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };
}