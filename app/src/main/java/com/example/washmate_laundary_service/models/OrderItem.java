package com.example.washmate_laundary_service.models;

public class OrderItem {
    private String orderItemId;
    private String orderId;
    private String serviceId;
    private int quantity;
    private double itemPrice;

    public OrderItem() {
        // Required for Firestore
    }

    public OrderItem(String orderItemId, String orderId, String serviceId, int quantity, double itemPrice) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.serviceId = serviceId;
        this.quantity = quantity;
        this.itemPrice = itemPrice;
    }

    // Getters and Setters
    public String getOrderItemId() { return orderItemId; }
    public void setOrderItemId(String orderItemId) { this.orderItemId = orderItemId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getItemPrice() { return itemPrice; }
    public void setItemPrice(double itemPrice) { this.itemPrice = itemPrice; }
}
