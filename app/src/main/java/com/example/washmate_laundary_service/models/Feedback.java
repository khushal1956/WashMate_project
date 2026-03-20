package com.example.washmate_laundary_service.models;

import java.util.Date;

public class Feedback {
    private String feedbackId;
    private String orderId;
    private String customerId;
    private float rating;
    private String comment;
    private Date timestamp;

    public Feedback() {
        // Required for Firestore
    }

    public Feedback(String feedbackId, String orderId, String customerId, float rating, String comment, Date timestamp) {
        this.feedbackId = feedbackId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getFeedbackId() { return feedbackId; }
    public void setFeedbackId(String feedbackId) { this.feedbackId = feedbackId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}
