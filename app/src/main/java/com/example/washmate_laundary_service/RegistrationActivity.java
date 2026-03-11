package com.example.washmate_laundary_service;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import androidx.activity.EdgeToEdge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.washmate_laundary_service.models.Customer;
import com.example.washmate_laundary_service.models.CustomerAddress;
import com.example.washmate_laundary_service.models.Admin;
import com.example.washmate_laundary_service.utils.FirebaseConstants;

import java.util.Date;

public class RegistrationActivity extends BaseActivity {

    private TextInputEditText etFullName, etEmail, etMobile, etAddress, etPassword, etConfirmPassword;
    private TextInputLayout tilFullName, tilEmail, tilMobile, tilAddress, tilPassword, tilConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin, tvTitle, tvSubtitle;
    private View headerBackground, cardRegistration, btnBack;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        // Initialize Views
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);
        etAddress = findViewById(R.id.etAddress);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        tilFullName = findViewById(R.id.tilFullName);
        tilEmail = findViewById(R.id.tilEmail);
        tilMobile = findViewById(R.id.tilMobile);
        tilAddress = findViewById(R.id.tilAddress);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        headerBackground = findViewById(R.id.headerBackground);
        cardRegistration = findViewById(R.id.cardRegistration);
        btnBack = findViewById(R.id.btnBack);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Animations removed for stability test
        // applyEntranceAnimations();

        if (btnRegister != null) {
            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validateInput()) {
                        registerUser();
                    }
                }
            });
        }

        if (tvLogin != null) {
            tvLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // Go back to login
                }
            });
        }
    }

    private void applyEntranceAnimations() {
        // Disabled for stability
    }

    private boolean validateInput() {
        boolean isValid = true;

        // Reset errors
        if (tilFullName != null) tilFullName.setError(null);
        if (tilEmail != null) tilEmail.setError(null);
        if (tilMobile != null) tilMobile.setError(null);
        if (tilAddress != null) tilAddress.setError(null);
        if (tilPassword != null) tilPassword.setError(null);
        if (tilConfirmPassword != null) tilConfirmPassword.setError(null);

        String fullName = etFullName != null ? etFullName.getText().toString().trim() : "";
        String email = etEmail != null ? etEmail.getText().toString().trim() : "";
        String mobile = etMobile != null ? etMobile.getText().toString().trim() : "";
        String address = etAddress != null ? etAddress.getText().toString().trim() : "";
        String password = etPassword != null ? etPassword.getText().toString() : "";
        String confirmPassword = etConfirmPassword != null ? etConfirmPassword.getText().toString() : "";

        if (TextUtils.isEmpty(fullName)) {
            if (tilFullName != null) tilFullName.setError("Full name is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(email)) {
            if (tilEmail != null) tilEmail.setError("Email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (tilEmail != null) tilEmail.setError("Enter a valid email address");
            isValid = false;
        }

        if (TextUtils.isEmpty(mobile)) {
            if (tilMobile != null) tilMobile.setError("Mobile number is required");
            isValid = false;
        } else if (mobile.length() < 10) {
            if (tilMobile != null) tilMobile.setError("Enter a valid mobile number");
            isValid = false;
        }

        if (TextUtils.isEmpty(address)) {
            if (tilAddress != null) tilAddress.setError("Address is required");
            isValid = false;
        }



        if (TextUtils.isEmpty(password)) {
            if (tilPassword != null) tilPassword.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            if (tilPassword != null) tilPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            if (tilConfirmPassword != null) tilConfirmPassword.setError("Confirm password is required");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            if (tilConfirmPassword != null) tilConfirmPassword.setError("Passwords do not match");
            isValid = false;
        }

        return isValid;
    }

    private void registerUser() {
        if (etFullName == null || etEmail == null || etEmail == null || etMobile == null || etAddress == null ||
            etPassword == null) {
            return;
        }

        // --- Network Pre-check ---
        if (!com.example.washmate_laundary_service.utils.NetworkUtils.isNetworkAvailable(this)) {
            showConnectionTroubleshooter("No internet connection detected. Please enable Wi-Fi or Mobile Data.");
            return;
        }

        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String password = etPassword.getText().toString();

        btnRegister.setEnabled(false);
        btnRegister.setText("Registering...");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration success, save to Firestore
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                saveUserToFirestore(user.getUid(), fullName, email, mobile, address);
                            } else {
                                btnRegister.setEnabled(true);
                                btnRegister.setText("Register");
                                Toast.makeText(RegistrationActivity.this, "Authentication error: User null", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // If registration fails, display a message to the user.
                            btnRegister.setEnabled(true);
                            btnRegister.setText("Register");
                            Exception e = task.getException();
                            String error = e != null ? e.getMessage() : "Unknown authentication error";
                            
                            if (e instanceof com.google.firebase.FirebaseNetworkException) {
                                showConnectionTroubleshooter("Could not reach authentication server. This usually means the device has no internet or Firebase is blocked.");
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Registration Failed: " + error, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void showConnectionTroubleshooter(String message) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Network Connection Issue")
                .setMessage(message + "\n\nTroubleshooting Steps:\n1. Open your browser and check if google.com opens.\n2. Ensure Google Play Services is enabled.\n3. If using an Emulator, restart the Emulator with 'Cold Boot'.\n4. Check if your firewall is blocking Firebase.")
                .setPositiveButton("Retry", (dialog, which) -> registerUser())
                .setNegativeButton("Check Settings", (dialog, which) -> {
                    startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void saveUserToFirestore(String userId, String fullName, String email, String mobile, String address) {
        saveCustomerToFirestore(userId, fullName, email, mobile, address);
    }

    private void saveCustomerToFirestore(String userId, String fullName, String email, String mobile, String address) {
        Customer customer = new Customer(userId, fullName, email, "", mobile, "Active", new Date());

        mFirestore.collection(FirebaseConstants.COLLECTION_CUSTOMERS).document(userId)
                .set(customer)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            saveAddressToFirestore(userId, address);
                        } else {
                            handleFirestoreError(task.getException(), "saving profile");
                        }
                    }
                });
    }

    private void handleFirestoreError(Exception e, String context) {
        btnRegister.setEnabled(true);
        btnRegister.setText("Register");
        String errorMsg = e != null ? e.getMessage() : "Unknown error";
        
        if (errorMsg != null && errorMsg.toLowerCase().contains("network")) {
            showConnectionTroubleshooter("Connection timeout while " + context + ". The database is currently unreachable.");
        } else {
            Toast.makeText(this, "Database Error during " + context + ": " + errorMsg, Toast.LENGTH_LONG).show();
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(RegistrationActivity.this, CustomerDashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void saveAddressToFirestore(String userId, String addressText) {
        String addressId = mFirestore.collection(FirebaseConstants.COLLECTION_CUSTOMER_ADDRESSES).document().getId();
        CustomerAddress address = new CustomerAddress(addressId, userId, addressText, "", "", true, new Date());

        mFirestore.collection(FirebaseConstants.COLLECTION_CUSTOMER_ADDRESSES).document(addressId)
                .set(address)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                            navigateToHome();
                        } else {
                            // Even if address fails, the user is created, but we should notify
                            btnRegister.setEnabled(true);
                            btnRegister.setText("Register");
                            Toast.makeText(RegistrationActivity.this, "User created but failed to save address.", Toast.LENGTH_SHORT).show();
                            navigateToHome(); // Still navigate since account exists
                        }
                    }
                });
    }
}
