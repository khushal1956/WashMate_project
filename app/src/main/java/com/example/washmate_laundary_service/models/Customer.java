package com.example.washmate_laundary_service.models;

import java.util.Date;

public class Customer {
    private String customerId;
    private String fullName;
    private String email;
    private String passwordHash;
    private String mobileNo;
    private String status;
    private Date createdAt;

    public Customer() {
        // Required for Firestore
    }

    public Customer(String customerId, String fullName, String email, String passwordHash, String mobileNo, String status, Date createdAt) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.mobileNo = mobileNo;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getMobileNo() { return mobileNo; }
    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
