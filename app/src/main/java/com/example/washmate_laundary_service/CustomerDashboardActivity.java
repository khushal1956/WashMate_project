package com.example.washmate_laundary_service;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.washmate_laundary_service.utils.FirebaseConstants;
import androidx.core.view.GravityCompat;
import android.widget.ImageButton;
import java.util.ArrayList;
import java.util.List;


public class CustomerDashboardActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    
    // UI Components
    private TextView tvGreeting, tvHeaderName, tvHeaderEmail;
    private androidx.drawerlayout.widget.DrawerLayout drawerLayout;
    private com.google.android.material.navigation.NavigationView navigationView;
    private ImageButton btnMenu, btnNotification;
    
    // Bottom Nav
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        initializeViews();
        setupDrawer();
        fetchUserData();
        setupServiceCards();
    }

    private void initializeViews() {
        tvGreeting = findViewById(R.id.tvGreeting);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);
        
        // Navigation Header Views
        View headerView = navigationView.getHeaderView(0);
        tvHeaderName = headerView.findViewById(R.id.tvHeaderName);
        tvHeaderEmail = headerView.findViewById(R.id.tvHeaderEmail);
        
        setupBottomNavigation(R.id.nav_home);
        
        // Profile Icon trigger
        View ivUserProfile = findViewById(R.id.ivUserProfile);
        if (ivUserProfile != null) {
            ivUserProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        }
        
        // Menu button trigger
        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        // Pulse animation for Offer Tag
        View tagFirstOrder = findViewById(R.id.tagFirstOrder);
        if (tagFirstOrder != null) {
            Animation pulse = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            pulse.setDuration(1200);
            pulse.setRepeatMode(Animation.REVERSE);
            pulse.setRepeatCount(Animation.INFINITE);
            tagFirstOrder.startAnimation(pulse);
        }
    }

    
    private void setupDrawer() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Already here
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else if (id == R.id.nav_my_orders) {
                startActivity(new Intent(this, CustomerOrdersActivity.class));
            } else if (id == R.id.nav_clothing_selection) {
                startActivity(new Intent(this, ClothingSelectionActivity.class));
            } else if (id == R.id.nav_settings) {
                // Settings Activity (not created yet, placeholder or profile)
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_support) {
                startActivity(new Intent(this, SupportActivity.class));
            } else if (id == R.id.nav_logout) {
                mAuth.signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void fetchUserData() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            String email = mAuth.getCurrentUser().getEmail();
            if (tvHeaderEmail != null) tvHeaderEmail.setText(email);
            
            mFirestore.collection(FirebaseConstants.COLLECTION_CUSTOMERS).document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("fullName");
                            if (name != null) {
                                if (tvGreeting != null) tvGreeting.setText(getString(R.string.greeting_format, name.split(" ")[0]));
                                if (tvHeaderName != null) tvHeaderName.setText(name);
                            }
                        }
                    });
        }
    }
    
    private void setupServiceCards() {
        View cardWashing = findViewById(R.id.cardWashing);
        if (cardWashing != null) cardWashing.setOnClickListener(v -> startActivity(new Intent(this, WashingServiceActivity.class)));
        
        View cardDryClean = findViewById(R.id.cardDryClean);
        if (cardDryClean != null) cardDryClean.setOnClickListener(v -> startActivity(new Intent(this, DryCleaningServiceActivity.class)));
        
        View cardIroning = findViewById(R.id.cardIroning);
        if (cardIroning != null) cardIroning.setOnClickListener(v -> startActivity(new Intent(this, IroningServiceActivity.class)));
        
        View cardPremium = findViewById(R.id.cardPremium);
        if (cardPremium != null) cardPremium.setOnClickListener(v -> startActivity(new Intent(this, PremiumServiceActivity.class)));

        // Claim Offer logic
        View btnClaimOffer1 = findViewById(R.id.btnClaimOffer1);
        if (btnClaimOffer1 != null) {
            btnClaimOffer1.setOnClickListener(v -> {
                // Animation feedback
                v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).withEndAction(() -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).withEndAction(() -> {
                        Intent intent = new Intent(this, ClothingSelectionActivity.class);
                        intent.putExtra("SERVICE_NAME", "First Order Offer");
                        intent.putExtra("SERVICE_TYPE", "washing");
                        intent.putExtra("SERVICE_PRICE", 0.7); // 30% off multiplier
                        startActivity(intent);
                    });
                });
                Toast.makeText(this, "🎉 Offer Applied! 30% discount will be added to your first order.", Toast.LENGTH_LONG).show();
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // No manual selection needed, setupBottomNavigation handles it
    }
}
