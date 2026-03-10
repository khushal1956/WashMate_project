package com.example.washmate_laundary_service.models;

import java.util.Date;

public class Notification {
    private String notificationId;
    private String userType;
    private String userId;
    private String message;
    private boolean isRead;
    private Date createdAt;

    public Notification() {
        // Required for Firestore
    }

    public Notification(String notificationId, String userType, String userId, String message, boolean isRead, Date createdAt) {
        this.notificationId = notificationId;
        this.userType = userType;
        this.userId = userId;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean getIsRead() { return isRead; }
    public void setIsRead(boolean isRead) { this.isRead = isRead; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
