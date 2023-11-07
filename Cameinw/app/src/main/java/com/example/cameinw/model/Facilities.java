package com.example.cameinw.model;

public class Facilities {

    private Integer id;
    private Boolean hasFreeParking;
    private Boolean hasFreeWiFi;
    private Boolean hasbreakfast;
    private Boolean hasbalcony;
    private Boolean hasSwimmingPool;
    private Boolean nonSmoking;

    private User user;

    //----GETTERS & SETTERS


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getHasFreeParking() {
        return hasFreeParking;
    }

    public void setHasFreeParking(Boolean hasFreeParking) {
        this.hasFreeParking = hasFreeParking;
    }

    public Boolean getHasFreeWiFi() {
        return hasFreeWiFi;
    }

    public void setHasFreeWiFi(Boolean hasFreeWiFi) {
        this.hasFreeWiFi = hasFreeWiFi;
    }

    public Boolean getHasbreakfast() {
        return hasbreakfast;
    }

    public void setHasbreakfast(Boolean hasbreakfast) {
        this.hasbreakfast = hasbreakfast;
    }

    public Boolean getHasbalcony() {
        return hasbalcony;
    }

    public void setHasbalcony(Boolean hasbalcony) {
        this.hasbalcony = hasbalcony;
    }

    public Boolean getHasSwimmingPool() {
        return hasSwimmingPool;
    }

    public void setHasSwimmingPool(Boolean hasSwimmingPool) {
        this.hasSwimmingPool = hasSwimmingPool;
    }

    public Boolean getNonSmoking() {
        return nonSmoking;
    }

    public void setNonSmoking(Boolean nonSmoking) {
        this.nonSmoking = nonSmoking;
    }
}
