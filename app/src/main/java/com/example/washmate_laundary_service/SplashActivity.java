package com.example.washmate_laundary_service;

import android.content.Intent;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends BaseActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth and navigate directly (no intro video)
        mAuth = FirebaseAuth.getInstance();
        navigateToMain();
    }

    private void navigateToMain() {
        // Check if user is already logged in
        if (mAuth != null && mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();

            // 1. Check if Admin
            db.collection(com.example.washmate_laundary_service.utils.FirebaseConstants.COLLECTION_ADMINS).document(uid).get()
                    .addOnSuccessListener(adminSnap -> {
                        if (adminSnap.exists()) {
                            startActivity(new Intent(SplashActivity.this, AdminDashboardActivity.class));
                            finish();
                            return;
                        }

                        // 2. Check if Staff
                        db.collection("STAFF").document(uid).get()
                                .addOnSuccessListener(staffSnap -> {
                                    if (staffSnap.exists()) {
                                        startActivity(new Intent(SplashActivity.this, StaffDashboardActivity.class));
                                        finish();
                                        return;
                                    }

                                    // 3. Default to Customer
                                    startActivity(new Intent(SplashActivity.this, CustomerDashboardActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    // Fallback to Customer or Login on error, just to not block
                                    startActivity(new Intent(SplashActivity.this, CustomerDashboardActivity.class));
                                    finish();
                                });
                    })
                    .addOnFailureListener(e -> {
                         // Fallback
                        startActivity(new Intent(SplashActivity.this, CustomerDashboardActivity.class));
                        finish();
                    });
        } else {
            // User is not logged in, go to login page
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
    }
}
