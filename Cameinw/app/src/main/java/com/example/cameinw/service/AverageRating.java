package com.example.cameinw.service;

import com.example.cameinw.model.Place;
import com.example.cameinw.model.Review;

import java.util.List;

public class AverageRating {
    public static String getAveragePlaceRating(Place place) {
        List<Review> reviews = place.getReviews();

        if (((Integer) reviews.size()).equals(0)) {return " - ";}

        Integer averageRating = 0;

        for (Review singleReview: reviews) {
            String rating = singleReview.getRating().toString();
            if (rating.equals("ONE_STAR")) {
                averageRating+=1;
            } else if (rating.equals("TWO_STARS")) {
                averageRating+=2;
            } else if (rating.equals("THREE_STARS")) {
                averageRating+=3;
            } else if (rating.equals("FOUR_STARS")) {
                averageRating+=4;
            } else if (rating.equals("FIVE_STARS")) {
                averageRating+=5;
            }
        }
        averageRating = averageRating / reviews.size();

        if (averageRating<1.5) {
            return "1/5";
        } else if (averageRating<2.5) {
            return "2/5";
        } else if (averageRating<3.5) {
            return "3/5";
        } else if (averageRating<4.5) {
            return "4/5";
        } else {
            return "5/5";
        }
    }

}
