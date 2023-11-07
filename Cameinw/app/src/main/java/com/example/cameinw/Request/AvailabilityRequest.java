package com.example.cameinw.Request;

public class AvailabilityRequest {
    private String checkIn;
    private String checkOut;
    private String country;
    private String city;
    private Double latitude;
    private Double longitude;
    private Integer guests;

    //----GETTERS & SETTERS----\\


    public String getCheckIn() {return checkIn;}
    public void setCheckIn(String checkIn) {this.checkIn = checkIn;}
    public String getCheckOut() {return checkOut;}
    public void setCheckOut(String checkOut) {this.checkOut = checkOut;}

    public String getCountry() {return country;}

    public void setCountry(String country) {this.country = country;}

    public String getCity() {return city;}

    public void setCity(String city) {this.city = city;}
    public Double getLatitude() {return latitude;}
    public void setLatitude(Double latitude) {this.latitude = latitude;}
    public Double getLongitude() {return longitude;}
    public void setLongitude(Double longitude) {this.longitude = longitude;}
    public Integer getGuests() {return guests;}
    public void setGuests(Integer guests) {this.guests = guests;}
}
