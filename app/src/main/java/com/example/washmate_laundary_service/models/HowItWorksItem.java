package com.example.washmate_laundary_service.models;

public class HowItWorksItem {
    private int imageResId;
    private String title;
    private String description;

    public HowItWorksItem(int imageResId, String title, String description) {
        this.imageResId = imageResId;
        this.title = title;
        this.description = description;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
