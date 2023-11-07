package com.example.cameinw.model;

import com.example.cameinw.enums.PaymentMethod;

public class Regulations {

    private Integer id;
    private String arrivalTime;
    private String departureTime;
    private String cancellationPolicy;
    private PaymentMethod paymentMethod;
    private Boolean ageRestriction;
    private Boolean arePetsAllowed;
    private Boolean areEventsAllowed;
    private Boolean smokingAllowed;
    private String quietHours;

    private User user;

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

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getCancellationPolicy() {
        return cancellationPolicy;
    }

    public void setCancellationPolicy(String cancellationPolicy) {
        this.cancellationPolicy = cancellationPolicy;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public boolean isAgeRestriction() {
        return ageRestriction;
    }

    public void setAgeRestriction(boolean ageRestriction) {
        this.ageRestriction = ageRestriction;
    }

    public boolean isArePetsAllowed() {
        return arePetsAllowed;
    }

    public void setArePetsAllowed(boolean arePetsAllowed) {
        this.arePetsAllowed = arePetsAllowed;
    }

    public boolean isAreEventsAllowed() {
        return areEventsAllowed;
    }

    public void setAreEventsAllowed(boolean areEventsAllowed) {
        this.areEventsAllowed = areEventsAllowed;
    }

    public boolean isSmokingAllowed() {
        return smokingAllowed;
    }

    public void setSmokingAllowed(boolean smokingAllowed) {
        this.smokingAllowed = smokingAllowed;
    }

    public String getQuietHours() {
        return quietHours;
    }

    public void setQuietHours(String quietHours) {
        this.quietHours = quietHours;
    }
}
