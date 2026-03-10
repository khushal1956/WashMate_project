package com.example.washmate_laundary_service;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class PremiumServiceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_service);

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Setup click listeners for service cards
        setupServiceCardListeners();
    }

    private void setupServiceCardListeners() {
        // Premium Wash & Fold card
        MaterialCardView cardPremiumWashFold = findViewById(R.id.cardPremiumWashFold);
        if (cardPremiumWashFold != null) {
            cardPremiumWashFold.setOnClickListener(v -> launchOrderActivity("Premium Wash & Fold", 150.0));
        }

        // Designer Garment Care card
        MaterialCardView cardDesignerGarment = findViewById(R.id.cardDesignerGarment);
        if (cardDesignerGarment != null) {
            cardDesignerGarment.setOnClickListener(v -> launchOrderActivity("Designer Garment Care", 300.0));
        }

        // Express Premium Service card
        MaterialCardView cardExpressPremium = findViewById(R.id.cardExpressPremium);
        if (cardExpressPremium != null) {
            cardExpressPremium.setOnClickListener(v -> launchOrderActivity("Express Premium Service", 250.0));
        }
    }

    private void launchOrderActivity(String serviceName, double price) {
        Intent intent = new Intent(this, ClothingSelectionActivity.class);
        // intent.putExtra("SERVICE_NAME", serviceName);
        // intent.putExtra("SERVICE_TYPE", "premium");
        // intent.putExtra("SERVICE_PRICE", price);
        startActivity(intent);
    }
}
