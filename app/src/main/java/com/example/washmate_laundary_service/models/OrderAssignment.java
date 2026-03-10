package com.example.washmate_laundary_service.models;

import java.util.Date;

public class OrderAssignment {
    private String assignmentId;
    private String orderId;
    private String staffId;
    private String taskType;
    private String assignedBy;
    private Date assignedAt;

    public OrderAssignment() {
        // Required for Firestore
    }

    public OrderAssignment(String assignmentId, String orderId, String staffId, String taskType, String assignedBy, Date assignedAt) {
        this.assignmentId = assignmentId;
        this.orderId = orderId;
        this.staffId = staffId;
        this.taskType = taskType;
        this.assignedBy = assignedBy;
        this.assignedAt = assignedAt;
    }

    // Getters and Setters
    public String getAssignmentId() { return assignmentId; }
    public void setAssignmentId(String assignmentId) { this.assignmentId = assignmentId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }

    public String getAssignedBy() { return assignedBy; }
    public void setAssignedBy(String assignedBy) { this.assignedBy = assignedBy; }

    public Date getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Date assignedAt) { this.assignedAt = assignedAt; }
}
