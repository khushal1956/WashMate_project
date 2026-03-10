package com.example.washmate_laundary_service;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.washmate_laundary_service.utils.FirebaseConstants;

public class ProfileActivity extends BaseActivity {

    private TextView tvUserName, tvCurrentEmail;
    private TextInputEditText etNewEmail, etPasswordForEmail;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private Button btnUpdateEmail, btnUpdatePassword, btnLogout;
    private ImageButton btnBack;
    private ProgressBar progressBar;
    private BottomNavigationView bottomNavigation;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
        
        etNewEmail = findViewById(R.id.etNewEmail);
        etPasswordForEmail = findViewById(R.id.etPasswordForEmail);
        
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        
        btnUpdateEmail = findViewById(R.id.btnUpdateEmail);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);
        btnLogout = findViewById(R.id.btnLogout);
        btnBack = findViewById(R.id.btnBack);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        
        progressBar = findViewById(R.id.progressBar);
    }

    private void loadUserData() {
        if (currentUser != null) {
            // Set current email
            tvCurrentEmail.setText(currentUser.getEmail());

            // Load user name from Firestore
            String userId = currentUser.getUid();
            mFirestore.collection(FirebaseConstants.COLLECTION_CUSTOMERS)
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String fullName = documentSnapshot.getString("fullName");
                            if (fullName != null && !fullName.isEmpty()) {
                                tvUserName.setText(fullName);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void setClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnUpdateEmail.setOnClickListener(v -> updateEmail());

        btnUpdatePassword.setOnClickListener(v -> updatePassword());

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
        
        findViewById(R.id.cardChangeLanguage).setOnClickListener(v -> showLanguageDialog());
    }

    private void showLanguageDialog() {
        final String[] languages = {"English", "हिंदी", "ગુજરાતી", "मराठी"};
        final String[] langCodes = {"en", "hi", "gu", "mr"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.select_language));
        builder.setSingleChoiceItems(languages, -1, (dialog, which) -> {
            setLocale(langCodes[which]);
            dialog.dismiss();
        });
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void setLocale(String lang) {
        com.example.washmate_laundary_service.utils.LocaleHelper.setLocale(this, lang);
        
        // Restart app to apply changes
        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finishAffinity(); // Close all activities
    }

    private void updateEmail() {
        String newEmail = etNewEmail.getText().toString().trim();
        String password = etPasswordForEmail.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(newEmail)) {
            Toast.makeText(this, "Please enter new email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your current password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress(true);

        // Re-authenticate user before changing email
        String currentEmail = currentUser.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, password);

        currentUser.reauthenticate(credential)
                .addOnSuccessListener(aVoid -> {
                    // Update email
                    currentUser.updateEmail(newEmail)
                            .addOnSuccessListener(aVoid1 -> {
                                showProgress(false);
                                tvCurrentEmail.setText(newEmail);
                                etNewEmail.setText("");
                                etPasswordForEmail.setText("");
                                Toast.makeText(this, "Email updated successfully!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                showProgress(false);
                                Toast.makeText(this, "Failed to update email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    Toast.makeText(this, "Authentication failed. Check your password.", Toast.LENGTH_SHORT).show();
                });
    }

    private void updatePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(this, "Please enter current password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "Please enter new password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress(true);

        // Re-authenticate user before changing password
        String email = currentUser.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);

        currentUser.reauthenticate(credential)
                .addOnSuccessListener(aVoid -> {
                    // Update password
                    currentUser.updatePassword(newPassword)
                            .addOnSuccessListener(aVoid1 -> {
                                showProgress(false);
                                etCurrentPassword.setText("");
                                etNewPassword.setText("");
                                etConfirmPassword.setText("");
                                Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                showProgress(false);
                                Toast.makeText(this, "Failed to update password: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                });
    }

    private void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            btnUpdateEmail.setEnabled(false);
            btnUpdatePassword.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnUpdateEmail.setEnabled(true);
            btnUpdatePassword.setEnabled(true);
        }
    }
    
    private void setupBottomNavigation() {
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_profile);
            bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(this, CustomerDashboardActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_orders) {
                    startActivity(new Intent(this, CustomerOrdersActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    // Already on profile
                    return true;
                } else if (itemId == R.id.nav_support) {
                    startActivity(new Intent(this, SupportActivity.class));
                    finish();
                    return true;
                }
                return false;
            });
        }
    }
}
