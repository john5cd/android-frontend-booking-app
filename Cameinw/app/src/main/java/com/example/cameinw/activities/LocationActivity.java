package com.example.cameinw.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import com.example.cameinw.R;
import com.example.cameinw.model.Place;
import com.example.cameinw.util.MenuUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocationActivity extends AppCompatActivity {

    TextInputEditText inputAddress, inputAddressNumber, inputPostalCode, inputMunicipality, inputCity, inputCountry;

    String address, city, country, finalAddress;

    Double lat, lng;
    MaterialButton locatePlaceButton, skipButton;

    Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        initializeComponents();
        locatePlace();
    }

    private void initializeComponents() {
        MenuUtils.setTopBar("Locate Your Place!", LocationActivity.this);

        inputAddress = findViewById(R.id.address);
        inputAddressNumber = findViewById(R.id.addressNumber);
        inputPostalCode = findViewById(R.id.postalCode);
        inputMunicipality = findViewById(R.id.municipality);
        inputCity = findViewById(R.id.city);
        inputCountry = findViewById(R.id.country);
        locatePlaceButton = findViewById(R.id.locatePlaceButton);
        skipButton = findViewById(R.id.skipButton);

        inputAddress.addTextChangedListener(loginTextWatcher);
        inputAddressNumber.addTextChangedListener(loginTextWatcher);
        inputPostalCode.addTextChangedListener(loginTextWatcher);
        inputMunicipality.addTextChangedListener(loginTextWatcher);
        inputCity.addTextChangedListener(loginTextWatcher);
        inputCountry.addTextChangedListener(loginTextWatcher);

        skipButton.setOnClickListener(v -> {
            Intent searchIntent = new Intent(LocationActivity.this, SearchActivity.class);
            startActivity(searchIntent);
        });
    }

    private void locatePlace() {
        locatePlaceButton.setOnClickListener(v -> {
            place = new Place();
            insertPlace();

            Intent placeIntent = new Intent(LocationActivity.this, PlaceActivity.class);
            placeIntent.putExtra("address", finalAddress);
            placeIntent.putExtra("city", city);
            placeIntent.putExtra("country", country);
            placeIntent.putExtra("lat", lat);
            placeIntent.putExtra("lng", lng);
            startActivity(placeIntent);
        });
    }

    private void insertPlace() {
        address = String.valueOf(inputAddress.getText());
        String addressNumber = String.valueOf(inputAddressNumber.getText());
        String postalCode = String.valueOf(inputPostalCode.getText());
        String municipality = String.valueOf(inputMunicipality.getText());

        city = String.valueOf(inputCity.getText());
        country = String.valueOf(inputCountry.getText());
        finalAddress = address + " " + addressNumber;

        String fullAddress = address + " " + addressNumber + ", " + postalCode + " " + municipality + " " + city + " " + country;
        getLatLong(fullAddress);
    }

    private void getLatLong(String fullAddress) {
        Geocoder geocoder = new Geocoder(LocationActivity.this);
        List<Address> addressList = new ArrayList<>();
        try {
            addressList = geocoder.getFromLocationName(fullAddress, 1);
        } catch (IOException e) {
            Logger.getLogger(LocationActivity.class.getName()).log(Level.SEVERE, e.toString());
        }

        if (addressList.size()>0) {
            Address myAddress = addressList.get(0);

            lat = myAddress.getLatitude();
            lng = myAddress.getLongitude();

            Logger.getLogger(LocationActivity.class.getName()).log(Level.SEVERE, myAddress.toString());
        } else {
            Toast.makeText(LocationActivity.this, "No such place!", Toast.LENGTH_SHORT).show();
        }
    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String address = inputAddress.getText().toString().trim();
            String addressNumber = inputAddressNumber.getText().toString().trim();
            String postalCode = inputPostalCode.getText().toString().trim();
            String municipality = inputMunicipality.getText().toString().trim();
            String city = inputCity.getText().toString().trim();
            String country = inputCountry.getText().toString().trim();

            locatePlaceButton.setEnabled(!address.isEmpty() &&
                    !addressNumber.isEmpty() &&
                    !postalCode.isEmpty() &&
                    !municipality.isEmpty() &&
                    !city.isEmpty() &&
                    !country.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };
}