package com.example.washmate_laundary_service;

import android.content.Intent;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
public class CustomerOrdersActivity extends BaseActivity {

    private RecyclerView rvOrders;
    private LinearLayout llEmptyState;
    private CustomerOrdersAdapter adapter;
    private BottomNavigationView bottomNavigation;
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefresh;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        androidx.activity.EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_orders);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        initializeViews();
        setupRecyclerView();
        setupSwipeRefresh();
        fetchOrders();
    }

    private void initializeViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvOrders = findViewById(R.id.rvOrders);
        llEmptyState = findViewById(R.id.llEmptyState);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        setupBottomNavigation();
    }

    private void setupRecyclerView() {
        adapter = new CustomerOrdersAdapter();
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(this::fetchOrders);
        swipeRefresh.setColorSchemeResources(R.color.washmatePrimary);
    }

    private void fetchOrders() {
        if (mAuth.getCurrentUser() == null) {
            swipeRefresh.setRefreshing(false);
            Toast.makeText(this, "Please login to view orders", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String customerId = mAuth.getCurrentUser().getUid();

        mFirestore.collection("ORDERS")
                .whereEqualTo("customerId", customerId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    swipeRefresh.setRefreshing(false);
                    List<Order> orders = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Order order = document.toObject(Order.class);
                        orders.add(order);
                    }

                    // Sort orders by timestamp descending (Client-side to avoid index requirement)
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        orders.sort((o1, o2) -> Long.compare(o2.getTimestamp(), o1.getTimestamp()));
                    } else {
                        java.util.Collections.sort(orders, (o1, o2) -> Long.compare(o2.getTimestamp(), o1.getTimestamp()));
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
                    swipeRefresh.setRefreshing(false);
                    Log.e("CustomerOrders", "Error fetching orders", e);
                    Toast.makeText(this, "Failed to load orders: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void setupBottomNavigation() {
        setupBottomNavigation(R.id.nav_orders);
    }
}
