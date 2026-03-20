package com.example.washmate_laundary_service;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.washmate_laundary_service.models.PromoItem;

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

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        List<PromoItem> promoList = new ArrayList<>();
        promoList.add(new PromoItem("20% OFF", "Valid on your first laundry order", "WASHFIRST"));
        promoList.add(new PromoItem("FLAT ₹100 OFF", "On orders above ₹500", "WELCOME100"));
        promoList.add(new PromoItem("FREE IRONING", "Get 2 items ironed free with any wash", "IRONFREE"));
        promoList.add(new PromoItem("WEEKEND SPECIAL", "15% discount on all dry cleaning", "WEEKEND15"));

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
