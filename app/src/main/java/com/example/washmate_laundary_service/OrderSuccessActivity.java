package com.example.washmate_laundary_service;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;

public class OrderSuccessActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        String orderId = getIntent().getStringExtra("ORDER_ID");
        double amount = getIntent().getDoubleExtra("TOTAL_AMOUNT", 0.0);
        String paymentMethod = getIntent().getStringExtra("PAYMENT_METHOD");

        TextView tvOrderId = findViewById(R.id.tvOrderId);
        TextView tvTotalPaid = findViewById(R.id.tvTotalPaid);
        TextView tvPaymentMethod = findViewById(R.id.tvPaymentMethod);

        if (orderId != null) tvOrderId.setText("#" + orderId);
        tvTotalPaid.setText("₹" + String.format("%.2f", amount));
        if (paymentMethod != null) tvPaymentMethod.setText(paymentMethod);

        MaterialButton btnGoHome = findViewById(R.id.btnGoHome);
        btnGoHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomerDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        MaterialButton btnTrackOrder = findViewById(R.id.btnTrackOrder);
        btnTrackOrder.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomerOrdersActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
