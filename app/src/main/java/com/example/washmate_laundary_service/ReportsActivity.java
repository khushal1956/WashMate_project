package com.example.washmate_laundary_service;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ReportsActivity extends BaseActivity {

    private TextView tvReportRevenue, tvReportCompleted, tvReportProcessing, tvReportCancelled, tvReportTotalOrders;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        mFirestore = FirebaseFirestore.getInstance();

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        tvReportRevenue = findViewById(R.id.tvReportRevenue);
        tvReportCompleted = findViewById(R.id.tvReportCompleted);
        tvReportProcessing = findViewById(R.id.tvReportProcessing);
        tvReportCancelled = findViewById(R.id.tvReportCancelled);
        tvReportTotalOrders = findViewById(R.id.tvReportTotalOrders);

        fetchReportData();
    }

    private void fetchReportData() {
        android.util.Log.d("Reports", "Fetching data from ORDERS collection");
        mFirestore.collection("ORDERS")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int docCount = queryDocumentSnapshots.size();
                    android.util.Log.d("Reports", "Found " + docCount + " documents");
                    
                    double totalRevenue = 0;
                    int totalCompleted = 0;
                    int totalProcessing = 0;
                    int totalCancelled = 0;
                    int totalOrders = docCount;

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Object amountObj = document.get("totalAmount");
                        double amount = 0;
                        if (amountObj instanceof Number) {
                            amount = ((Number) amountObj).doubleValue();
                        } else if (amountObj instanceof String) {
                             try {
                                amount = Double.parseDouble((String) amountObj);
                            } catch (Exception e) {
                                android.util.Log.e("Reports", "Failed to parse amount string: " + amountObj);
                            }
                        }
                        
                        String status = document.getString("status");

                        // Add to realized revenue only if completed
                        if (status != null && "Completed".equalsIgnoreCase(status)) {
                            totalRevenue += amount;
                        }

                        if ("Completed".equalsIgnoreCase(status)) {
                            totalCompleted++;
                        } else if ("Rejected".equalsIgnoreCase(status) || "Cancelled".equalsIgnoreCase(status)) {
                            totalCancelled++;
                        } else if (status != null && !"Pending".equalsIgnoreCase(status)) {
                            totalProcessing++;
                        }
                    }

                    tvReportRevenue.setText(String.format("₹%.0f", totalRevenue));
                    tvReportCompleted.setText(String.valueOf(totalCompleted));
                    tvReportProcessing.setText(String.valueOf(totalProcessing));
                    tvReportCancelled.setText(String.valueOf(totalCancelled));
                    tvReportTotalOrders.setText(String.valueOf(totalOrders));

                    if (totalOrders == 0) {
                         android.util.Log.w("Reports", "No orders found in database");
                    }
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("Reports", "Failed to fetch report data", e);
                    Toast.makeText(this, "Failed to load reports: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
