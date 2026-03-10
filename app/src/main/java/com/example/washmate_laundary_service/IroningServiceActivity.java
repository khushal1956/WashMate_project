package com.example.washmate_laundary_service;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class IroningServiceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ironing_service);

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Setup click listeners for service cards
        setupServiceCardListeners();
    }

    private void setupServiceCardListeners() {
        // Regular Ironing card
        MaterialCardView cardRegularIroning = findViewById(R.id.cardRegularIroning);
        if (cardRegularIroning != null) {
            cardRegularIroning.setOnClickListener(v -> launchOrderActivity("Regular Ironing", 30.0));
        }

        // Premium Ironing card
        MaterialCardView cardPremiumIroning = findViewById(R.id.cardPremiumIroning);
        if (cardPremiumIroning != null) {
            cardPremiumIroning.setOnClickListener(v -> launchOrderActivity("Premium Ironing", 50.0));
        }
    }

    private void launchOrderActivity(String serviceName, double price) {
        Intent intent = new Intent(this, ClothingSelectionActivity.class);
        // intent.putExtra("SERVICE_NAME", serviceName);
        // intent.putExtra("SERVICE_TYPE", "ironing");
        // intent.putExtra("SERVICE_PRICE", price);
        startActivity(intent);
    }
}
