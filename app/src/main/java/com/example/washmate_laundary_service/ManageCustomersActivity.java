package com.example.washmate_laundary_service;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.washmate_laundary_service.models.Customer;
import com.example.washmate_laundary_service.utils.FirebaseConstants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ManageCustomersActivity extends BaseActivity {

    private RecyclerView rvCustomerList;
    private CustomerAdapter adapter;
    private List<Customer> customerList;
    private FirebaseFirestore db;
    private LinearLayout llEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_customers);

        db = FirebaseFirestore.getInstance();
        customerList = new ArrayList<>();

        rvCustomerList = findViewById(R.id.rvCustomerList);
        llEmptyState = findViewById(R.id.llEmptyState);
        ImageButton btnBack = findViewById(R.id.btnBack);

        rvCustomerList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomerAdapter(customerList);
        rvCustomerList.setAdapter(adapter);

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        fetchCustomers();
    }

    private void fetchCustomers() {
        db.collection(FirebaseConstants.COLLECTION_CUSTOMERS)
                .orderBy("fullName", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        customerList.clear();
                        customerList.addAll(value.toObjects(Customer.class));
                        if (customerList.isEmpty()) {
                            llEmptyState.setVisibility(View.VISIBLE);
                            rvCustomerList.setVisibility(View.GONE);
                        } else {
                            llEmptyState.setVisibility(View.GONE);
                            rvCustomerList.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
