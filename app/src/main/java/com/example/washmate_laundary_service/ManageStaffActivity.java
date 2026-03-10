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
import com.example.washmate_laundary_service.models.Staff;
import com.example.washmate_laundary_service.utils.FirebaseConstants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class ManageStaffActivity extends BaseActivity implements StaffAdapter.OnStaffActionListener {

    private RecyclerView rvStaffList;
    private StaffAdapter adapter;
    private List<Staff> staffList;
    private FirebaseFirestore mFirestore;
    private LinearLayout llEmptyState;
    private ImageButton btnBack;
    private FloatingActionButton fabAddStaff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_staff);

        mFirestore = FirebaseFirestore.getInstance();
        staffList = new ArrayList<>();

        // Initialize Views
        rvStaffList = findViewById(R.id.rvStaffList);
        llEmptyState = findViewById(R.id.llEmptyState);
        btnBack = findViewById(R.id.btnBack);
        fabAddStaff = findViewById(R.id.fabAddStaff);

        // Setup RecyclerView
        rvStaffList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StaffAdapter(staffList, this);
        rvStaffList.setAdapter(adapter);

        // Listeners
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (fabAddStaff != null) {
            fabAddStaff.setOnClickListener(v -> {
                Intent intent = new Intent(ManageStaffActivity.this, AddStaffActivity.class);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchStaffData();
    }

    private void fetchStaffData() {
        if (mFirestore == null) return;

        mFirestore.collection("STAFF")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    staffList.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        staffList.addAll(queryDocumentSnapshots.toObjects(Staff.class));
                        if (llEmptyState != null) llEmptyState.setVisibility(View.GONE);
                    } else {
                        if (llEmptyState != null) llEmptyState.setVisibility(View.VISIBLE);
                    }
                    if (adapter != null) adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching staff: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDeleteStaff(Staff staff) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Staff")
                .setMessage("Are you sure you want to delete " + staff.getFullName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteStaffFromFirestore(staff);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteStaffFromFirestore(Staff staff) {
        mFirestore.collection("STAFF").document(staff.getStaffId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Staff deleted successfully", Toast.LENGTH_SHORT).show();
                    fetchStaffData(); // Refresh list
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete staff", Toast.LENGTH_SHORT).show();
                });
    }
}

