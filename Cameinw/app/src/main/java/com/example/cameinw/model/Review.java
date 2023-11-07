package com.example.cameinw.model;

import com.example.cameinw.enums.PropertyRating;

public class Review {

    private Integer id;
    private PropertyRating rating;
    private String comment;
    private Place place;
    private User user;

    //----GETTERS & SETTERS----\\

    public Integer getId() {return id;}

    public void setId(Integer id) {this.id = id;}
    public PropertyRating getRating() {return rating;}
    public void setRating(PropertyRating rating) {this.rating = rating;}
    public String getComment() {return comment;}
    public void setComment(String comment) {this.comment = comment;}

    public Place getPlace() {return place;}

    public void setPlace(Place place) {this.place = place;}

    public User getUser() {return user;}

    public void setUser(User user) {this.user = user;}
}
