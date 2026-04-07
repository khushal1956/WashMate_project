package com.example.washmate_laundary_service.models;

public class HowItWorksItem {
    private int imageResId;
    private String title;
    private String description;
    private String videoUri;
    private boolean isVideo;

    public HowItWorksItem(int imageResId, String title, String description) {
        this.imageResId = imageResId;
        this.title = title;
        this.description = description;
        this.isVideo = false;
    }

    public HowItWorksItem(String videoUri, String title, String description) {
        this.videoUri = videoUri;
        this.title = title;
        this.description = description;
        this.isVideo = true;
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

    public String getVideoUri() {
        return videoUri;
    }

    public boolean isVideo() {
        return isVideo;
    }
}
