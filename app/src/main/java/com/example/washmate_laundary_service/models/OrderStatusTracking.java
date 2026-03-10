package com.example.washmate_laundary_service.models;

import java.util.Date;

public class OrderStatusTracking {
    private String trackingId;
    private String orderId;
    private String status;
    private String updatedBy;
    private Date updatedAt;

    public OrderStatusTracking() {
        // Required for Firestore
    }

    public OrderStatusTracking(String trackingId, String orderId, String status, String updatedBy, Date updatedAt) {
        this.trackingId = trackingId;
        this.orderId = orderId;
        this.status = status;
        this.updatedBy = updatedBy;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getTrackingId() { return trackingId; }
    public void setTrackingId(String trackingId) { this.trackingId = trackingId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
