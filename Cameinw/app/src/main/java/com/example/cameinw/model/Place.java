package com.example.cameinw.model;

import com.example.cameinw.enums.PropertyRating;
import com.example.cameinw.enums.PropertyType;

import java.util.List;

public class Place {

    private Integer id;
    private String name;
    private String country;
    private String city;
    private String address;
    private Integer guests;
    private PropertyType propertyType;
    private String description;
    private Integer bedrooms;
    private Integer beds;
    private Integer area;
    private Integer bathrooms;
    private Integer cost;
    private Double latitude;
    private Double longitude;
    private User user;
    private List<Image> images;
    private List<Reservation> reservations;
    private List<Review> reviews;
    private String mainImage;
    private Regulations regulations;
    private Facilities facilities;

    //---------GETTERS & SETTERS---------\\

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getCountry() {return country;}

    public void setCountry(String country) {this.country = country;}

    public String getCity() {return city;}

    public void setCity(String city) {this.city = city;}
    public String getAddress() {return address;}
    public void setAddress(String address) {this.address = address;}
    public Integer getGuests() {return guests;}
    public void setGuests(Integer guests) {this.guests = guests;}
    public PropertyType getPropertyType() {return propertyType;}
    public void setPropertyType(PropertyType propertyType) {this.propertyType = propertyType;}
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
    public Integer getBedrooms() {return bedrooms;}
    public void setBedrooms(Integer bedrooms) {this.bedrooms = bedrooms;}
    public Integer getBathrooms() {return bathrooms;}
    public void setBathrooms(Integer bathrooms) {this.bathrooms = bathrooms;}
    public Integer getCost() {return cost;}
    public void setCost(Integer cost) {this.cost = cost;}
    public Double getLatitude() {return latitude;}
    public void setLatitude(Double latitude) {this.latitude = latitude;}
    public Double getLongitude() {return longitude;}
    public void setLongitude(Double longitude) {this.longitude = longitude;}
    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}
    public Integer getBeds() {return beds;}
    public void setBeds(Integer beds) {this.beds = beds;}
    public Integer getArea() {return area;}
    public void setArea(Integer area) {this.area = area;}

    public Integer getId() {return id;}

    public void setId(Integer id) {this.id = id;}
    public List<Image> getImages() {return images;}
    public void setImages(List<Image> images) {this.images = images;}
    public List<Reservation> getReservations() {return reservations;}
    public void setReservations(List<Reservation> reservations) {this.reservations = reservations;}
    public List<Review> getReviews() {return reviews;}
    public void setReviews(List<Review> reviews) {this.reviews = reviews;}
    public String getMainImage() {return mainImage;}
    public void setMainImage(String mainImage) {this.mainImage = mainImage;}
    public Regulations getRegulations() {return regulations;}
    public void setRegulations(Regulations regulations) {this.regulations = regulations;}
    public Facilities getFacilities() {return facilities;}
    public void setFacilities(Facilities facilities) {this.facilities = facilities;}
}
