package com.example.washmate_laundary_service.models;

public class PromoItem {
    private String id;
    private String title;
    private String description;
    private String code;
    private double discountValue; // Value of discount
    private String discountType;  // "PERCENT" or "FLAT"

    public PromoItem() {
        // Required for Firestore
    }

    public PromoItem(String id, String title, String description, String code, double discountValue, String discountType) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.code = code;
        this.discountValue = discountValue;
        this.discountType = discountType;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public double getDiscountValue() { return discountValue; }
    public void setDiscountValue(double discountValue) { this.discountValue = discountValue; }
    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }
}

