package com.example.washmate_laundary_service;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class WashingServiceActivity extends BaseActivity {

    private static final String TAG = "WashingServiceActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_washing_service);
        
        Log.d(TAG, "WashingServiceActivity created");

        // Back button
        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Service cards
        MaterialCardView cardRegularWash = findViewById(R.id.cardRegularWash);
        MaterialCardView cardDelicateWash = findViewById(R.id.cardDelicateWash);
        MaterialCardView cardExpressWash = findViewById(R.id.cardExpressWash);

        // Regular Wash click listener
        if (cardRegularWash != null) {
            cardRegularWash.setOnClickListener(v -> {
                try {
                    Log.d(TAG, "Regular Wash clicked, launching ClothingSelectionActivity");
                    Intent intent = new Intent(WashingServiceActivity.this, ClothingSelectionActivity.class);
                    intent.putExtra("SERVICE_NAME", "Regular Wash");
                    intent.putExtra("SERVICE_TYPE", "washing");
                    intent.putExtra("SERVICE_PRICE", 100.0); // Base price
                    startActivity(intent);
                    Log.d(TAG, "ClothingSelectionActivity started successfully");
                } catch (Exception e) {
                    Log.e(TAG, "Error launching OrderActivity", e);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Log.e(TAG, "cardRegularWash is null!");
        }

        // Delicate Wash click listener
        if (cardDelicateWash != null) {
            cardDelicateWash.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(WashingServiceActivity.this, ClothingSelectionActivity.class);
                    intent.putExtra("SERVICE_NAME", "Delicate Wash"); 
                    intent.putExtra("SERVICE_TYPE", "washing");
                    intent.putExtra("SERVICE_PRICE", 120.0); 
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error launching OrderActivity", e);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

        // Express Wash click listener
        if (cardExpressWash != null) {
            cardExpressWash.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(WashingServiceActivity.this, ClothingSelectionActivity.class);
                    intent.putExtra("SERVICE_NAME", "Express Wash");
                    intent.putExtra("SERVICE_TYPE", "washing");
                    intent.putExtra("SERVICE_PRICE", 150.0);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error launching OrderActivity", e);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
