package com.example.cameinw.Response;

import com.example.cameinw.enums.PropertyRating;
import com.example.cameinw.model.User;

public class ReviewResponse {
    private PropertyRating rating;
    private String comment;
    private User user;

    public PropertyRating getRating() {
        return rating;
    }

    public void setRating(PropertyRating rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
