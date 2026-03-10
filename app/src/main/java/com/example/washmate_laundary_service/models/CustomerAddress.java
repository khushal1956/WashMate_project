package com.example.washmate_laundary_service.models;

import java.util.Date;

public class CustomerAddress {
    private String addressId;
    private String customerId;
    private String addressText;
    private String city;
    private String pincode;
    private boolean isDefault;
    private Date createdAt;

    public CustomerAddress() {
        // Required for Firestore
    }

    public CustomerAddress(String addressId, String customerId, String addressText, String city, String pincode, boolean isDefault, Date createdAt) {
        this.addressId = addressId;
        this.customerId = customerId;
        this.addressText = addressText;
        this.city = city;
        this.pincode = pincode;
        this.isDefault = isDefault;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getAddressId() { return addressId; }
    public void setAddressId(String addressId) { this.addressId = addressId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getAddressText() { return addressText; }
    public void setAddressText(String addressText) { this.addressText = addressText; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public boolean getIsDefault() { return isDefault; }
    public void setIsDefault(boolean isDefault) { this.isDefault = isDefault; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
