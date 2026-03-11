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
import com.example.washmate_laundary_service.adapters.SliderAdapter;

public class CustomerDashboardActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    
    // UI Components
    private TextView tvGreeting, tvHeaderName, tvHeaderEmail;
    private androidx.drawerlayout.widget.DrawerLayout drawerLayout;
    private com.google.android.material.navigation.NavigationView navigationView;
    private ImageButton btnMenu;
    private androidx.viewpager2.widget.ViewPager2 vpSlider;
    
    // Slider
    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable;
    
    // Bottom Nav
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        initializeViews();
        setupSlider();
        setupDrawer();
        fetchUserData();
        setupServiceCards();
    }

    private void initializeViews() {
        tvGreeting = findViewById(R.id.tvGreeting);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);
        vpSlider = findViewById(R.id.vpSlider);
        
        // Navigation Header Views
        View headerView = navigationView.getHeaderView(0);
        tvHeaderName = headerView.findViewById(R.id.tvHeaderName);
        tvHeaderEmail = headerView.findViewById(R.id.tvHeaderEmail);
        
        bottomNavigation = findViewById(R.id.bottomNavigation);
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_home);
            bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) return true;
                if (itemId == R.id.nav_orders) {
                    startActivity(new Intent(this, CustomerOrdersActivity.class));
                    return true;
                }
                if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(this, ProfileActivity.class));
                    return true;
                }
                 if (itemId == R.id.nav_clothing_selection) {
                    startActivity(new Intent(this, ClothingSelectionActivity.class));
                    return true;
                }
                if (itemId == R.id.nav_support) {
                    startActivity(new Intent(this, SupportActivity.class));
                    return true;
                }
                return false;
            });
        }
    }

    private void setupSlider() {
        List<com.example.washmate_laundary_service.adapters.SliderAdapter.SliderItem> sliderItems = new ArrayList<>();
        // Premium Slider Items (HD Photos)
        sliderItems.add(new com.example.washmate_laundary_service.adapters.SliderAdapter.SliderItem(
                R.drawable.slide_1_washing, 
                "Doorstep Laundry Service", 
                "Premium Washing & Care"
        ));
        sliderItems.add(new com.example.washmate_laundary_service.adapters.SliderAdapter.SliderItem(
                R.drawable.slide_2_ironing, 
                "Steam Iron & Press", 
                "Wrinkle-free professional finish"
        ));
        sliderItems.add(new com.example.washmate_laundary_service.adapters.SliderAdapter.SliderItem(
               R.drawable.slide_3_delivery, 
                "Free Pickup & Delivery", 
                "At your doorstep, on time"
        ));
        sliderItems.add(new com.example.washmate_laundary_service.adapters.SliderAdapter.SliderItem(
               R.drawable.slide_4_cleaning, 
                "Deep Wash & Care", 
                "Fabric-friendly stain removal"
        ));

        vpSlider.setAdapter(new com.example.washmate_laundary_service.adapters.SliderAdapter(sliderItems));
        
        // Auto-Slide
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = vpSlider.getCurrentItem();
                int totalItems = vpSlider.getAdapter().getItemCount();
                int nextItem = (currentItem + 1) % totalItems;
                vpSlider.setCurrentItem(nextItem, true);
                sliderHandler.postDelayed(this, 4500);
            }
        };
        sliderHandler.postDelayed(sliderRunnable, 4500);
    }
    
    private void setupDrawer() {
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        
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
                                tvGreeting.setText(getString(R.string.greeting_format, name.split(" ")[0]));
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
        
        View cardNewOrder = findViewById(R.id.cardNewOrder);
        if (cardNewOrder != null) cardNewOrder.setOnClickListener(v -> startActivity(new Intent(this, ClothingSelectionActivity.class)));
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (sliderRunnable != null) sliderHandler.postDelayed(sliderRunnable, 4500);
        if (bottomNavigation != null) bottomNavigation.setSelectedItemId(R.id.nav_home);
    }
}
