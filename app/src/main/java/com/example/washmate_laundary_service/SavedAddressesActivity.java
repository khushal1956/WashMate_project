package com.example.washmate_laundary_service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.washmate_laundary_service.models.CustomerAddress;
import com.example.washmate_laundary_service.utils.FirebaseConstants;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SavedAddressesActivity extends BaseActivity {

    private RecyclerView rvAddresses;
    private LinearLayout llEmptyState;
    private ProgressBar progressBar;
    private ImageButton btnBack;
    private MaterialButton btnAddAddress;
    
    private SavedAddressesAdapter adapter;
    private List<CustomerAddress> addressList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<Intent> locationPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_addresses);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        addressList = new ArrayList<>();

        initializeViews();
        setupRecyclerView();
        setupLauncher();
        
        loadAddresses();

        btnBack.setOnClickListener(v -> finish());
        btnAddAddress.setOnClickListener(v -> {
            Intent intent = new Intent(this, LocationPickerActivity.class);
            locationPickerLauncher.launch(intent);
        });
    }

    private void initializeViews() {
        rvAddresses = findViewById(R.id.rvAddresses);
        llEmptyState = findViewById(R.id.llEmptyState);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);
        btnAddAddress = findViewById(R.id.btnAddAddress);
    }

    private void setupRecyclerView() {
        adapter = new SavedAddressesAdapter(addressList, new SavedAddressesAdapter.OnAddressActionListener() {
            @Override
            public void onDelete(CustomerAddress address) {
                deleteAddress(address);
            }

            @Override
            public void onSetDefault(CustomerAddress address) {
                setDefaultAddress(address);
            }
        });
        rvAddresses.setLayoutManager(new LinearLayoutManager(this));
        rvAddresses.setAdapter(adapter);
    }

    private void setupLauncher() {
        locationPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        String addressText = result.getData().getStringExtra("address");
                        String city = result.getData().getStringExtra("city");
                        String pincode = result.getData().getStringExtra("pincode");
                        
                        if (addressText != null) {
                            saveNewAddress(addressText, city, pincode);
                        }
                    }
                }
        );
    }

    private void loadAddresses() {
        if (mAuth.getCurrentUser() == null) return;
        
        progressBar.setVisibility(View.VISIBLE);
        db.collection(FirebaseConstants.COLLECTION_CUSTOMERS)
                .document(mAuth.getCurrentUser().getUid())
                .collection("addresses")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    addressList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        CustomerAddress address = doc.toObject(CustomerAddress.class);
                        addressList.add(address);
                    }
                    updateUI();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load addresses", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUI() {
        if (addressList.isEmpty()) {
            llEmptyState.setVisibility(View.VISIBLE);
            rvAddresses.setVisibility(View.GONE);
        } else {
            llEmptyState.setVisibility(View.GONE);
            rvAddresses.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

    private void saveNewAddress(String addressText, String city, String pincode) {
        String uid = mAuth.getCurrentUser().getUid();
        String addressId = UUID.randomUUID().toString();
        
        boolean isFirst = addressList.isEmpty();
        CustomerAddress newAddress = new CustomerAddress(addressId, uid, addressText, city, pincode, isFirst, new Date());

        progressBar.setVisibility(View.VISIBLE);
        db.collection(FirebaseConstants.COLLECTION_CUSTOMERS)
                .document(uid)
                .collection("addresses")
                .document(addressId)
                .set(newAddress)
                .addOnSuccessListener(aVoid -> {
                    loadAddresses();
                    Toast.makeText(this, "Address saved successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to save address", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteAddress(CustomerAddress address) {
        db.collection(FirebaseConstants.COLLECTION_CUSTOMERS)
                .document(mAuth.getCurrentUser().getUid())
                .collection("addresses")
                .document(address.getAddressId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    loadAddresses();
                    Toast.makeText(this, "Address deleted", Toast.LENGTH_SHORT).show();
                });
    }

    private void setDefaultAddress(CustomerAddress selectedAddress) {
        String uid = mAuth.getCurrentUser().getUid();
        progressBar.setVisibility(View.VISIBLE);
        
        // Use a batch to update multiple documents
        com.google.firebase.firestore.WriteBatch batch = db.batch();
        
        for (CustomerAddress addr : addressList) {
            boolean isNewDefault = addr.getAddressId().equals(selectedAddress.getAddressId());
            batch.update(db.collection(FirebaseConstants.COLLECTION_CUSTOMERS)
                    .document(uid).collection("addresses").document(addr.getAddressId()), 
                    "isDefault", isNewDefault);
        }
        
        batch.commit().addOnSuccessListener(aVoid -> {
            loadAddresses();
            Toast.makeText(this, "Default address updated", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        });
    }
}
