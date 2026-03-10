package com.example.washmate_laundary_service.models;

public class Order {
    private String orderId;
    private String customerId;
    private String customerName;
    private String serviceName;
    private String serviceType;
    private String itemDescription;
    private int quantity;
    private String additionalServices; // Comma-separated list of additional services
    private double additionalServicesCharge;
    private String pickupAddress;
    private String city;
    private String pincode;
    private String pickupDate;
    private String paymentMode;
    private String paymentMethod; // UPI, Credit Card, or Debit Card (if online)
    private double serviceCharge;
    private double totalAmount;
    private String status;
    private String paymentStatus; // Paid, Pending, Failed
    private long timestamp;


    public Order() {
        // Default constructor required for Firebase
    }

    public Order(String orderId, String customerId, String customerName, String serviceName,
                 String serviceType, String itemDescription, int quantity, String additionalServices,
                 double additionalServicesCharge, String pickupAddress, String city, String pincode,
                 String pickupDate, String paymentMode, String paymentMethod,
                 double serviceCharge, double totalAmount, String paymentStatus) {
        this.orderId = orderId;

        this.customerId = customerId;
        this.customerName = customerName;
        this.serviceName = serviceName;
        this.serviceType = serviceType;
        this.itemDescription = itemDescription;
        this.quantity = quantity;
        this.additionalServices = additionalServices;
        this.additionalServicesCharge = additionalServicesCharge;
        this.pickupAddress = pickupAddress;
        this.city = city;
        this.pincode = pincode;
        this.pickupDate = pickupDate;
        this.paymentMode = paymentMode;
        this.paymentMethod = paymentMethod;
        this.serviceCharge = serviceCharge;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.status = "Pending";
        this.timestamp = System.currentTimeMillis();
    }


    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public String getItemDescription() { return itemDescription; }
    public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getAdditionalServices() { return additionalServices; }
    public void setAdditionalServices(String additionalServices) { this.additionalServices = additionalServices; }

    public double getAdditionalServicesCharge() { return additionalServicesCharge; }
    public void setAdditionalServicesCharge(double additionalServicesCharge) { this.additionalServicesCharge = additionalServicesCharge; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getPickupDate() { return pickupDate; }
    public void setPickupDate(String pickupDate) { this.pickupDate = pickupDate; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public double getServiceCharge() { return serviceCharge; }
    public void setServiceCharge(double serviceCharge) { this.serviceCharge = serviceCharge; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}

