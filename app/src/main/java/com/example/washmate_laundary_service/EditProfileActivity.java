package com.example.washmate_laundary_service;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.washmate_laundary_service.utils.FirebaseConstants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends BaseActivity {

    private TextInputEditText etFullName, etMobile;
    private AutoCompleteTextView spinnerGender;
    private MaterialButton btnSave;
    private ProgressBar progressBar;
    private TextView tvInitials;
    
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
        }

        initializeViews();
        setupGenderSpinner();
        loadUserData();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveProfileChanges());
    }

    private void initializeViews() {
        etFullName = findViewById(R.id.etFullName);
        etMobile = findViewById(R.id.etMobile);
        spinnerGender = findViewById(R.id.spinnerGender);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);
        tvInitials = findViewById(R.id.tvInitials);
    }

    private void setupGenderSpinner() {
        String[] genders = {"Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genders);
        spinnerGender.setAdapter(adapter);
    }

    private void loadUserData() {
        if (userId == null) return;

        progressBar.setVisibility(View.VISIBLE);
        db.collection(FirebaseConstants.COLLECTION_CUSTOMERS).document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    progressBar.setVisibility(View.GONE);
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("fullName");
                        String mobile = documentSnapshot.getString("mobileNo");
                        String gender = documentSnapshot.getString("gender");

                        if (name != null) {
                            etFullName.setText(name);
                            setInitials(name);
                        }
                        if (mobile != null) etMobile.setText(mobile);
                        if (gender != null) spinnerGender.setText(gender, false);
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                });
    }

    private void setInitials(String fullName) {
        if (fullName != null && !fullName.isEmpty()) {
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

    private void saveProfileChanges() {
        String name = etFullName.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String gender = spinnerGender.getText().toString().trim();

        if (name.isEmpty() || mobile.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName", name);
        updates.put("mobileNo", mobile);
        updates.put("gender", gender);

        db.collection(FirebaseConstants.COLLECTION_CUSTOMERS).document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
