package com.example.washmate_laundary_service;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.washmate_laundary_service.models.Order;
import com.example.washmate_laundary_service.utils.FirebaseConstants;
import com.google.firebase.firestore.FirebaseFirestore;

public class OrderTrackingActivity extends BaseActivity {

    private String orderId;
    private FirebaseFirestore mFirestore;
    
    private TextView tvOrderId, tvOrderDate;
    
    // Timeline steps
    private View layoutStepPlaced, layoutStepPickup, layoutStepProcess, layoutStepOut, layoutStepDelivered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);

        mFirestore = FirebaseFirestore.getInstance();
        orderId = getIntent().getStringExtra("ORDER_ID");
        
        if (orderId == null) {
            Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        fetchOrderDetails();
        
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void initializeViews() {
        tvOrderId = findViewById(R.id.tvOrderId);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        
        layoutStepPlaced = findViewById(R.id.layoutStepPlaced);
        layoutStepPickup = findViewById(R.id.layoutStepPickup);
        layoutStepProcess = findViewById(R.id.layoutStepProcess);
        layoutStepOut = findViewById(R.id.layoutStepOut);
        layoutStepDelivered = findViewById(R.id.layoutStepDelivered);
        
        // Initialize placeholders titles
        setStepData(layoutStepPlaced, "Order Placed", "We have received your order", false, true, false); // Top
        setStepData(layoutStepPickup, "Pickup Assigned", "Driver is on the way", false, false, false);
        setStepData(layoutStepProcess, "In Process", "Cleaning in progress", false, false, false);
        setStepData(layoutStepOut, "Out for Delivery", "Order is out for delivery", false, false, false);
        setStepData(layoutStepDelivered, "Delivered", "Order has been delivered", true, false, false); // Bottom
    }

    private void fetchOrderDetails() {
        mFirestore.collection("ORDERS").document(orderId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Order order = documentSnapshot.toObject(Order.class);
                        if (order != null) {
                            updateUI(order);
                        }
                    } else {
                        Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching order", Toast.LENGTH_SHORT).show());
    }

    private void updateUI(Order order) {
        tvOrderId.setText("Order #" + order.getOrderId());
        tvOrderDate.setText(order.getPickupDate()); // Or formatted timestamp if available key exists
        
        String status = order.getStatus(); // Pending, Pickup Assigned, In Progress, Out for Delivery, Completed
        
        // Reset all first
        resetSteps();
        
        // Logic to activate steps based on status
        // Case insensitive check
        if (status == null) status = "Pending";
        String s = status.toLowerCase();
        
        // Step 1: Placed - Always active
        activateStep(layoutStepPlaced, true);
        
        if (s.contains("pickup") || s.contains("process") || s.contains("clean") || s.contains("out") || s.contains("completed") || s.contains("delivered")) {
            activateStep(layoutStepPickup, true);
        }
        
        if (s.contains("process") || s.contains("clean") || s.contains("wash") || s.contains("iron") || s.contains("out") || s.contains("completed") || s.contains("delivered")) {
            activateStep(layoutStepProcess, true);
        }
        
        if (s.contains("out") || s.contains("completed") || s.contains("delivered")) {
            activateStep(layoutStepOut, true);
        }
        
        if (s.contains("completed") || s.contains("delivered")) {
            activateStep(layoutStepDelivered, true);
        }
    }
    
    private void resetSteps() {
        activateStep(layoutStepPlaced, false);
        activateStep(layoutStepPickup, false);
        activateStep(layoutStepProcess, false);
        activateStep(layoutStepOut, false);
        activateStep(layoutStepDelivered, false);
    }
    
    private void setStepData(View view, String title, String subtitle, boolean isLast, boolean isFirst, boolean isActive) {
        if (view == null) return;
        TextView tvTitle = view.findViewById(R.id.tvStepTitle);
        TextView tvSubtitle = view.findViewById(R.id.tvStepSubtitle);
        View lineTop = view.findViewById(R.id.lineTop);
        View lineBottom = view.findViewById(R.id.lineBottom);
        
        tvTitle.setText(title);
        tvSubtitle.setText(subtitle);
        
        if (isFirst) lineTop.setVisibility(View.INVISIBLE);
        if (isLast) lineBottom.setVisibility(View.INVISIBLE);
        
        activateStep(view, isActive);
    }

    private void activateStep(View view, boolean isActive) {
        if (view == null) return;
        ImageView ivIcon = view.findViewById(R.id.ivStatusIcon);
        TextView tvTitle = view.findViewById(R.id.tvStepTitle);
        TextView tvSubtitle = view.findViewById(R.id.tvStepSubtitle);
        View lineTop = view.findViewById(R.id.lineTop);
        View lineBottom = view.findViewById(R.id.lineBottom);
        
        int activeColor = ContextCompat.getColor(this, R.color.washmatePrimary);
        int inactiveColor = ContextCompat.getColor(this, R.color.textSecondary);
        int grayColor = ContextCompat.getColor(this, R.color.light_gray);
        
        if (isActive) {
            ivIcon.setImageResource(R.drawable.ic_check_circle); 
            ivIcon.setColorFilter(activeColor);
            tvTitle.setTextColor(activeColor);
            
            lineTop.setBackgroundColor(activeColor); 
            lineBottom.setBackgroundColor(activeColor);
        } else {
            ivIcon.setImageResource(R.drawable.ic_circle_outline);
            ivIcon.setColorFilter(inactiveColor);
            tvTitle.setTextColor(inactiveColor);
            
            lineTop.setBackgroundColor(grayColor);
            lineBottom.setBackgroundColor(grayColor);
        }
    }
}
