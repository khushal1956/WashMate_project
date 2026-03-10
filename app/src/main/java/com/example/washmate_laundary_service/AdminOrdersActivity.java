package com.example.washmate_laundary_service;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.washmate_laundary_service.models.Order;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.UUID;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import com.example.washmate_laundary_service.models.Staff;
import com.example.washmate_laundary_service.models.OrderAssignment;
import com.google.firebase.auth.FirebaseAuth;


public class AdminOrdersActivity extends BaseActivity implements AdminOrdersAdapter.OnOrderActionListener {

    private RecyclerView rvOrders;
    private LinearLayout llEmptyState;
    private AdminOrdersAdapter adapter;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);

        mFirestore = FirebaseFirestore.getInstance();

        initializeViews();
        setupRecyclerView();
        fetchOrders();
    }

    private void initializeViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvOrders = findViewById(R.id.rvOrders);
        llEmptyState = findViewById(R.id.llEmptyState);
    }

    private void setupRecyclerView() {
        adapter = new AdminOrdersAdapter(this);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(adapter);
    }

    private void fetchOrders() {
        mFirestore.collection("ORDERS")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Order> orders = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Order order = document.toObject(Order.class);
                        orders.add(order);
                    }

                    if (orders.isEmpty()) {
                        llEmptyState.setVisibility(View.VISIBLE);
                        rvOrders.setVisibility(View.GONE);
                    } else {
                        llEmptyState.setVisibility(View.GONE);
                        rvOrders.setVisibility(View.VISIBLE);
                        adapter.setOrders(orders);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AdminOrders", "Error fetching orders", e);
                    Toast.makeText(this, "Failed to load orders", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onAcceptOrder(Order order, int position) {
        updateOrderStatus(order.getOrderId(), "Accepted", position);
    }

    @Override
    public void onRejectOrder(Order order, int position) {
        updateOrderStatus(order.getOrderId(), "Rejected", position);
    }

    @Override
    public void onAssignOrder(Order order, int position) {
        showAssignStaffDialog(order, position);
    }

    private void showAssignStaffDialog(Order order, int position) {
        // Fetch Staff
        mFirestore.collection("STAFF")
                .whereEqualTo("availabilityStatus", "Available") 
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Staff> staffList = new ArrayList<>();
                    List<String> staffNames = new ArrayList<>();
                    
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Staff staff = document.toObject(Staff.class);
                        
                        boolean isDelivery = "Delivery Boy".equalsIgnoreCase(staff.getStaffRole());
                        boolean isCompleted = "Completed".equalsIgnoreCase(order.getStatus());

                        // Logic:
                        // 1. If Order is Completed -> Only show Delivery Boys
                        // 2. If Order is NOT Completed -> Show Washers/Pressmen (Non-Delivery)
                        //    (Unless we add logic for Pickup later, but for now this separates the flows)
                        
                        if ("Accepted".equalsIgnoreCase(order.getStatus())) {
                            // Stage 1: Pickup - Show Delivery Boys only
                            if (isDelivery) {
                                staffList.add(staff);
                                staffNames.add(staff.getFullName() + " (Delivery)");
                            }
                        } else if ("Picked Up".equalsIgnoreCase(order.getStatus())) {
                            // Stage 2: Service - Show Non-Delivery Staff only
                            if (!isDelivery) {
                                staffList.add(staff);
                                staffNames.add(staff.getFullName() + " (" + staff.getStaffRole() + ")");
                            }
                        } else if ("Completed".equalsIgnoreCase(order.getStatus())) {
                            // Stage 3: Delivery - Show Delivery Boys only
                             if (isDelivery) {
                                staffList.add(staff);
                                staffNames.add(staff.getFullName() + " (Delivery)");
                            }
                        } else {
                             // Fallback
                            staffList.add(staff);
                            staffNames.add(staff.getFullName() + " (" + staff.getStaffRole() + ")");
                        }
                    }


                    if (staffList.isEmpty()) {
                        Toast.makeText(this, "No available staff found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Show Dialog
                    String[] namesArray = staffNames.toArray(new String[0]);
                    new AlertDialog.Builder(this)
                            .setTitle("Assign Order to Staff")
                            .setItems(namesArray, (dialog, which) -> {
                                Staff selectedStaff = staffList.get(which);
                                assignOrderToStaff(order, selectedStaff, position);
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching staff: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void assignOrderToStaff(Order order, Staff staff, int position) {
        String assignmentId = UUID.randomUUID().toString();
        String adminId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        OrderAssignment assignment = new OrderAssignment(
                assignmentId,
                order.getOrderId(),
                staff.getStaffId(),
                staff.getStaffRole(), // Task Type based on Role
                adminId,

                new Date()
        );

        // Save Assignment
        mFirestore.collection("ORDER_STAFF_ASSIGNMENTS")
                .document(assignmentId)
                .set(assignment)
                .addOnSuccessListener(aVoid -> {
                    // Determine new status based on Role
                    // Determine new status based on Current Status & Role
                    String newStatus = "Processing";
                    String currentStatus = order.getStatus();

                    if ("Accepted".equalsIgnoreCase(currentStatus) && "Delivery Boy".equalsIgnoreCase(staff.getStaffRole())) {
                         newStatus = "Pickup Assigned";
                    } else if ("Picked Up".equalsIgnoreCase(currentStatus)) {
                         newStatus = "In Service";
                    } else if ("Completed".equalsIgnoreCase(currentStatus) && "Delivery Boy".equalsIgnoreCase(staff.getStaffRole())) {
                         newStatus = "Out for Delivery";
                    } else {
                        // Default Fallback
                        if ("Delivery Boy".equalsIgnoreCase(staff.getStaffRole())) {
                            newStatus = "Out for Delivery"; 
                        }
                    }
                    
                    // Update Order Status
                    updateOrderStatus(order.getOrderId(), newStatus, position);
                    Toast.makeText(this, "Assigned to " + staff.getFullName(), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to assign order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void updateOrderStatus(String orderId, String newStatus, int position) {
        mFirestore.collection("ORDERS")
                .document(orderId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Order " + newStatus.toLowerCase(), Toast.LENGTH_SHORT).show();
                    adapter.updateOrderStatus(position, newStatus);
                })
                .addOnFailureListener(e -> {
                    Log.e("AdminOrders", "Error updating order status", e);
                    Toast.makeText(this, "Failed to update order", Toast.LENGTH_SHORT).show();
                });
    }
}
