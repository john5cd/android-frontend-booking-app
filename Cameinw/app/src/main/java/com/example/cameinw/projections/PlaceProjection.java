package com.example.cameinw.projections;

import com.example.cameinw.enums.PropertyType;

public class PlaceProjection {
    Integer id;
    String name;
    String country;
    String city;
    String address;
    Integer guests;
    Integer cost;
    Integer bathrooms;
    Integer bedrooms;
    String description;
    PropertyType type;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public Integer getGuests() {
        return guests;
    }

    public Integer getCost() {
        return cost;
    }

    public Integer getBathrooms() {
        return bathrooms;
    }

    public Integer getBedrooms() {
        return bedrooms;
    }

    public String getDescription() {
        return description;
    }

    public PropertyType getType() {
        return type;
    }
}
