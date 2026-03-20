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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.net.Uri;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.example.washmate_laundary_service.models.Customer;
import com.example.washmate_laundary_service.models.CustomerAddress;
import com.example.washmate_laundary_service.models.Admin;
import com.example.washmate_laundary_service.utils.FirebaseConstants;

import java.util.Date;

public class RegistrationActivity extends BaseActivity {

    private TextInputEditText etFullName, etEmail, etMobile, etAddress, etPassword, etConfirmPassword, etPincode, etCity;
    private TextInputLayout tilFullName, tilEmail, tilMobile, tilAddress, tilPassword, tilConfirmPassword, tilPincode, tilGender, tilCity;
    private AutoCompleteTextView actvGender;
    private ShapeableImageView ivProfilePhoto;
    private Button btnBackStep2, btnNextStep2, btnBackStep3, btnRegister;
    private Button btnNextStep1;
    private TextView tvLogin, tvTitle, tvSubtitle, tvProgressText;
    private View headerBackground, cardRegistration, btnBack, flFormContainer;
    private View llStep1, llStep2, llStep3;
    private LinearProgressIndicator progressRegistration;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private int currentStep = 1;
    private Uri profileImageUri = null;

    private final ActivityResultLauncher<Intent> locationPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String address = result.getData().getStringExtra("address");
                    String city = result.getData().getStringExtra("city");
                    String pincode = result.getData().getStringExtra("pincode");

