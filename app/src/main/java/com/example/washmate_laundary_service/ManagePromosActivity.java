package com.example.washmate_laundary_service;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.washmate_laundary_service.models.PromoItem;
import com.example.washmate_laundary_service.utils.FirebaseConstants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ManagePromosActivity extends BaseActivity {

    private RecyclerView rvPromoList;
    private PromosAdapter adapter;
    private List<PromoItem> promoList;
    private FirebaseFirestore db;
    private LinearLayout llEmptyState;
    private FloatingActionButton fabAddPromo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_promos);

        db = FirebaseFirestore.getInstance();
        promoList = new ArrayList<>();

        rvPromoList = findViewById(R.id.rvPromoList);
        llEmptyState = findViewById(R.id.llEmptyState);
        fabAddPromo = findViewById(R.id.fabAddPromo);
        ImageButton btnBack = findViewById(R.id.btnBack);

        rvPromoList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PromosAdapter(promoList, true, promo -> {
            new AlertDialog.Builder(this, R.style.GlassDialogTheme)
                    .setTitle("Delete Promotion")
                    .setMessage("Are you sure you want to delete this promotion?")
                    .setPositiveButton("Delete", (dialog, which) -> deletePromo(promo))
                    .setNegativeButton("Cancel", null)
                    .show();
        });
        rvPromoList.setAdapter(adapter);

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        if (fabAddPromo != null) {
            fabAddPromo.setOnClickListener(v -> showAddPromoDialog());
        }

        fetchPromos();
    }

    private void fetchPromos() {
        db.collection(FirebaseConstants.COLLECTION_PROMOTIONS)
                .orderBy("title", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        promoList.clear();
                        promoList.addAll(value.toObjects(PromoItem.class));
                        if (promoList.isEmpty()) {
                            llEmptyState.setVisibility(View.VISIBLE);
                            rvPromoList.setVisibility(View.GONE);
                        } else {
                            llEmptyState.setVisibility(View.GONE);
                            rvPromoList.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void showAddPromoDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_promo, null);
        EditText etTitle = view.findViewById(R.id.etPromoTitle);
        EditText etDesc = view.findViewById(R.id.etPromoDesc);
        EditText etCode = view.findViewById(R.id.etPromoCode);
        EditText etDiscountValue = view.findViewById(R.id.etPromoDiscountValue);
        com.google.android.material.textfield.MaterialAutoCompleteTextView etDiscountType = view.findViewById(R.id.etPromoDiscountType);

        // Setup Dropdown
        String[] types = new String[]{"PERCENT", "FLAT"};
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, types);
        etDiscountType.setAdapter(adapter);


        new AlertDialog.Builder(this, R.style.GlassDialogTheme)
                .setTitle("Add New Promotion")
                .setView(view)
                .setPositiveButton("Add", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String desc = etDesc.getText().toString().trim();
                    String code = etCode.getText().toString().trim();
                    String discountValStr = etDiscountValue.getText().toString().trim();
                    String discountType = etDiscountType.getText().toString().trim();

                    double discountValue = 0.0;
                    try {
                        if (!TextUtils.isEmpty(discountValStr)) {
                            discountValue = Double.parseDouble(discountValStr);
                        }
                    } catch (NumberFormatException ignored) {}

                    if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(code)) {
                        savePromo(title, desc, code, discountValue, discountType);
                    } else {

                        Toast.makeText(this, "Title and Code are required", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void savePromo(String title, String desc, String code, double discountValue, String discountType) {
        String id = db.collection(FirebaseConstants.COLLECTION_PROMOTIONS).document().getId();
        PromoItem promo = new PromoItem(id, title, desc, code, discountValue, discountType);


        db.collection(FirebaseConstants.COLLECTION_PROMOTIONS).document(id)
                .set(promo)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Promotion added!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void deletePromo(PromoItem promo) {
        if (promo.getId() == null) return;
        db.collection(FirebaseConstants.COLLECTION_PROMOTIONS).document(promo.getId())
                .delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Promotion deleted", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error deleting: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
