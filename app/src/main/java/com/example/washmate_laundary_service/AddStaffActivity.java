package com.example.washmate_laundary_service;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.washmate_laundary_service.models.Staff;
import com.example.washmate_laundary_service.utils.FirebaseConstants;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Date;
import java.util.UUID;

public class AddStaffActivity extends BaseActivity {

    private TextInputEditText etFullName, etEmail, etMobile, etPassword;
    private Spinner spnRole;
    private Button btnSubmit;
    private ImageButton btnBack;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize Views
        etFullName = findViewById(R.id.etStaffFullName);
        etEmail = findViewById(R.id.etStaffEmail);
        etMobile = findViewById(R.id.etStaffMobile);
        etPassword = findViewById(R.id.etStaffPassword);
        spnRole = findViewById(R.id.spnStaffRole);
        btnSubmit = findViewById(R.id.btnAddStaffSubmit);
        btnBack = findViewById(R.id.btnBack);

        // Setup Spinners
        setupSpinners();

        // Listeners
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> saveStaff());
        }
    }

    private void setupSpinners() {
        String[] roles = {"Washer", "Dryer", "Pressman", "Delivery Boy"};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRole.setAdapter(roleAdapter);
    }

    private void saveStaff() {
        String name = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String role = spnRole.getSelectedItem().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(mobile) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmit.setEnabled(false);
        btnSubmit.setText("Creating Account...");

        if (mAuth.getCurrentUser() == null) {
             Toast.makeText(this, "Admin not logged in", Toast.LENGTH_SHORT).show();
             btnSubmit.setEnabled(true);
             btnSubmit.setText("Register Staff");
             return;
        }
        String adminId = mAuth.getCurrentUser().getUid();

        // Use a secondary Firebase App to create the user without logging out the Admin
        com.google.firebase.FirebaseOptions options = com.google.firebase.FirebaseApp.getInstance().getOptions();
        String secondaryAppName = "SecondaryApp";
        com.google.firebase.FirebaseApp secondaryApp;

        try {
            secondaryApp = com.google.firebase.FirebaseApp.getInstance(secondaryAppName);
        } catch (IllegalStateException e) {
            secondaryApp = com.google.firebase.FirebaseApp.initializeApp(this, options, secondaryAppName);
        }

        FirebaseAuth secondaryAuth = FirebaseAuth.getInstance(secondaryApp);

        secondaryAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String staffId = authResult.getUser().getUid();
                    
                    Staff staff = new Staff(staffId, name, email, password, mobile, role, "Available", adminId, new Date());

                    mFirestore.collection("STAFF").document(staffId)
                            .set(staff)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Staff Added Successfully", Toast.LENGTH_SHORT).show();
                                cleanupSecondaryApp(secondaryAppName);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to save staff data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                cleanupSecondaryApp(secondaryAppName);
                                btnSubmit.setEnabled(true);
                                btnSubmit.setText("Register Staff");
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to create account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    cleanupSecondaryApp(secondaryAppName);
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Register Staff");
                });
    }

    private void cleanupSecondaryApp(String appName) {
        try {
            com.google.firebase.FirebaseApp app = com.google.firebase.FirebaseApp.getInstance(appName);
            // Sign out the secondary user just in case
            FirebaseAuth.getInstance(app).signOut();
            // Delete the app instance to free resources
            // Note: delete() is not directly exposed in older SDKs or might be async. 
            // In standard Android implementation, we just leave it or try to reflect entry, but
            // for simple use cases, just letting it be is often fine, or we can leave it initialized.
            // However, to avoid memory leaks if called repeatedly:
             try {
                // There isn't a simple public delete() in standard Android API without some work or ignoring it.
                // But generally, creating it once and reusing is better, or just ignoring.
                // For this implementation, we will check existence in saveStaff to reuse.
            } catch (Exception e) {}
        } catch (IllegalStateException e) {
            // App doesn't exist
        }
    }
}
