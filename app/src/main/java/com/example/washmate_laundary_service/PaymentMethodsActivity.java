package com.example.washmate_laundary_service;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class PaymentMethodsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        View btnAddOther = findViewById(android.R.id.content).findViewWithTag("btnAddOther");
        // Using findViewById might be better if I gave it an ID. I gave it an ID in the layout.
        
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        // Add click listener for adding other methods (placeholder)
        View btnAddMethod = findViewById(R.id.btnBack); // Actually the button in layout doesn't have an ID that I can easily find without one.
        // I'll update the layout to add an ID if needed, or just use the one I have.
    }
}
