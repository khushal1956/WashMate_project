package com.example.washmate_laundary_service;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;


public class AdminDashboardActivity extends BaseActivity {

    private View headerBackground, cardManageOrders, cardManageServices, cardManageStaff;
    private View tvAdminGreeting, tvAdminSubtitle;
    private Button btnAdminLogout;
    private TextView tvTotalRevenue, tvTotalOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize Views
        headerBackground = findViewById(R.id.headerBackgroundOverlay);
        tvAdminGreeting = findViewById(R.id.tvAdminGreeting);
        tvAdminSubtitle = findViewById(R.id.tvAdminSubtitle);
        
        cardManageOrders = findViewById(R.id.cardManageOrders);
        cardManageServices = findViewById(R.id.cardManageServices);
        cardManageStaff = findViewById(R.id.cardManageStaff);
        
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvTotalOrders = findViewById(R.id.tvTotalOrders);
        
        btnAdminLogout = findViewById(R.id.btnAdminLogout);

        // Apply Entrance Animations
        applyEntranceAnimations();

        // Listeners
        if (btnAdminLogout != null) {
            btnAdminLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }

        // Add dummy clicks for premium feel
        View.OnClickListener dummyListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        };
        
        if (cardManageOrders != null) {
            cardManageOrders.setOnClickListener(v -> {
                Intent intent = new Intent(AdminDashboardActivity.this, AdminOrdersActivity.class);
                startActivity(intent);
            });
        }
        
        if (cardManageServices != null) {
            cardManageServices.setOnClickListener(v -> {
                Intent intent = new Intent(AdminDashboardActivity.this, ManageServicesActivity.class);
                startActivity(intent);
            });
        }
        if (cardManageStaff != null) {
            cardManageStaff.setOnClickListener(v -> {
                Intent intent = new Intent(AdminDashboardActivity.this, ManageStaffActivity.class);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchPendingOrdersCount();
        fetchDashboardStats();
    }

    private void fetchDashboardStats() {
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("ORDERS")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double totalRevenue = 0;
                    int totalOrders = queryDocumentSnapshots.size();

                    for (com.google.firebase.firestore.QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Double amount = doc.getDouble("totalAmount");
                        if (amount != null) {
                            totalRevenue += amount;
                        }
                    }

                    if (tvTotalRevenue != null) {
                        tvTotalRevenue.setText(String.format("₹%.0f", totalRevenue));
                    }
                    if (tvTotalOrders != null) {
                        tvTotalOrders.setText(String.valueOf(totalOrders));
                    }
                })
                .addOnFailureListener(e -> {
                     // Handle error silently or log
                });
    }

    private void fetchPendingOrdersCount() {
        TextView tvOrderBadge = findViewById(R.id.tvOrderBadge);
        if (tvOrderBadge == null) return;

        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("ORDERS")
                .whereEqualTo("status", "Pending")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int count = queryDocumentSnapshots.size();
                    if (count > 0) {
                        tvOrderBadge.setVisibility(View.VISIBLE);
                        tvOrderBadge.setText(count + " NEW");
                    } else {
                        tvOrderBadge.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    tvOrderBadge.setVisibility(View.GONE);
                });
    }

    private void applyEntranceAnimations() {
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        Animation scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);

        if (headerBackground != null && fadeIn != null) headerBackground.startAnimation(fadeIn);
        if (tvAdminGreeting != null && fadeIn != null) tvAdminGreeting.startAnimation(fadeIn);
        if (tvAdminSubtitle != null && fadeIn != null) tvAdminSubtitle.startAnimation(fadeIn);

        if (cardManageOrders != null && slideUp != null) cardManageOrders.startAnimation(slideUp);
        if (cardManageServices != null && slideUp != null) cardManageServices.startAnimation(slideUp);
        if (cardManageStaff != null && slideUp != null) cardManageStaff.startAnimation(slideUp);
    }
}
