package com.example.washmate_laundary_service.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CartItem implements Serializable {
    private String clothingName;
    private int quantity;
    private List<ServiceItem> selectedServices; // e.g. Wash, Iron
    private double totalPrice;

    public CartItem(String clothingName, int quantity, List<ServiceItem> selectedServices) {
        this.clothingName = clothingName;
        this.quantity = quantity;
        this.selectedServices = selectedServices;
        calculateTotal();
    }

    private void calculateTotal() {
        double serviceSum = 0;
        for (ServiceItem service : selectedServices) {
            serviceSum += service.getPrice();
        }
        // Price = (Sum of services) * Quantity
        this.totalPrice = serviceSum * quantity;
    }

    public String getClothingName() {
        return clothingName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateTotal();
    }

    public List<ServiceItem> getSelectedServices() {
        return selectedServices;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
    
    public String getServicesSummary() {
        StringBuilder sb = new StringBuilder();
        for (ServiceItem s : selectedServices) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(s.getName());
        }
        return sb.toString();
    }
}
