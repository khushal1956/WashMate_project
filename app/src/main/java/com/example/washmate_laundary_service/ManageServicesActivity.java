package com.example.washmate_laundary_service;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.washmate_laundary_service.models.LaundryService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class ManageServicesActivity extends BaseActivity {

    private RecyclerView rvServiceList;
    private LaundryServiceAdapter adapter;
    private List<LaundryService> serviceList;
    private FirebaseFirestore mFirestore;
    private LinearLayout llEmptyState;
    private ImageButton btnBack;
    private FloatingActionButton fabAddService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_services);

        mFirestore = FirebaseFirestore.getInstance();
        serviceList = new ArrayList<>();

        // Initialize Views
        rvServiceList = findViewById(R.id.rvServiceList);
        llEmptyState = findViewById(R.id.llEmptyState);
        btnBack = findViewById(R.id.btnBack);
        fabAddService = findViewById(R.id.fabAddService);

        // Setup RecyclerView
        rvServiceList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LaundryServiceAdapter(serviceList, null);
        rvServiceList.setAdapter(adapter);

        // Listeners
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (fabAddService != null) {
            fabAddService.setOnClickListener(v -> {
                Intent intent = new Intent(ManageServicesActivity.this, AddServiceActivity.class);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchServiceData();
    }

    private void fetchServiceData() {
        if (mFirestore == null) return;

        mFirestore.collection("LAUNDRY_SERVICES")
                .orderBy("serviceName", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    serviceList.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        serviceList.addAll(queryDocumentSnapshots.toObjects(LaundryService.class));
                        if (llEmptyState != null) llEmptyState.setVisibility(View.GONE);
                    } else {
                        if (llEmptyState != null) llEmptyState.setVisibility(View.VISIBLE);
                    }
                    if (adapter != null) adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                });
    }
}
