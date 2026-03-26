package com.example.washmate_laundary_service;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.washmate_laundary_service.models.PromoItem;
import com.example.washmate_laundary_service.utils.FirebaseConstants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class PromosActivity extends BaseActivity {

    private RecyclerView rvPromos;
    private LinearLayout emptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promos);

        rvPromos = findViewById(R.id.rvPromos);
        emptyState = findViewById(R.id.emptyState);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        fetchPromosFromFirestore();
    }

    private void fetchPromosFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(FirebaseConstants.COLLECTION_PROMOTIONS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<PromoItem> promoList = queryDocumentSnapshots.toObjects(PromoItem.class);
                    updateUI(promoList);
                })
                .addOnFailureListener(e -> {
                    // Fallback to empty state on error
                    updateUI(new ArrayList<>());
                });
    }

    private void updateUI(List<PromoItem> promoList) {
        if (promoList.isEmpty()) {
            rvPromos.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            rvPromos.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
            
            rvPromos.setLayoutManager(new LinearLayoutManager(this));
            rvPromos.setAdapter(new PromosAdapter(promoList));
        }
    }
}
