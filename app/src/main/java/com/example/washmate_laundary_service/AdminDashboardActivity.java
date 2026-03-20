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
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import android.widget.ImageButton;

public class AdminDashboardActivity extends BaseActivity {

    private View headerBackground, cardManageOrders, cardManageServices, cardManageStaff, cardReports;
    private View tvAdminGreeting, tvAdminSubtitle;
    private Button btnAdminLogout;
    private TextView tvTotalRevenue, tvTotalOrders, tvTotalCustomers, tvActiveStaff;
    private TextView tvStatusPending, tvStatusInService, tvStatusCompleted;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize Views
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);
        headerBackground = findViewById(R.id.headerBackgroundOverlay);
        tvAdminGreeting = findViewById(R.id.tvAdminGreeting);
        tvAdminSubtitle = findViewById(R.id.tvAdminSubtitle);
        
        cardManageOrders = findViewById(R.id.cardManageOrders);
        cardManageServices = findViewById(R.id.cardManageServices);
        cardManageStaff = findViewById(R.id.cardManageStaff);
        cardReports = findViewById(R.id.cardReports);
        
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvTotalOrders = findViewById(R.id.tvTotalOrders);
        tvTotalCustomers = findViewById(R.id.tvTotalCustomers);
        tvActiveStaff = findViewById(R.id.tvActiveStaff);
        
        tvStatusPending = findViewById(R.id.tvStatusPending);
        tvStatusInService = findViewById(R.id.tvStatusInService);
        tvStatusCompleted = findViewById(R.id.tvStatusCompleted);
        
        btnAdminLogout = findViewById(R.id.btnAdminLogout);

        // Apply Entrance Animations
        applyEntranceAnimations();

        // Hamburger Menu Listener
        if (btnMenu != null && drawerLayout != null) {
            btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        // Navigation Drawer Listener
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_dashboard) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (id == R.id.nav_manage_orders) {
                    startActivity(new Intent(AdminDashboardActivity.this, AdminOrdersActivity.class));
                } else if (id == R.id.nav_manage_staff) {
                    startActivity(new Intent(AdminDashboardActivity.this, ManageStaffActivity.class));
                } else if (id == R.id.nav_manage_services) {
                    startActivity(new Intent(AdminDashboardActivity.this, ManageServicesActivity.class));
                } else if (id == R.id.nav_reports) {
                    startActivity(new Intent(AdminDashboardActivity.this, ReportsActivity.class));
                } else if (id == R.id.nav_logout) {
                    com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });
        }

        // Listeners for Bottom Logout button
        if (btnAdminLogout != null) {
            btnAdminLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }
        
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
        if (cardReports != null) {
            cardReports.setOnClickListener(v -> {
                Intent intent = new Intent(AdminDashboardActivity.this, ReportsActivity.class);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchPendingOrdersBadge();
        fetchDashboardStats();
        fetchUserAndStaffCounts();
    }

    private void fetchDashboardStats() {
        android.util.Log.d("AdminDashboard", "Fetching stats from collection: ORDERS");
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("ORDERS")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int docCount = queryDocumentSnapshots.size();
                    android.util.Log.d("AdminDashboard", "Found " + docCount + " documents in ORDERS");
                    
                    double totalRevenue = 0;
                    int totalOrders = docCount;
                    int pendingCount = 0;
                    int inServiceCount = 0;
                    int completedCount = 0;

                    for (com.google.firebase.firestore.QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        // Revenue Calculation
                        Object amountObj = doc.get("totalAmount");
                        double amount = 0;
                        if (amountObj instanceof Number) {
                            amount = ((Number) amountObj).doubleValue();
                        } else if (amountObj instanceof String) {
                            try {
                                amount = Double.parseDouble((String) amountObj);
                            } catch (Exception e) {
                                android.util.Log.e("AdminDashboard", "Failed to parse amount string: " + amountObj);
                            }
                        }
                        
                        String status = doc.getString("status");
                        if ("Completed".equalsIgnoreCase(status)) {
                            totalRevenue += amount;
                            completedCount++;
                        } else if ("Pending".equalsIgnoreCase(status)) {
                            pendingCount++;
                        } else if (status != null && !"Cancelled".equalsIgnoreCase(status) && !"Rejected".equalsIgnoreCase(status)) {
                            inServiceCount++;
                        }
                    }

                    if (tvTotalRevenue != null) {
                        tvTotalRevenue.setText(String.format("₹%.0f", totalRevenue));
                    }
                    if (tvTotalOrders != null) {
                        tvTotalOrders.setText(String.valueOf(totalOrders));
                    }
                    
                    // Update Breakdown
                    if (tvStatusPending != null) tvStatusPending.setText(String.valueOf(pendingCount));
                    if (tvStatusInService != null) tvStatusInService.setText(String.valueOf(inServiceCount));
                    if (tvStatusCompleted != null) tvStatusCompleted.setText(String.valueOf(completedCount));
                    
                    if (totalOrders == 0) {
                        android.util.Log.w("AdminDashboard", "Database appears to be empty or collection name mismatch");
                    }
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("AdminDashboard", "Error fetching dashboard stats", e);
                    Toast.makeText(this, "Stats fetch failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void fetchUserAndStaffCounts() {
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        
        // Fetch Customers
        db.collection("CUSTOMERS").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (tvTotalCustomers != null) {
                tvTotalCustomers.setText(String.valueOf(queryDocumentSnapshots.size()));
            }
        });
        
        // Fetch Available Staff
        db.collection("STAFF")
                .whereEqualTo("availabilityStatus", "Available")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (tvActiveStaff != null) {
                        tvActiveStaff.setText(String.valueOf(queryDocumentSnapshots.size()));
                    }
                });
    }

    private void fetchPendingOrdersBadge() {
        TextView tvOrderBadge = findViewById(R.id.tvOrderBadge);
        if (tvOrderBadge == null) return;

        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("ORDERS")
                .whereEqualTo("status", "Pending")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int pendingCount = queryDocumentSnapshots.size();
                    if (pendingCount > 0) {
                        tvOrderBadge.setVisibility(View.VISIBLE);
                        tvOrderBadge.setText(pendingCount + " NEW");
                    } else {
                        tvOrderBadge.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("AdminDashboard", "Error fetching pending count", e);
                    tvOrderBadge.setVisibility(View.GONE);
                });
    }

    private void applyEntranceAnimations() {
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        if (headerBackground != null && fadeIn != null) headerBackground.startAnimation(fadeIn);
        if (tvAdminGreeting != null && fadeIn != null) tvAdminGreeting.startAnimation(fadeIn);
        if (tvAdminSubtitle != null && fadeIn != null) tvAdminSubtitle.startAnimation(fadeIn);

        if (cardManageOrders != null && slideUp != null) cardManageOrders.startAnimation(slideUp);
        if (cardManageServices != null && slideUp != null) cardManageServices.startAnimation(slideUp);
        if (cardManageStaff != null && slideUp != null) cardManageStaff.startAnimation(slideUp);
        if (cardReports != null && slideUp != null) cardReports.startAnimation(slideUp);
    }
}
