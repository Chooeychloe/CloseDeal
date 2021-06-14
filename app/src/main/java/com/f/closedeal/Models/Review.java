package com.f.closedeal.Models;

public class Review {

    String review, ratedUser, rater,rating, timeStamp;

    public Review() {
    }

    public Review(String review, String ratedUser, String rater, String rating, String timeStamp) {
        this.review = review;
        this.ratedUser = ratedUser;
        this.rater = rater;
        this.rating = rating;
        this.timeStamp = timeStamp;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getRatedUser() {
        return ratedUser;
    }

    public void setRatedUser(String ratedUser) {
        this.ratedUser = ratedUser;
    }

    public String getRater() {
        return rater;
    }

    public void setRater(String rater) {
        this.rater = rater;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }


}
