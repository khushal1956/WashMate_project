package com.example.washmate_laundary_service.models;

import java.util.Date;

public class Feedback {
    private String feedbackId;
    private String orderId;
    private String customerId;
    private int rating;
    private String comments;
    private Date createdAt;

    public Feedback() {
        // Required for Firestore
    }

    public Feedback(String feedbackId, String orderId, String customerId, int rating, String comments, Date createdAt) {
        this.feedbackId = feedbackId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.rating = rating;
        this.comments = comments;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getFeedbackId() { return feedbackId; }
    public void setFeedbackId(String feedbackId) { this.feedbackId = feedbackId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
