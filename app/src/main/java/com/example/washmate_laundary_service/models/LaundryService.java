package com.example.washmate_laundary_service.models;

public class LaundryService {
    private String serviceId;
    private String serviceName;
    private double price;
    private String status;

    public LaundryService() {
        // Required for Firestore
    }

    public LaundryService(String serviceId, String serviceName, double price, String status) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.price = price;
        this.status = status;
    }

    // Getters and Setters
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
