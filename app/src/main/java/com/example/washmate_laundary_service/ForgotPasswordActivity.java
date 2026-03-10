package com.example.washmate_laundary_service;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Random;

public class ForgotPasswordActivity extends BaseActivity {

    private TextInputEditText etEmail, etOtp, etNewPassword, etConfirmPassword;
    private Button btnSendOtp, btnVerifyOtp, btnResetPassword;
    private TextView tvInstructions, tvResendOtp;
    private ImageButton btnBack;
    private LinearLayout layoutEmailSection, layoutOtpSection, layoutPasswordSection;
    private ProgressBar progressBar;
    
    private FirebaseAuth mAuth;
    private String generatedOtp;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();

        // Initialize Views
        initializeViews();

        // Set Click Listeners
        setClickListeners();
    }

    private void initializeViews() {
        // Input fields
        etEmail = findViewById(R.id.etEmail);
        etOtp = findViewById(R.id.etOtp);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        // Buttons
        btnSendOtp = findViewById(R.id.btnSendOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnBack = findViewById(R.id.btnBack);

        // TextViews
        tvInstructions = findViewById(R.id.tvInstructions);
        tvResendOtp = findViewById(R.id.tvResendOtp);

        // Layouts
        layoutEmailSection = findViewById(R.id.layoutEmailSection);
        layoutOtpSection = findViewById(R.id.layoutOtpSection);
        layoutPasswordSection = findViewById(R.id.layoutPasswordSection);

        // Progress Bar
        progressBar = findViewById(R.id.progressBar);
    }

    private void setClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Send OTP button
        btnSendOtp.setOnClickListener(v -> sendOtp());

        // Verify OTP button
        btnVerifyOtp.setOnClickListener(v -> verifyOtp());

        // Resend OTP
        tvResendOtp.setOnClickListener(v -> sendOtp());

        // Reset Password button
        btnResetPassword.setOnClickListener(v -> resetPassword());
    }

    private void sendOtp() {
        String email = etEmail.getText().toString().trim();

        // Validate email
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        userEmail = email;

        // Show progress
        showProgress(true);

        // Send password reset email directly
        // Firebase will handle checking if the email exists
        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> {
                    showProgress(false);
                    Toast.makeText(this, "Password reset link sent to " + userEmail, Toast.LENGTH_LONG).show();
                    
                    // Generate OTP for demo purposes
                    Random random = new Random();
                    generatedOtp = String.format("%06d", random.nextInt(1000000));
                    Toast.makeText(this, "Demo OTP: " + generatedOtp, Toast.LENGTH_LONG).show();
                    
                    showOtpSection();
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    // Firebase will return an error if email doesn't exist
                    String errorMessage = e.getMessage();
                    if (errorMessage != null && errorMessage.contains("no user record")) {
                        Toast.makeText(this, "Email not registered", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void verifyOtp() {
        String enteredOtp = etOtp.getText().toString().trim();

        if (TextUtils.isEmpty(enteredOtp)) {
            Toast.makeText(this, "Please enter OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        if (enteredOtp.length() != 6) {
            Toast.makeText(this, "OTP must be 6 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verify OTP
        if (enteredOtp.equals(generatedOtp)) {
            Toast.makeText(this, "OTP verified successfully!", Toast.LENGTH_SHORT).show();
            showPasswordSection();
        } else {
            Toast.makeText(this, "Invalid OTP. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetPassword() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate passwords
        if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
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

        // Show progress
        showProgress(true);

        // Sign in with email and temporary password, then update password
        // Note: This is a workaround. In production, you'd use Firebase Admin SDK or custom backend
        // For now, we'll use Firebase's password reset flow
        
        Toast.makeText(this, "Please use the password reset link sent to your email", Toast.LENGTH_LONG).show();
        showProgress(false);
        
        // Redirect to login
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void showOtpSection() {
        layoutEmailSection.setVisibility(View.GONE);
        layoutOtpSection.setVisibility(View.VISIBLE);
        layoutPasswordSection.setVisibility(View.GONE);
        tvInstructions.setText("Enter the OTP sent to your email");
    }

    private void showPasswordSection() {
        layoutEmailSection.setVisibility(View.GONE);
        layoutOtpSection.setVisibility(View.GONE);
        layoutPasswordSection.setVisibility(View.VISIBLE);
        tvInstructions.setText("Create a new password");
    }

    private void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            btnSendOtp.setEnabled(false);
            btnVerifyOtp.setEnabled(false);
            btnResetPassword.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnSendOtp.setEnabled(true);
            btnVerifyOtp.setEnabled(true);
            btnResetPassword.setEnabled(true);
        }
    }
}
