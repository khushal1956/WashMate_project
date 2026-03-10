package com.example.washmate_laundary_service;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class DryCleaningServiceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dry_cleaning_service);

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Setup click listeners for service cards
        setupServiceCardListeners();
    }

    private void setupServiceCardListeners() {
        // Suit Dry Clean card
        MaterialCardView cardSuitDryClean = findViewById(R.id.cardSuitDryClean);
        if (cardSuitDryClean != null) {
            cardSuitDryClean.setOnClickListener(v -> launchOrderActivity("Suit Dry Clean", 200.0));
        }

        // Dress Dry Clean card
        MaterialCardView cardDressDryClean = findViewById(R.id.cardDressDryClean);
        if (cardDressDryClean != null) {
            cardDressDryClean.setOnClickListener(v -> launchOrderActivity("Dress Dry Clean", 150.0));
        }
    }

    private void launchOrderActivity(String serviceName, double price) {
        Intent intent = new Intent(this, ClothingSelectionActivity.class);
        // intent.putExtra("SERVICE_NAME", serviceName);
        // intent.putExtra("SERVICE_TYPE", "dry_cleaning");
        // intent.putExtra("SERVICE_PRICE", price);
        startActivity(intent);
    }
}
