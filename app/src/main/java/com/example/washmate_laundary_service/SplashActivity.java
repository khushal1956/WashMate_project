package com.example.washmate_laundary_service;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    private static final int SPLASH_DELAY = 2500; // 2.5 seconds loading line

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // Now we actually show the splash UI

        mAuth = FirebaseAuth.getInstance();
        
        // Wait for SPLASH_DELAY milliseconds, then check language and route
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            android.content.SharedPreferences prefs = getSharedPreferences("WashMate_Prefs", MODE_PRIVATE);
            boolean isLanguageSet = prefs.getBoolean("IS_LANGUAGE_SET", false);
            
            if (!isLanguageSet) {
                startActivity(new Intent(SplashActivity.this, LanguageSelectionActivity.class));
                finish();
            } else {
                navigateToMain();
            }
        }, SPLASH_DELAY);
    }

    private void navigateToMain() {
        // Check if user is already logged in
        if (mAuth != null && mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();

            Runnable fallbackToCustomer = () -> {
                startActivity(new Intent(SplashActivity.this, CustomerDashboardActivity.class));
                finish();
            };

            // 1. Check if Admin
            db.collection(com.example.washmate_laundary_service.utils.FirebaseConstants.COLLECTION_ADMINS).document(uid).get()
                    .addOnSuccessListener(adminSnap -> {
                        if (adminSnap.exists()) {
                            startActivity(new Intent(SplashActivity.this, AdminDashboardActivity.class));
                            finish();
                        } else {
                            checkStaffRole(uid, fallbackToCustomer);
                        }
                    })
                    .addOnFailureListener(e -> {
                        checkStaffRole(uid, fallbackToCustomer);
                    });
        } else {
            // User is not logged in, go to login page
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void checkStaffRole(String uid, Runnable fallbackToCustomer) {
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        // 2. Check if Staff
        db.collection(com.example.washmate_laundary_service.utils.FirebaseConstants.COLLECTION_STAFF).document(uid).get()
                .addOnSuccessListener(staffSnap -> {
                    if (staffSnap.exists()) {
                        startActivity(new Intent(SplashActivity.this, StaffDashboardActivity.class));
                        finish();
                    } else {
                        checkCustomerRole(uid, fallbackToCustomer);
                    }
                })
                .addOnFailureListener(e -> {
                    checkCustomerRole(uid, fallbackToCustomer);
                });
    }

    private void checkCustomerRole(String uid, Runnable fallbackToCustomer) {
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        // 3. Check if Customer
        db.collection(com.example.washmate_laundary_service.utils.FirebaseConstants.COLLECTION_CUSTOMERS).document(uid).get()
                .addOnSuccessListener(customerSnap -> {
                    // Fallback to customer dashboard whether profile exists or not
                    fallbackToCustomer.run();
                })
                .addOnFailureListener(e -> {
                    fallbackToCustomer.run();
                });
    }
}
