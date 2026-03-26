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
    private List<com.example.washmate_laundary_service.models.PromoItem> promoList;

    
    private TextView tvFeaturedTitle, tvFeaturedSub, tvFeaturedCode;
    private View layoutPromoBadge;
    private View layoutSpecialOfferHeader;





    // Bottom Nav
    private BottomNavigationView bottomNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        promoList = new ArrayList<>();


        initializeViews();
        setupDrawer();
        fetchUserData();
        setupServiceCards();
        fetchPromosFromFirestore();

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
        
        // Featured Offer Views
        tvFeaturedTitle = findViewById(R.id.tvFeaturedTitle);
        tvFeaturedSub = findViewById(R.id.tvFeaturedSub);
        tvFeaturedCode = findViewById(R.id.tvFeaturedCode);
        layoutPromoBadge = findViewById(R.id.layoutPromoBadge);
        layoutSpecialOfferHeader = findViewById(R.id.layoutSpecialOfferHeader);

        
        if (layoutSpecialOfferHeader != null) {
            layoutSpecialOfferHeader.setVisibility(View.GONE);
        }




        
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
    }



    private void fetchPromosFromFirestore() {
        mFirestore.collection(com.example.washmate_laundary_service.utils.FirebaseConstants.COLLECTION_PROMOTIONS)
                .orderBy("title") // or any order
                .limit(5) // Just show first 5 for dashboard
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        promoList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            com.example.washmate_laundary_service.models.PromoItem item = doc.toObject(com.example.washmate_laundary_service.models.PromoItem.class);
                            if (item != null) {
                                item.setId(doc.getId());
                                promoList.add(item);
                            }
                        }
                        
                        // Update Featured Offer with the first promo if available

                        if (!promoList.isEmpty()) {
                            com.example.washmate_laundary_service.models.PromoItem firstPromo = promoList.get(0);
                            if (tvFeaturedTitle != null) tvFeaturedTitle.setText(firstPromo.getTitle());
                            if (tvFeaturedSub != null) tvFeaturedSub.setText(firstPromo.getDescription());
                            if (tvFeaturedCode != null) tvFeaturedCode.setText(firstPromo.getCode());
                            
                            if (layoutPromoBadge != null) {
                                layoutPromoBadge.setOnClickListener(v -> {
                                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(android.content.Context.CLIPBOARD_SERVICE);
                                    android.content.ClipData clip = android.content.ClipData.newPlainText("Promo Code", firstPromo.getCode());
                                    if (clipboard != null) {
                                        clipboard.setPrimaryClip(clip);
                                        Toast.makeText(this, "Promo Code " + firstPromo.getCode() + " copied!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            if (layoutSpecialOfferHeader != null && layoutSpecialOfferHeader.getVisibility() != View.VISIBLE) {
                                layoutSpecialOfferHeader.setAlpha(0f);
                                layoutSpecialOfferHeader.setVisibility(View.VISIBLE);
                                layoutSpecialOfferHeader.animate().alpha(1f).setDuration(500).start();
                            }

                        } else {
                            if (layoutSpecialOfferHeader != null) {
                                layoutSpecialOfferHeader.setVisibility(View.GONE);
                            }
                        }
                    }


                });
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
