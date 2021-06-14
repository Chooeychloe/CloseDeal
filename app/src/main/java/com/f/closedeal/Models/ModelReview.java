package com.f.closedeal.Models;

public class ModelReview {

    String uid, ratings, review, timeStamp;

    public ModelReview() {
    }

    public ModelReview(String uid, String ratings, String review, String timeStamp) {
        this.uid = uid;
        this.ratings = ratings;
        this.review = review;
        this.timeStamp = timeStamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
