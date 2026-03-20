package com.example.washmate_laundary_service.models;

public class PromoItem {
    private String title;
    private String description;
    private String code;

    public PromoItem(String title, String description, String code) {
        this.title = title;
        this.description = description;
        this.code = code;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCode() { return code; }
}
