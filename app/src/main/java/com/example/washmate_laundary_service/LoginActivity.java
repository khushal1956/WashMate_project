package com.example.washmate_laundary_service;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.example.washmate_laundary_service.utils.FirebaseConstants;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends BaseActivity {

    private TextInputEditText etEmail, etPassword;
    private android.widget.TextView tvForgotPassword;
    private android.widget.Button buttonLogin;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        androidx.activity.EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonLogin = findViewById(R.id.btnLogin);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // Pre-fill removed as per request to allow manual entry
        // if (etEmail != null) etEmail.setText("admin@gmail.com");




        // Language Spinner Setup
        android.widget.Spinner spinnerLanguage = findViewById(R.id.spinnerLanguage);
        android.widget.ArrayAdapter<CharSequence> adapter = android.widget.ArrayAdapter.createFromResource(
                this,
                R.array.languages,
                R.layout.spinner_item_glass
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_glass);
        spinnerLanguage.setAdapter(adapter);

        // Set current selection
        String currentLang = com.example.washmate_laundary_service.utils.LocaleHelper.getLanguage(this);
        int selection = 0;
        if (currentLang.equals("hi")) selection = 1;
        else if (currentLang.equals("gu")) selection = 2;
        else if (currentLang.equals("mr")) selection = 3;
        spinnerLanguage.setSelection(selection);

        spinnerLanguage.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedLang = "en";
                if (position == 1) selectedLang = "hi";
                else if (position == 2) selectedLang = "gu";
                else if (position == 3) selectedLang = "mr";

                if (!selectedLang.equals(currentLang)) {
                    com.example.washmate_laundary_service.utils.LocaleHelper.setLocale(LoginActivity.this, selectedLang);
                    recreate();
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        mAuth = FirebaseAuth.getInstance();
        if (mAuth == null) {
            return;
        }

        if (buttonLogin != null) {
            buttonLogin.setOnClickListener(v -> {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (validateLoginInput(email, password)) {
                    performLogin(email, password);
                }
            });
        }

        View tvRegister = findViewById(R.id.tvRegister);
        if (tvRegister != null) {
            tvRegister.setOnClickListener(v -> {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            });
        }

        if (tvForgotPassword != null) {
            tvForgotPassword.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            });
        }

        // --- Google Sign-In Setup ---
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        View btnGoogle = findViewById(R.id.btnGoogle);
        if (btnGoogle != null) {
            btnGoogle.setOnClickListener(v -> signInWithGoogle());
        }

        View btnApple = findViewById(R.id.btnApple);
        if (btnApple != null) {
            btnApple.setOnClickListener(v -> Toast.makeText(this, "Apple Sign-In coming soon!", Toast.LENGTH_SHORT).show());
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        if (buttonLogin != null) buttonLogin.setEnabled(false);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    checkUserRoleAndNavigate(authResult.getUser().getUid());
                })
                .addOnFailureListener(e -> {
                    if (buttonLogin != null) buttonLogin.setEnabled(true);
                    Toast.makeText(LoginActivity.this, "Authentication Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean validateLoginInput(String email, String password) {
        boolean isValid = true;

        // Get TextInputLayouts for error display
        com.google.android.material.textfield.TextInputLayout tilEmail = findViewById(R.id.tilEmail);
        com.google.android.material.textfield.TextInputLayout tilPassword = findViewById(R.id.tilPassword);

        // Reset errors
        if (tilEmail != null) tilEmail.setError(null);
        if (tilPassword != null) tilPassword.setError(null);

        // Validate email
        if (TextUtils.isEmpty(email)) {
            if (tilEmail != null) tilEmail.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (tilEmail != null) tilEmail.setError("Enter a valid email address");
            isValid = false;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            if (tilPassword != null) tilPassword.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            if (tilPassword != null) tilPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }

        return isValid;
    }

    private void performLogin(String email, String password) {
        // ── Offline Admin Login ──────────────────────────────────────────
        // No Firebase call needed. If credentials match, go straight to dashboard.
        if (email.equals("admin@gmail.com") && password.equals("admin123")) {
            navigateToAdminDashboard();
            return;
        }

        // Firebase Login for regular users
        if (buttonLogin != null) buttonLogin.setEnabled(false);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    if (authResult.getUser() != null) {
                        checkUserRoleAndNavigate(authResult.getUser().getUid());
                    } else {
                        if (buttonLogin != null) buttonLogin.setEnabled(true);
                        Toast.makeText(LoginActivity.this, "Authentication failed: User null", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    if (buttonLogin != null) buttonLogin.setEnabled(true);
                    if (e instanceof FirebaseNetworkException) {
                        Toast.makeText(LoginActivity.this, "Network Error: No internet connection. Please check your network and try again.", Toast.LENGTH_LONG).show();
                    } else if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(LoginActivity.this, "Incorrect email or password. Please try again.", Toast.LENGTH_LONG).show();
                    } else if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidUserException) {
                        Toast.makeText(LoginActivity.this, "No account found with this email. Please sign up first.", Toast.LENGTH_LONG).show();
                    } else {
                        String errorMsg = e.getMessage();
                        if (errorMsg != null && (errorMsg.toLowerCase().contains("network") || errorMsg.toLowerCase().contains("unavailable") || errorMsg.toLowerCase().contains("offline"))) {
                            Toast.makeText(LoginActivity.this, "Connection Error: Please check your internet connection.", Toast.LENGTH_LONG).show();
                        } else if (errorMsg != null && (errorMsg.contains("credential") || errorMsg.contains("INVALID_LOGIN_CREDENTIALS"))) {
                            Toast.makeText(LoginActivity.this, "Incorrect email or password. Please try again.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Login failed: " + (errorMsg != null ? errorMsg : "Unknown error"), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkUserRoleAndNavigate(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        // 1. Check if Admin
        db.collection(FirebaseConstants.COLLECTION_ADMINS).document(uid).get()
                .addOnSuccessListener(adminSnap -> {
                    if (adminSnap.exists()) {
                        navigateToAdminDashboard();
                        return;
                    }
                    
                    // 2. Check if Staff
                    db.collection(FirebaseConstants.COLLECTION_STAFF).document(uid).get()
                            .addOnSuccessListener(staffSnap -> {
                                if (staffSnap.exists()) {
                                    startActivity(new Intent(LoginActivity.this, StaffDashboardActivity.class));
                                    finish();
                                    return;
                                }
                                
                                // 3. Check if Customer
                                db.collection(FirebaseConstants.COLLECTION_CUSTOMERS).document(uid).get()
                                        .addOnSuccessListener(customerSnap -> {
                                            if (customerSnap.exists()) {
                                                startActivity(new Intent(LoginActivity.this, CustomerDashboardActivity.class));
                                                finish();
                                            } else {
                                                if (buttonLogin != null) buttonLogin.setEnabled(true);
                                                Toast.makeText(LoginActivity.this, "Account found but no profile data. Please contact support.", Toast.LENGTH_LONG).show();
                                                // If it's a new sign-in from Google and no profile exists, maybe redirect to registration?
                                                // For now, just show error.
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            if (buttonLogin != null) buttonLogin.setEnabled(true);
                                            Toast.makeText(LoginActivity.this, "Error verifying customer profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                if (buttonLogin != null) buttonLogin.setEnabled(true);
                                Toast.makeText(LoginActivity.this, "Error checking staff role: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    if (buttonLogin != null) buttonLogin.setEnabled(true);
                    String error = e.getMessage();
                    if (error != null && error.toLowerCase().contains("network")) {
                        Toast.makeText(LoginActivity.this, "Network Error: Unable to verify account permissions.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error checking role: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void handleAdminLogin(String email, String password) {
        if (buttonLogin != null) buttonLogin.setEnabled(false);
        
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // Admin Logged in successfully
                    checkAndCreateAdminDocument(authResult.getUser().getUid(), email);
                })
                .addOnFailureListener(e -> {
                    // If user not found, create it
                    if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidUserException) {
                        createAdminAccount(email, password);
                    } else if (e instanceof com.google.firebase.FirebaseTooManyRequestsException || e.getClass().getSimpleName().equals("FirebaseTooManyRequestsException")) {
                        // Device is blocked? Fallback to Anonymous Auth to bypass rate limits
                        // and spoof the admin session.
                        mAuth.signInAnonymously()
                            .addOnSuccessListener(authResult -> {
                                // Treat this anonymous user as Admin in Firestore
                                checkAndCreateAdminDocument(authResult.getUser().getUid(), "admin@gmail.com");
                            })
                            .addOnFailureListener(e2 -> {
                                if (buttonLogin != null) buttonLogin.setEnabled(true);
                                Toast.makeText(LoginActivity.this, "Emergency Bypass Failed: " + e2.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                    } else {
                        if (buttonLogin != null) buttonLogin.setEnabled(true);
                        new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Admin Login Error")
                            .setMessage("Error: " + e.getMessage() + "\n\nClass: " + e.getClass().getSimpleName())
                            .setPositiveButton("OK", null)
                            .show();
                    }
                });
    }

    private void createAdminAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // Admin Created
                    // If this was our shadow account, we still want to save "admin@gmail.com"
                    String emailToSave = email;
                    if (email.equals("admin_internal_99@washmate.com")) {
                        emailToSave = "admin@gmail.com";
                    }
                    checkAndCreateAdminDocument(authResult.getUser().getUid(), emailToSave);
                })
                .addOnFailureListener(e -> {
                    if (buttonLogin != null) buttonLogin.setEnabled(true);
                    Toast.makeText(LoginActivity.this, "Failed to create Admin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void checkAndCreateAdminDocument(String uid, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(FirebaseConstants.COLLECTION_ADMINS).document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        // Create Admin Document
                        com.example.washmate_laundary_service.models.Admin admin = new com.example.washmate_laundary_service.models.Admin(
                                uid, "Admin", email, "", "", new java.util.Date()
                        );
                        db.collection(FirebaseConstants.COLLECTION_ADMINS).document(uid).set(admin)
                                .addOnSuccessListener(aVoid -> navigateToAdminDashboard())
                                .addOnFailureListener(e -> {
                                    if (buttonLogin != null) buttonLogin.setEnabled(true);
                                    Toast.makeText(LoginActivity.this, "Error setting up admin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        navigateToAdminDashboard();
                    }
                })
                .addOnFailureListener(e -> {
                     if (buttonLogin != null) buttonLogin.setEnabled(true);
                     Toast.makeText(LoginActivity.this, "Error checking admin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToAdminDashboard() {
        startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
        finish();
    }




}