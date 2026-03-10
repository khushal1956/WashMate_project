package com.example.washmate_laundary_service.models;

import java.util.Date;

public class Admin {
    private String adminId;
    private String fullName;
    private String email;
    private String passwordHash;
    private String mobileNo;
    private Date createdAt;

    public Admin() {
        // Required for Firestore
    }

    public Admin(String adminId, String fullName, String email, String passwordHash, String mobileNo, Date createdAt) {
        this.adminId = adminId;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.mobileNo = mobileNo;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getMobileNo() { return mobileNo; }
    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
