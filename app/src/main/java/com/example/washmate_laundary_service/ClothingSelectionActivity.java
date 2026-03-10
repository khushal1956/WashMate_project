package com.example.washmate_laundary_service;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.washmate_laundary_service.models.CartItem;
import com.example.washmate_laundary_service.models.ClothingItem;
import com.example.washmate_laundary_service.models.ServiceItem;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClothingSelectionActivity extends BaseActivity {

    private RecyclerView rvClothingItems;
    private TextView tvCartCount, tvCartTotal;
    private MaterialButton btnContinue;
    private View btnBack;
    
    private List<ClothingItem> clothingList = new ArrayList<>();
    private List<ServiceItem> availableServices = new ArrayList<>(); // from Firestore
    private List<CartItem> cart = new ArrayList<>();
    
    // Mock Data for Clothes
    private final String[] CLOTHING_NAMES = {"Shirt", "T-Shirt", "Jeans", "Trousers", "Saree", "Jacket", "Bedsheet", "Towel"};
    private final int[] CLOTHING_ICONS = {
            R.drawable.ic_cloth_shirt,
            R.drawable.ic_cloth_tshirt,
            R.drawable.ic_cloth_jeans,
            R.drawable.ic_cloth_trousers,
            R.drawable.ic_cloth_saree,
            R.drawable.ic_cloth_jacket,
            R.drawable.ic_cloth_bedsheet,
            R.drawable.ic_cloth_towel
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothing_selection);

        rvClothingItems = findViewById(R.id.rvClothingItems);
        tvCartCount = findViewById(R.id.tvCartCount);
        tvCartTotal = findViewById(R.id.tvCartTotal);
        btnContinue = findViewById(R.id.btnContinue);
        btnBack = findViewById(R.id.btnBack);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        setupRecyclerView();
        loadServicesFromFirestore();
        updateCartUI();
        
        btnContinue.setOnClickListener(v -> proceedToCheckout());
    }

    private void setupRecyclerView() {
        // Populate mock data
        clothingList.clear();
        for (int i = 0; i < CLOTHING_NAMES.length; i++) {
            clothingList.add(new ClothingItem(CLOTHING_NAMES[i], CLOTHING_ICONS[i]));
        }

        ClothingAdapter adapter = new ClothingAdapter(clothingList, this::openServiceConfigurationDialog);
        rvClothingItems.setLayoutManager(new GridLayoutManager(this, 2));
        rvClothingItems.setAdapter(adapter);
    }

    private void loadServicesFromFirestore() {
        FirebaseFirestore.getInstance().collection("LAUNDRY_SERVICES")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    availableServices.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("serviceName");
                        Double price = doc.getDouble("price");
                        if (name != null && price != null) {
                            availableServices.add(new ServiceItem(doc.getId(), name, price));
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load service prices", Toast.LENGTH_SHORT).show();
                });
    }

    private void openServiceConfigurationDialog(ClothingItem item) {
        if (availableServices.isEmpty()) {
            Toast.makeText(this, "Loading services...", Toast.LENGTH_SHORT).show();
            return; // Wait for services to load
        }

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.dialog_service_options);

        TextView tvTitle = dialog.findViewById(R.id.tvDialogTitle);
        ChipGroup chipGroup = dialog.findViewById(R.id.chipGroupServices);
        TextView tvQuantity = dialog.findViewById(R.id.tvDialogQuantity);
        ImageButton btnDecrease = dialog.findViewById(R.id.btnDialogDecrease);
        ImageButton btnIncrease = dialog.findViewById(R.id.btnDialogIncrease);
        MaterialButton btnAddToCart = dialog.findViewById(R.id.btnAddToCart);

        if (tvTitle != null) tvTitle.setText("Configure " + item.getName());

        // Populate Chips
        List<ServiceItem> tempSelectedServices = new ArrayList<>();
        if (chipGroup != null) {
            for (ServiceItem service : availableServices) {
                Chip chip = new Chip(this);
                chip.setText(service.getName() + " (₹" + (int)service.getPrice() + ")");
                chip.setCheckable(true);
                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) tempSelectedServices.add(service);
                    else tempSelectedServices.remove(service);
                    updateDialogButton(btnAddToCart, Integer.parseInt(tvQuantity.getText().toString()), tempSelectedServices);
                });
                chipGroup.addView(chip);
            }
        }
        
        final int[] quantity = {1};
        updateDialogButton(btnAddToCart, quantity[0], tempSelectedServices);

        if (btnDecrease != null) {
            btnDecrease.setOnClickListener(v -> {
                if (quantity[0] > 1) {
                    quantity[0]--;
                    if (tvQuantity != null) tvQuantity.setText(String.valueOf(quantity[0]));
                    updateDialogButton(btnAddToCart, quantity[0], tempSelectedServices);
                }
            });
        }

        if (btnIncrease != null) {
            btnIncrease.setOnClickListener(v -> {
                quantity[0]++;
                if (tvQuantity != null) tvQuantity.setText(String.valueOf(quantity[0]));
                updateDialogButton(btnAddToCart, quantity[0], tempSelectedServices);
            });
        }

        if (btnAddToCart != null) {
            btnAddToCart.setOnClickListener(v -> {
                if (tempSelectedServices.isEmpty()) {
                    Toast.makeText(this, "Select at least one service", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Add to Cart
                CartItem cartItem = new CartItem(item.getName(), quantity[0], new ArrayList<>(tempSelectedServices));
                cart.add(cartItem);
                updateCartUI();
                dialog.dismiss();
            });
        }

        dialog.show();
    }
    
    private void updateDialogButton(MaterialButton btn, int qty, List<ServiceItem> services) {
        if (btn == null) return;
        double total = 0;
        for (ServiceItem s : services) total += s.getPrice();
        total *= qty;
        btn.setText("Add to Cart - ₹" + (int)total);
    }

    private void updateCartUI() {
        int totalItems = 0;
        double totalPrice = 0;
        for (CartItem item : cart) {
            totalItems += item.getQuantity();
            totalPrice += item.getTotalPrice(); // CartItem logic handles calc
        }
        
        tvCartCount.setText(totalItems + " Items Selected");
        tvCartTotal.setText("Total: ₹" + (int)totalPrice);
        
        btnContinue.setEnabled(totalItems > 0);
        btnContinue.setAlpha(totalItems > 0 ? 1.0f : 0.5f);
    }
    
    private void proceedToCheckout() {
        if (cart.isEmpty()) return;
        
        // Flatten cart to description string
        StringBuilder builder = new StringBuilder();
        double finalPrice = 0;
        int finalQty = 0;
        
        for (CartItem item : cart) {
            finalQty += item.getQuantity();
            finalPrice += item.getTotalPrice(); // Assuming CartItem calculates correct total
            
            if (builder.length() > 0) builder.append(" | ");
            builder.append(item.getClothingName())
                   .append(" (")
                   .append(item.getServicesSummary())
                   .append(") x")
                   .append(item.getQuantity());
        }

        Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra("CART_DESCRIPTION", builder.toString());
        intent.putExtra("CART_TOTAL_PRICE", finalPrice);
        intent.putExtra("CART_TOTAL_QTY", finalQty);
        startActivity(intent);
    }

    // INTERNAL ADAPTER
    private static class ClothingAdapter extends RecyclerView.Adapter<ClothingAdapter.ViewHolder> {
        private List<ClothingItem> items;
        private OnItemClickListener listener;

        interface OnItemClickListener {
            void onItemClick(ClothingItem item);
        }

        public ClothingAdapter(List<ClothingItem> items, OnItemClickListener listener) {
            this.items = items;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clothing_category, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ClothingItem item = items.get(position);
            holder.tvName.setText(item.getName());
            holder.ivIcon.setImageResource(item.getIconResId());
            holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            ImageView ivIcon;
            
            ViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvClothingName);
                ivIcon = itemView.findViewById(R.id.ivClothingIcon);
            }
        }
    }
}
