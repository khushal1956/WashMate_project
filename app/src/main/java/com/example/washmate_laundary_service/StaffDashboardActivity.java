package com.example.washmate_laundary_service;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.washmate_laundary_service.models.Order;
import com.example.washmate_laundary_service.models.Staff;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.washmate_laundary_service.services.LocationService;
import com.example.washmate_laundary_service.models.Staff;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class StaffDashboardActivity extends BaseActivity {

    private TextView tvStaffName;
    private RecyclerView rvAssignedOrders;
    private LinearLayout llEmptyState;
    private ProgressBar progressBar;
    private ImageButton btnLogout;
    
    private StaffOrdersAdapter adapter;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private String currentStaffId;
    private static final int PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_dashboard);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        currentStaffId = mAuth.getCurrentUser().getUid();

        initializeViews();
        setupRecyclerView();
        loadStaffDetails();

        fetchAssignedOrders();
        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        } else {
            startLocationService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService();
            } else {
                Toast.makeText(this, "Location permission required for tracking", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startLocationService() {
        Intent serviceIntent = new Intent(this, LocationService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    @Override
    protected void onDestroy() {
        // Stop service when dashboard is closed (optional, depends on requirement to track in background even when app closed)
        // keeping it running for now as per requirement "background location update"
        super.onDestroy();
    }

    private void initializeViews() {
        tvStaffName = findViewById(R.id.tvStaffName);
        rvAssignedOrders = findViewById(R.id.rvAssignedOrders);
        llEmptyState = findViewById(R.id.llEmptyState);
        progressBar = findViewById(R.id.progressBar);
        btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void setupRecyclerView() {
        adapter = new StaffOrdersAdapter();
        rvAssignedOrders.setLayoutManager(new LinearLayoutManager(this));
        rvAssignedOrders.setAdapter(adapter);
        
        adapter.setOnStatusChangeListener((order, newStatus) -> {
            updateOrderStatus(order, newStatus);
        });
    }

    private void updateOrderStatus(Order order, String newStatus) {
        progressBar.setVisibility(View.VISIBLE);
        mFirestore.collection("ORDERS").document(order.getOrderId())
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Order marked as " + newStatus, Toast.LENGTH_SHORT).show();
                    fetchAssignedOrders(); // Refresh list to update UI
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to update status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadStaffDetails() {
        mFirestore.collection("STAFF").document(currentStaffId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Staff staff = documentSnapshot.toObject(Staff.class);
                        if (staff != null) {
                            tvStaffName.setText("Welcome back, " + staff.getFullName() + "!");
                        }
                    }
                });
    }

    private void fetchAssignedOrders() {
        progressBar.setVisibility(View.VISIBLE);
        
        mFirestore.collection("ORDERS")
                .whereEqualTo("assignedStaffId", currentStaffId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Order> orders = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        orders.add(doc.toObject(Order.class));
                    }
                    displayOrders(orders);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error fetching assignments", Toast.LENGTH_SHORT).show();
                });
    }

    private void displayOrders(List<Order> orders) {
        progressBar.setVisibility(View.GONE);
        if (orders.isEmpty()) {
            showEmptyState();
        } else {
            llEmptyState.setVisibility(View.GONE);
            rvAssignedOrders.setVisibility(View.VISIBLE);
            adapter.setOrders(orders);
        }
    }

    private void showEmptyState() {
        progressBar.setVisibility(View.GONE);
        llEmptyState.setVisibility(View.VISIBLE);
        rvAssignedOrders.setVisibility(View.GONE);
    }
}
