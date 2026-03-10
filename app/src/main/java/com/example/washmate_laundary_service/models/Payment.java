package com.example.washmate_laundary_service.models;

import java.util.Date;

public class Payment {
    private String paymentId;
    private String orderId;
    private String paymentMode;
    private String paymentStatus;
    private Date paymentDate;

    public Payment() {
        // Required for Firestore
    }

    public Payment(String paymentId, String orderId, String paymentMode, String paymentStatus, Date paymentDate) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.paymentMode = paymentMode;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
    }

    // Getters and Setters
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public Date getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }
}
