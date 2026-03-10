package com.example.washmate_laundary_service;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.washmate_laundary_service.models.Staff;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.DocumentSnapshot;
import android.widget.Toast;

public class TrackOrderActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView tvOrderTitle, tvOrderId;
    private ImageView ivStep1, ivStep2, ivStep3, ivStep4, ivStep5;
    private View viewLine1, viewLine2, viewLine3, viewLine4;

    // Status Logic
    private String currentStatus = "Pending";
    private FirebaseFirestore mFirestore;
    private Marker deliveryMarker;
    private ListenerRegistration staffLocationListener;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);

        mFirestore = FirebaseFirestore.getInstance();

        // Get info from intent
        String serviceName = getIntent().getStringExtra("SERVICE_NAME");
        orderId = getIntent().getStringExtra("ORDER_ID");
        currentStatus = getIntent().getStringExtra("ORDER_STATUS");
        if (currentStatus == null) currentStatus = "Pending";

        initializeViews();

        if (serviceName != null) tvOrderTitle.setText(serviceName);
        if (orderId != null) tvOrderId.setText("Order #" + orderId);

        updateTimeline(currentStatus);

        // Map setup
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void initializeViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        tvOrderTitle = findViewById(R.id.tvOrderTitle);
        tvOrderId = findViewById(R.id.tvOrderId);

        // Timeline indicators
        ivStep1 = findViewById(R.id.ivStep1);
        ivStep2 = findViewById(R.id.ivStep2);
        ivStep3 = findViewById(R.id.ivStep3);
        ivStep4 = findViewById(R.id.ivStep4);
        ivStep5 = findViewById(R.id.ivStep5);

        viewLine1 = findViewById(R.id.viewLine1);
        viewLine2 = findViewById(R.id.viewLine2);
        viewLine3 = findViewById(R.id.viewLine3);
        viewLine4 = findViewById(R.id.viewLine4);
    }

    private void updateTimeline(String status) {
        // Reset all
        int activeColor = ContextCompat.getColor(this, R.color.statusCompleted); // Green
        int inactiveColor = ContextCompat.getColor(this, R.color.textSecondary);
        int pendingColor = ContextCompat.getColor(this, R.color.statusPending); // Yellow

        // Step 1: Placed (Always active if order exists)
        ivStep1.setColorFilter(activeColor);
        viewLine1.setBackgroundColor(activeColor);

        // Logic based on status
        if (status.equals("Pending")) {
            // Only step 1 done
            ivStep2.setColorFilter(inactiveColor);
        } else if (status.equals("Pickup Assigned") || status.equals("Out for Pickup")) {
            ivStep2.setColorFilter(activeColor);
            viewLine2.setBackgroundColor(activeColor);
        } else if (status.equals("Picked Up") || status.equals("In Service")) {
            ivStep2.setColorFilter(activeColor);
            viewLine2.setBackgroundColor(activeColor);
            ivStep3.setColorFilter(activeColor);
            viewLine3.setBackgroundColor(activeColor);
        } else if (status.equals("Out for Delivery")) {
            ivStep2.setColorFilter(activeColor);
            viewLine2.setBackgroundColor(activeColor);
            ivStep3.setColorFilter(activeColor);
            viewLine3.setBackgroundColor(activeColor);
            ivStep4.setColorFilter(activeColor);
            viewLine4.setBackgroundColor(activeColor);
        } else if (status.equals("Delivered") || status.equals("Completed")) {
            ivStep2.setColorFilter(activeColor);
            viewLine2.setBackgroundColor(activeColor);
            ivStep3.setColorFilter(activeColor);
            viewLine3.setBackgroundColor(activeColor);
            ivStep4.setColorFilter(activeColor);
            viewLine4.setBackgroundColor(activeColor);
            ivStep5.setColorFilter(activeColor);
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (orderId != null) {
            fetchAssignedStaffAndTrack();
        }
    }

    private void fetchAssignedStaffAndTrack() {
        mFirestore.collection("ORDER_STAFF_ASSIGNMENTS")
                .whereEqualTo("orderId", orderId)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String staffId = queryDocumentSnapshots.getDocuments().get(0).getString("staffId");
                        if (staffId != null) {
                            startTrackingStaff(staffId);
                        }
                    } else {
                         // No staff assigned yet, show shop location or similar
                        LatLng shopLocation = new LatLng(28.6139, 77.2090);
                        mMap.addMarker(new MarkerOptions().position(shopLocation).title("WashMate Store"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(shopLocation, 14));
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to track order", Toast.LENGTH_SHORT).show());
    }

    private void startTrackingStaff(String staffId) {
        staffLocationListener = mFirestore.collection("STAFF").document(staffId)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Double lat = documentSnapshot.getDouble("currentLatitude");
                        Double lng = documentSnapshot.getDouble("currentLongitude");

                        if (lat != null && lng != null && lat != 0 && lng != 0) {
                            LatLng driverLocation = new LatLng(lat, lng);
                            updateDriverMarker(driverLocation);
                        }
                    }
                });
    }

    private void updateDriverMarker(LatLng location) {
        if (mMap == null) return;

        if (deliveryMarker == null) {
            deliveryMarker = mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title("Delivery Partner")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        } else {
            deliveryMarker.setPosition(location);
            mMap.animateCamera(CameraUpdateFactory.newLatLng(location));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (staffLocationListener != null) {
            staffLocationListener.remove();
        }
    }
}