                    if (etAddress != null && address != null) etAddress.setText(address);
                    if (etCity != null && city != null) etCity.setText(city);
                    if (etPincode != null && pincode != null) etPincode.setText(pincode);
                }
            }
    );

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    profileImageUri = result.getData().getData();
                    if (ivProfilePhoto != null && profileImageUri != null) {
                        ivProfilePhoto.setImageURI(profileImageUri);
                    }
                }
            }
    );

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
        cardRegistration = findViewById(R.id.cardRegistration);
        btnBack = findViewById(R.id.btnBack);
        
        // New Multi-Step Views
        progressRegistration = findViewById(R.id.progressRegistration);
        tvProgressText = findViewById(R.id.tvProgressText);
        flFormContainer = findViewById(R.id.flFormContainer);
        llStep1 = findViewById(R.id.llStep1);
        llStep2 = findViewById(R.id.llStep2);
        llStep3 = findViewById(R.id.llStep3);
        
        btnNextStep1 = findViewById(R.id.btnNextStep1);
        btnBackStep2 = findViewById(R.id.btnBackStep2);
        btnNextStep2 = findViewById(R.id.btnNextStep2);
        btnBackStep3 = findViewById(R.id.btnBackStep3);
        
        // New Fields Binding
        etPincode = findViewById(R.id.etPincode);
        tilPincode = findViewById(R.id.tilPincode);
        etCity = findViewById(R.id.etCity);
        tilCity = findViewById(R.id.tilCity);
        actvGender = findViewById(R.id.actvGender);
        tilGender = findViewById(R.id.tilGender);
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (ivProfilePhoto != null) {
            ivProfilePhoto.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                imagePickerLauncher.launch(intent);
            });
        }

        if (actvGender != null) {
            String[] genders = new String[]{getString(R.string.male), getString(R.string.female), getString(R.string.other)};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item_glass, genders);
            actvGender.setAdapter(adapter);
        }
        
        setupStepNavigation();
        
        if (tilAddress != null) {
            tilAddress.setEndIconOnClickListener(v -> {
                Intent intent = new Intent(RegistrationActivity.this, LocationPickerActivity.class);
                locationPickerLauncher.launch(intent);
            });
        }

        // Animations removed for stability test
        // applyEntranceAnimations();

        if (tvLogin != null) {
            tvLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // Go back to login
                }
            });
        }
        
        updateStepView();
    }

    private void setupStepNavigation() {
        if (btnNextStep1 != null) {
            btnNextStep1.setOnClickListener(v -> {
                if (validateStep1()) {
                    currentStep = 2;
                    updateStepView();
                }
            });
        }

        if (btnBackStep2 != null) {
            btnBackStep2.setOnClickListener(v -> {
                currentStep = 1;
                updateStepView();
            });
        }

        if (btnNextStep2 != null) {
            btnNextStep2.setOnClickListener(v -> {
                if (validateStep2()) {
                    currentStep = 3;
                    updateStepView();
                }
            });
        }

        if (btnBackStep3 != null) {
            btnBackStep3.setOnClickListener(v -> {
                currentStep = 2;
                updateStepView();
            });
        }

        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> {
                if (validateStep3()) {
                    registerUser();
                }
            });
        }
    }

    private void updateStepView() {
        if (llStep1 == null || llStep2 == null || llStep3 == null) return;

        llStep1.setVisibility(currentStep == 1 ? View.VISIBLE : View.GONE);
        llStep2.setVisibility(currentStep == 2 ? View.VISIBLE : View.GONE);
        llStep3.setVisibility(currentStep == 3 ? View.VISIBLE : View.GONE);

        if (progressRegistration != null && tvProgressText != null) {
            switch (currentStep) {
                case 1:
                    progressRegistration.setProgress(20);
                    tvProgressText.setText("Step 1 of 3");
                    break;
                case 2:
                    progressRegistration.setProgress(50);
                    tvProgressText.setText("Step 2 of 3");
                    break;
                case 3:
                    progressRegistration.setProgress(100);
                    tvProgressText.setText("Step 3 of 3");
                    break;
            }
        }
    }

    private void applyEntranceAnimations() {
        // Disabled for stability
    }

    private boolean validateStep1() {
        boolean isValid = true;
        if (tilFullName != null) tilFullName.setError(null);
        if (tilEmail != null) tilEmail.setError(null);
        if (tilGender != null) tilGender.setError(null);

        String fullName = etFullName != null ? etFullName.getText().toString().trim() : "";
        String email = etEmail != null ? etEmail.getText().toString().trim() : "";
        String gender = actvGender != null ? actvGender.getText().toString().trim() : "";

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

        if (TextUtils.isEmpty(gender) || gender.equals(getString(R.string.select_gender))) {
            if (tilGender != null) tilGender.setError("Gender is required");
            isValid = false;
        }

        return isValid;
    }

    private boolean validateStep2() {
        boolean isValid = true;
        if (tilMobile != null) tilMobile.setError(null);
        if (tilAddress != null) tilAddress.setError(null);
        if (tilCity != null) tilCity.setError(null);
        if (tilPincode != null) tilPincode.setError(null);

        String mobile = etMobile != null ? etMobile.getText().toString().trim() : "";
        String address = etAddress != null ? etAddress.getText().toString().trim() : "";
        String city = etCity != null ? etCity.getText().toString().trim() : "";
        String pincode = etPincode != null ? etPincode.getText().toString().trim() : "";

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

        if (TextUtils.isEmpty(city)) {
            if (tilCity != null) tilCity.setError("City is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(pincode)) {
            if (tilPincode != null) tilPincode.setError("Pincode is required");
            isValid = false;
        } else if (pincode.length() < 5) {
            if (tilPincode != null) tilPincode.setError("Enter a valid pincode");
            isValid = false;
        }

        return isValid;
    }

    private boolean validateStep3() {
        boolean isValid = true;
        if (tilPassword != null) tilPassword.setError(null);
        if (tilConfirmPassword != null) tilConfirmPassword.setError(null);

        String password = etPassword != null ? etPassword.getText().toString() : "";
        String confirmPassword = etConfirmPassword != null ? etConfirmPassword.getText().toString() : "";

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
        if (etFullName == null || etEmail == null || etMobile == null || etAddress == null ||
            etPassword == null || etPincode == null || actvGender == null) {
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
        String city = etCity.getText().toString().trim();
        String password = etPassword.getText().toString();
        String pincode = etPincode.getText().toString().trim();
        String gender = actvGender.getText().toString().trim();
        String profileUrl = profileImageUri != null ? profileImageUri.toString() : "";

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
                                saveUserToFirestore(user.getUid(), fullName, email, mobile, address, city, pincode, gender, profileUrl);
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

    private void saveUserToFirestore(String userId, String fullName, String email, String mobile, String address, String city, String pincode, String gender, String profileUrl) {
        saveCustomerToFirestore(userId, fullName, email, mobile, address, city, pincode, gender, profileUrl);
    }

    private void saveCustomerToFirestore(String userId, String fullName, String email, String mobile, String address, String city, String pincode, String gender, String profileUrl) {
        Customer customer = new Customer(userId, fullName, email, "", mobile, gender, profileUrl, "Active", new Date());

        mFirestore.collection(FirebaseConstants.COLLECTION_CUSTOMERS).document(userId)
                .set(customer)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            saveAddressToFirestore(userId, address, city, pincode);
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

    private void saveAddressToFirestore(String userId, String addressText, String city, String pincode) {
        String addressId = mFirestore.collection(FirebaseConstants.COLLECTION_CUSTOMER_ADDRESSES).document().getId();
        CustomerAddress address = new CustomerAddress(addressId, userId, addressText, city, pincode, true, new Date());

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
