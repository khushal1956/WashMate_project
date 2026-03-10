package com.example.washmate_laundary_service.models;

import java.util.Date;

public class Staff {
    private String staffId;
    private String fullName;
    private String email;
    private String passwordHash;
    private String mobileNo;
    private String staffRole;
    private String availabilityStatus;
    private String createdByAdmin;
    private Date createdAt;
    private double currentLatitude;
    private double currentLongitude;

    public Staff() {
        // Required for Firestore
    }

    public Staff(String staffId, String fullName, String email, String passwordHash, String mobileNo, String staffRole, String availabilityStatus, String createdByAdmin, Date createdAt) {
        this.staffId = staffId;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.mobileNo = mobileNo;
        this.staffRole = staffRole;
        this.availabilityStatus = availabilityStatus;
        this.createdByAdmin = createdByAdmin;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getMobileNo() { return mobileNo; }
    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }

    public String getStaffRole() { return staffRole; }
    public void setStaffRole(String staffRole) { this.staffRole = staffRole; }

    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }

    public String getCreatedByAdmin() { return createdByAdmin; }
    public void setCreatedByAdmin(String createdByAdmin) { this.createdByAdmin = createdByAdmin; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public double getCurrentLatitude() { return currentLatitude; }
    public void setCurrentLatitude(double currentLatitude) { this.currentLatitude = currentLatitude; }

    public double getCurrentLongitude() { return currentLongitude; }
    public void setCurrentLongitude(double currentLongitude) { this.currentLongitude = currentLongitude; }
}
