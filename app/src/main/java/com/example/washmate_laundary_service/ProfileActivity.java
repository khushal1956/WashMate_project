package com.example.washmate_laundary_service;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.washmate_laundary_service.utils.FirebaseConstants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends BaseActivity {

    private TextView tvUserName, tvCurrentEmail, tvInitials;
    private ImageButton btnBack, btnEditProfile;
    private MaterialButton btnLogout;
    private BottomNavigationView bottomNavigation;
    private SwitchMaterial switchNotifications;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        androidx.activity.EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        View mainLayout = findViewById(R.id.mainLayout);
        if (mainLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                return insets;
            });
        }

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize Views
        initializeViews();

        // Load User Data
        loadUserData();

        // Set Click Listeners
        setClickListeners();
        
        // Setup Bottom Navigation
        setupBottomNavigation();
    }

    private void initializeViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvCurrentEmail = findViewById(R.id.tvCurrentEmail);
        tvInitials = findViewById(R.id.tvInitials);
        
        btnBack = findViewById(R.id.btnBack);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);
        switchNotifications = findViewById(R.id.switchNotifications);
        
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void loadUserData() {
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (tvCurrentEmail != null) tvCurrentEmail.setText(email);
            
            mFirestore.collection(FirebaseConstants.COLLECTION_CUSTOMERS)
                    .document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("fullName");
                            if (name != null) {
                                if (tvUserName != null) tvUserName.setText(name);
                                setInitials(name);
                            }
                        }
                    });
        }
    }

    private void setInitials(String fullName) {
        if (fullName != null && !fullName.isEmpty() && tvInitials != null) {
            StringBuilder initials = new StringBuilder();
            String[] parts = fullName.trim().split("\\s+");
            for (int i = 0; i < Math.min(parts.length, 2); i++) {
                if (!parts[i].isEmpty()) {
                    initials.append(parts[i].charAt(0));
                }
            }
            tvInitials.setText(initials.toString().toUpperCase());
        }
    }

    private void setClickListeners() {
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
        
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> {
                startActivity(new Intent(this, EditProfileActivity.class));
            });
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                mAuth.signOut();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
        
        View cardMyAddresses = findViewById(R.id.cardMyAddresses);
        if (cardMyAddresses != null) {
            cardMyAddresses.setOnClickListener(v -> {
                startActivity(new Intent(this, SavedAddressesActivity.class));
            });
        }
        
        View cardPaymentMethods = findViewById(R.id.cardPaymentMethods);
        if (cardPaymentMethods != null) {
            cardPaymentMethods.setOnClickListener(v -> {
                startActivity(new Intent(this, PaymentMethodsActivity.class));
            });
        }
        
        View cardOrderHistory = findViewById(R.id.cardOrderHistory);
        if (cardOrderHistory != null) {
            cardOrderHistory.setOnClickListener(v -> {
                startActivity(new Intent(this, CustomerOrdersActivity.class));
            });
        }
        
        View cardHelpSupport = findViewById(R.id.cardHelpSupport);
        if (cardHelpSupport != null) {
            cardHelpSupport.setOnClickListener(v -> {
                startActivity(new Intent(this, SupportActivity.class));
            });
        }
        
        View cardPromos = findViewById(R.id.cardPromos);
        if (cardPromos != null) {
            cardPromos.setOnClickListener(v -> {
                startActivity(new Intent(this, PromosActivity.class));
            });
        }
        
        View cardPrivacySecurity = findViewById(R.id.cardPrivacySecurity);
        if (cardPrivacySecurity != null) {
            cardPrivacySecurity.setOnClickListener(v -> {
                // Navigate to forgot password screen as requested
                startActivity(new Intent(this, ForgotPasswordActivity.class));
            });
        }
        
        if (switchNotifications != null) {
            switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String status = isChecked ? "enabled" : "disabled";
                Toast.makeText(this, "Notifications " + status, Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void setupBottomNavigation() {
        setupBottomNavigation(R.id.nav_profile);
    }
}
