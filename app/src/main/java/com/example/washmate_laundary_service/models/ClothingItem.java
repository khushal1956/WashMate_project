package com.example.washmate_laundary_service.models;

public class ClothingItem {
    private String name;
    private int iconResId; 
    // In a real app with dynamic backend, this would have an ID and Image URL

    public ClothingItem(String name, int iconResId) {
        this.name = name;
        this.iconResId = iconResId;
    }

    public String getName() {
        return name;
    }

    public int getIconResId() {
        return iconResId;
    }
}
