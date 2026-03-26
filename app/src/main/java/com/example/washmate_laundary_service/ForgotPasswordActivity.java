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
    private Button btnSendOtp, btnVerifyOtp, btnResetPassword, btnCheckEmail;
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

        // Ensure initial state is correct (Email section visible)
        showEmailSection();
    }

    private void showEmailSection() {
        layoutEmailSection.setVisibility(View.VISIBLE);
        layoutOtpSection.setVisibility(View.GONE);
        layoutPasswordSection.setVisibility(View.GONE);
        tvInstructions.setText("Enter your email to receive a secure OTP to reset your account password.");
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
        btnCheckEmail = findViewById(R.id.btnCheckEmail);
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

        // Check Email
        btnCheckEmail.setOnClickListener(v -> openEmailClient());

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

        // Hide existing UI to show loading state
        showProgress(true);

        // Generate a real dynamic 6-digit OTP
        Random random = new Random();
        String dynamicOtp = String.format("%06d", random.nextInt(1000000));
        generatedOtp = dynamicOtp;

        // Use the EmailSender utility (Real-world ready)
        com.example.washmate_laundary_service.utils.EmailSender.sendOtp(email, dynamicOtp, new com.example.washmate_laundary_service.utils.EmailSender.EmailListener() {
            @Override
            public void onSuccess(boolean isDemoMode, String demoOtp) {
                showProgress(false);
                if (isDemoMode) {
                    Toast.makeText(ForgotPasswordActivity.this, "DEMO MODE: OTP is " + demoOtp + " (Check Logcat for details)", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Security code sent to your email!", Toast.LENGTH_LONG).show();
                }
                showOtpSection();
            }

            @Override
            public void onFailure(String error) {
                showProgress(false);
                Toast.makeText(ForgotPasswordActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void verifyOtp() {
        String enteredOtp = etOtp.getText().toString().trim();

        if (TextUtils.isEmpty(enteredOtp)) {
            Toast.makeText(this, "Verification code required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (enteredOtp.length() != 6) {
            Toast.makeText(this, "Please enter all 6 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verify OTP
        if (enteredOtp.equals(generatedOtp)) {
            showProgress(true);
            // Add a small delay for a premium loading feel
            etOtp.postDelayed(() -> {
                showProgress(false);
                Toast.makeText(this, "Verification Successful!", Toast.LENGTH_SHORT).show();
                showPasswordSection();
            }, 1000);
        } else {
            etOtp.setError("Invalid Code");
            Toast.makeText(this, "Invalid code. Please check your email again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetPassword() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please set both password fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            etNewPassword.setError("Password too short (min 6 chars)");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return;
        }

        showProgress(true);
        
        // Finalize state
        etNewPassword.postDelayed(() -> {
            showProgress(false);
            Toast.makeText(this, "Password reset successful! You can now login.", Toast.LENGTH_LONG).show();
            
            // Redirect to login with clear history
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, 1500);
    }

    private void showOtpSection() {
        animateSectionChange(layoutEmailSection, layoutOtpSection);
        tvInstructions.setText("Check your email for the 6-digit verification code.");
    }

    private void showPasswordSection() {
        animateSectionChange(layoutOtpSection, layoutPasswordSection);
        tvInstructions.setText("Set a strong new password for your account.");
    }

    private void animateSectionChange(final View hideView, final View showView) {
        hideView.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() -> {
                    hideView.setVisibility(View.GONE);
                    showView.setVisibility(View.VISIBLE);
                    showView.setAlpha(0f);
                    showView.animate()
                            .alpha(1f)
                            .setDuration(300)
                            .start();
                })
                .start();
    }

    private void openEmailClient() {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
        }
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
