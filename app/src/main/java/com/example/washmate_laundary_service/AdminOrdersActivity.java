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
import android.app.DatePickerDialog;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Button;

import com.example.washmate_laundary_service.models.Staff;
import com.example.washmate_laundary_service.models.OrderAssignment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class AdminOrdersActivity extends BaseActivity implements AdminOrdersAdapter.OnOrderActionListener {

    private RecyclerView rvOrders;
    private LinearLayout llEmptyState;
    private AdminOrdersAdapter adapter;
    private FirebaseFirestore mFirestore;

    private Spinner spinnerStatusFilter;
    private Button btnDateFilter;
    private ImageButton btnClearFilter;
    
    private String currentStatusFilter = "All";
    private Calendar currentDateFilter = null;

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
        
        spinnerStatusFilter = findViewById(R.id.spinnerStatusFilter);
        btnDateFilter = findViewById(R.id.btnDateFilter);
        btnClearFilter = findViewById(R.id.btnClearFilter);
        
        setupFilters();
    }
    
    private void setupFilters() {
        // Setup Status Spinner
        String[] statuses = {"All", "Pending", "Accepted", "Pickup Assigned", "Picked Up", "In Service", "Out for Delivery", "Completed", "Rejected", "Cancelled"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatusFilter.setAdapter(spinnerAdapter);
        
        spinnerStatusFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentStatusFilter = statuses[position];
                fetchOrders(); // Refresh based on filter
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        // Setup Date Picker
        btnDateFilter.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            if (currentDateFilter != null) {
                c = currentDateFilter;
            }
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                if (currentDateFilter == null) currentDateFilter = Calendar.getInstance();
                currentDateFilter.set(year, month, dayOfMonth);
                
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                btnDateFilter.setText(sdf.format(currentDateFilter.getTime()));
                btnClearFilter.setVisibility(View.VISIBLE);
                
                fetchOrders(); // Refresh based on date
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
        
        // Clear Filter
        btnClearFilter.setOnClickListener(v -> {
            currentDateFilter = null;
            btnDateFilter.setText("Filter Date");
            btnClearFilter.setVisibility(View.GONE);
            fetchOrders();
        });
    }

    private void setupRecyclerView() {
        adapter = new AdminOrdersAdapter(this);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(adapter);
    }

    private void fetchOrders() {
        android.util.Log.d("AdminOrders", "Fetching orders. Status: " + currentStatusFilter + ", Date: " + (currentDateFilter != null ? currentDateFilter.getTime().toString() : "None"));
        
        Query query = mFirestore.collection("ORDERS");
        
        if (!currentStatusFilter.equals("All")) {
            query = query.whereEqualTo("status", currentStatusFilter);
        }
        
        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int docCount = queryDocumentSnapshots.size();
                    android.util.Log.d("AdminOrders", "Query returned " + docCount + " documents");
                    
                    List<Order> orders = new ArrayList<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String filterDateStr = currentDateFilter != null ? sdf.format(currentDateFilter.getTime()) : null;
                    
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Order order = document.toObject(Order.class);
                            if (order == null) continue;
                            
                            // Client-side date filtering
                            if (filterDateStr != null) {
                                String orderDate = sdf.format(new Date(order.getTimestamp()));
                                if (!orderDate.equals(filterDateStr)) {
                                    android.util.Log.d("AdminOrders", "Skipping order " + order.getOrderId() + " due to date mismatch: " + orderDate + " vs " + filterDateStr);
                                    continue;
                                }
                            }
                            
                            orders.add(order);
                        } catch (Exception e) {
                            android.util.Log.e("AdminOrders", "Failed to deserialize Order document: " + document.getId(), e);
                        }
                    }

                    android.util.Log.d("AdminOrders", "Final order list size after filtering: " + orders.size());

                    if (orders.isEmpty()) {
                        llEmptyState.setVisibility(View.VISIBLE);
                        rvOrders.setVisibility(View.GONE);
                        if (docCount > 0) {
                             Toast.makeText(this, "No orders match the selected filters", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        llEmptyState.setVisibility(View.GONE);
                        rvOrders.setVisibility(View.VISIBLE);
                        // Sort locally (newest first)
                        orders.sort((o1, o2) -> Long.compare(o2.getTimestamp(), o1.getTimestamp()));
                        adapter.setOrders(orders);
                    }
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("AdminOrders", "Firestore Error fetching orders", e);
                    Toast.makeText(this, "Failed to load orders: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                    
                    // Also update assignedStaff tracking fields directly in the order
                    mFirestore.collection("ORDERS").document(order.getOrderId())
                            .update("assignedStaffId", staff.getStaffId(),
                                    "assignedStaffName", staff.getFullName());
                                    
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
