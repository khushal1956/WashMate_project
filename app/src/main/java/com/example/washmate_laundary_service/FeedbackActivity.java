package com.example.washmate_laundary_service;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.washmate_laundary_service.models.Feedback;
import com.example.washmate_laundary_service.utils.FirebaseConstants;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.UUID;

public class FeedbackActivity extends BaseActivity {

    private RatingBar ratingBar;
    private EditText etComment;
    private MaterialButton btnSubmit;
    private String orderId;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        orderId = getIntent().getStringExtra("ORDER_ID");
        if (orderId == null) {
            Toast.makeText(this, "Order information missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ratingBar = findViewById(R.id.ratingBar);
        etComment = findViewById(R.id.etComment);
        btnSubmit = findViewById(R.id.btnSubmitFeedback);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnSubmit.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login to submit feedback", Toast.LENGTH_SHORT).show();
            return;
        }

        float rating = ratingBar.getRating();
        String comment = etComment.getText().toString().trim();
        String customerId = mAuth.getCurrentUser().getUid();
        String feedbackId = UUID.randomUUID().toString();

        Feedback feedback = new Feedback(feedbackId, orderId, customerId, rating, comment, new Date());

        btnSubmit.setEnabled(false);
        btnSubmit.setText("Submitting...");

        mFirestore.collection("FEEDBACKS").document(feedbackId)
                .set(feedback)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(FeedbackActivity.this, "Thank you for your feedback!", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Submit Review");
                    Toast.makeText(FeedbackActivity.this, "Failed to submit: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
