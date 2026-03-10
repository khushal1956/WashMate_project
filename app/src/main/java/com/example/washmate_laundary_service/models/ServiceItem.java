package com.example.washmate_laundary_service.models;

public class ServiceItem {
    private String id;
    private String name;
    private double price;
    private int quantity;

    public ServiceItem() {
        // Default constructor
    }

    public ServiceItem(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
