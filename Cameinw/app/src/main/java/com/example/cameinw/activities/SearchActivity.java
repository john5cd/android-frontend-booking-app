package com.example.cameinw.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.cameinw.R;
import com.example.cameinw.util.MenuUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class SearchActivity extends AppCompatActivity {

    private TextInputEditText inputCity, inputCountry, inputGuests, inputDates;
    private String checkIn, checkOut, selectedDate, city, country, guests;
    private MaterialButton searchButton;
    private MaterialDatePicker<Pair<Long, Long>> materialDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initializeComponents();
        setDatePicker();
        getSelectedDates();
        search();
    }

    private void initializeComponents() {
        MenuUtils.setMenus("Search", SearchActivity.this);

        inputCity = findViewById(R.id.searchCity);
        inputCountry = findViewById(R.id.searchCountry);
        inputGuests = findViewById(R.id.searchguests);
        inputDates = findViewById(R.id.searchDate);
        searchButton = findViewById(R.id.searchButton);

        inputDates.setFocusable(false);
        inputDates.clearFocus();
        inputCity.addTextChangedListener(loginTextWatcher);
        inputCountry.addTextChangedListener(loginTextWatcher);
        inputDates.addTextChangedListener(loginTextWatcher);
        inputGuests.addTextChangedListener(loginTextWatcher);

        city = inputCity.getText().toString().trim();
        country = inputCountry.getText().toString().trim();
        guests = inputGuests.getText().toString().trim();
        selectedDate = inputDates.getText().toString();
    }

    private void getSelectedDates() {
        inputDates.setOnClickListener(v -> {
            materialDatePicker.show(getSupportFragmentManager(), "tag picker");
            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                inputDates.setText(materialDatePicker.getHeaderText().toString());

                try {
                    checkIn = convertTimeToDate(selection.first);
                    checkOut = convertTimeToDate(selection.second);
                } catch (ParseException e) {throw new RuntimeException(e);}
            });
        });
    }

    private void search() {
        searchButton.setOnClickListener(v -> {
            Integer guests2 = Integer.valueOf(guests);

            Intent searchResultIntent = new Intent(SearchActivity.this, SearchResultsActivity.class);
            searchResultIntent.putExtra("city", city);
            searchResultIntent.putExtra("country", country);
            searchResultIntent.putExtra("guests", guests2);
            searchResultIntent.putExtra("checkIn", checkIn);
            searchResultIntent.putExtra("checkOut", checkOut);
            startActivity(searchResultIntent);
        });
    }

    private void setDatePicker() {
        CalendarConstraints constraintsBuilder = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now()).build();

        materialDatePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setCalendarConstraints(constraintsBuilder)
                .setSelection(Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds())).build();
    }

    private String convertTimeToDate(Long time) throws ParseException {

        Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utc.setTimeInMillis(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        return format.format(utc.getTime());
    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            city = inputCity.getText().toString().trim();
            country = inputCountry.getText().toString().trim();
            guests = inputGuests.getText().toString().trim();
            selectedDate = inputDates.getText().toString();

            searchButton.setEnabled(!city.isEmpty() &&
                    !country.isEmpty() &&
                    !guests.isEmpty() &&
                    !selectedDate.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };
}