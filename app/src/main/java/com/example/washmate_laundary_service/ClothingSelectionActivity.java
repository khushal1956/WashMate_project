package com.example.washmate_laundary_service;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.washmate_laundary_service.models.CartItem;
import com.example.washmate_laundary_service.models.ClothingItem;
import com.example.washmate_laundary_service.models.ServiceItem;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClothingSelectionActivity extends BaseActivity {

    private RecyclerView rvClothingItems;
    private TextView tvCartCount, tvCartTotal;
    private MaterialButton btnContinue;
    private View btnBack;
    private LinearLayout chipGroupCategories;
    private TextView tvServiceCategory;
    
    private String selectedServiceName = "Standard Wash";
    private String selectedServiceType = "washing";
    private double servicePriceBase = 1.0;
    
    private List<ClothingItem> fullClothingList = new ArrayList<>();
    private List<ClothingItem> filteredList = new ArrayList<>();
    private List<ServiceItem> availableServices = new ArrayList<>();
    private List<CartItem> cart = new ArrayList<>();
    
    // Mock Data and Categories
    private final String[] NAMES = {"Shirt", "T-Shirt", "Jeans", "Trousers", "Saree", "Jacket", "Bedsheet", "Towel", "Premium Dress", "Bed Linens"};
    private final String[] CATEGORIES = {"Tops", "Tops", "Bottoms", "Bottoms", "Outerwear", "Outerwear", "Bedding", "Bedding", "Outerwear", "Bedding"};
    private final int[] ICONS = {
            R.drawable.ic_cloth_shirt_vibrant, R.drawable.ic_cloth_tshirt_vibrant,
            R.drawable.ic_cloth_jeans_vibrant, R.drawable.ic_cloth_trousers_vibrant,
            R.drawable.ic_cloth_saree_vibrant, R.drawable.ic_cat_outerwear,
            R.drawable.ic_cloth_bedsheet_vibrant, R.drawable.ic_cloth_towel_vibrant,
            R.drawable.ic_cloth_premium_dress_vibrant, R.drawable.ic_cloth_bedsheet_vibrant
    };
    private final double[] PRICES = {120, 80, 250, 220, 450, 350, 300, 150, 650, 400};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothing_selection);

        initializeViews();
        setupCategories();
        loadServicesAndItems();
        updateCartUI();
    }

    private void initializeViews() {
        rvClothingItems = findViewById(R.id.rvClothingItems);
        tvCartCount = findViewById(R.id.tvCartCount);
        tvCartTotal = findViewById(R.id.tvCartTotal);
        btnContinue = findViewById(R.id.btnContinue);
        btnBack = findViewById(R.id.btnBack);
        chipGroupCategories = findViewById(R.id.chipGroupCategories);
        tvServiceCategory = findViewById(R.id.tvServiceCategory);

        // Get extras from Intent
        if (getIntent().hasExtra("SERVICE_NAME")) {
            selectedServiceName = getIntent().getStringExtra("SERVICE_NAME");
            selectedServiceType = getIntent().getStringExtra("SERVICE_TYPE");
            servicePriceBase = getIntent().getDoubleExtra("SERVICE_PRICE", 1.0);
        }

        if (tvServiceCategory != null) tvServiceCategory.setText(selectedServiceName.toUpperCase());

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
        btnContinue.setOnClickListener(v -> proceedToCheckout());
    }

    private void setupCategories() {
        View.OnClickListener clickListener = v -> {
            updateChipStyles((TextView) v);
            filterByCategory(((TextView) v).getText().toString());
        };

        findViewById(R.id.chipTops).setOnClickListener(clickListener);
        findViewById(R.id.chipBottoms).setOnClickListener(clickListener);
        findViewById(R.id.chipBedding).setOnClickListener(clickListener);
        findViewById(R.id.chipOuterwear).setOnClickListener(clickListener);
    }

    private void updateChipStyles(TextView selectedChip) {
        int[] chipIds = {R.id.chipTops, R.id.chipBottoms, R.id.chipBedding, R.id.chipOuterwear};
        for (int id : chipIds) {
            TextView chip = findViewById(id);
            if (chip == selectedChip) {
                chip.setBackgroundResource(R.drawable.bg_gradient_primary);
                chip.setTextColor(getResources().getColor(android.R.color.white));
            } else {
                chip.setBackgroundResource(R.drawable.bg_glass_card);
                chip.setTextColor(Color.parseColor("#94A3B8"));
            }
        }
    }

    private void loadServicesAndItems() {
        // Prepare items with initial data and dynamic pricing
        fullClothingList.clear();
        for (int i = 0; i < NAMES.length; i++) {
            ClothingItem item = new ClothingItem(NAMES[i], ICONS[i]);
            
            // Apply logic for pricing based on service type
            double baseItemPrice = PRICES[i];
            double finalItemPrice = baseItemPrice;
            
            if ("washing".equalsIgnoreCase(selectedServiceType)) {
                // For washing, PRICES array might be base, or we use servicePriceBase as multiplier
                finalItemPrice = baseItemPrice; // Let's keep base for washing
            } else if ("dry_cleaning".equalsIgnoreCase(selectedServiceType)) {
                finalItemPrice = servicePriceBase > 1.0 ? servicePriceBase : baseItemPrice * 1.5;
            } else if ("ironing".equalsIgnoreCase(selectedServiceType)) {
                finalItemPrice = servicePriceBase; // Fixed price per item for ironing
            } else if ("premium".equalsIgnoreCase(selectedServiceType)) {
                finalItemPrice = servicePriceBase > 1.0 ? servicePriceBase : baseItemPrice * 2.0;
            }
            
            item.setPrice(finalItemPrice);
            item.setCategory(CATEGORIES[i]);
            fullClothingList.add(item);
        }
        
        // Mocking available services
        availableServices.clear();
        availableServices.add(new ServiceItem("1", selectedServiceName, servicePriceBase));

        filterByCategory("Tops"); // Default
    }

    private void filterByCategory(String category) {
        filteredList.clear();
        for (ClothingItem item : fullClothingList) {
            if (item.getCategory().equalsIgnoreCase(category)) {
                filteredList.add(item);
            }
        }
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        GlassClothingAdapter adapter = new GlassClothingAdapter(filteredList, selectedServiceType, new GlassClothingAdapter.OnQuantityChangeListener() {
            @Override
            public void onQuantityChanged(ClothingItem item, int newQty) {
                updateItemInCart(item, newQty);
            }
        });
        rvClothingItems.setLayoutManager(new LinearLayoutManager(this));
        rvClothingItems.setAdapter(adapter);
    }

    private void updateItemInCart(ClothingItem item, int qty) {
        // Find if already in cart
        CartItem existing = null;
        for (CartItem ci : cart) {
            if (ci.getClothingName().equals(item.getName())) {
                existing = ci;
                break;
            }
        }

        if (qty > 0) {
            if (existing != null) {
                existing.setQuantity(qty);
            } else {
                List<ServiceItem> services = new ArrayList<>();
                services.add(new ServiceItem("wash", "WashMate", item.getPrice()));
                cart.add(new CartItem(item.getName(), qty, services));
            }
        } else if (existing != null) {
            cart.remove(existing);
        }
        updateCartUI();
    }

    private void updateCartUI() {
        int totalItems = 0;
        double totalPrice = 0;
        for (CartItem item : cart) {
            totalItems += item.getQuantity();
            totalPrice += item.getTotalPrice();
        }
        
        tvCartCount.setText(totalItems + " Items Selected");
        tvCartTotal.setText(String.format(Locale.getDefault(), "₹%.2f", totalPrice));
        
        btnContinue.setEnabled(totalItems > 0);
        btnContinue.setAlpha(totalItems > 0 ? 1.0f : 0.6f);
    }

    private void proceedToCheckout() {
        if (cart.isEmpty()) return;
        
        StringBuilder builder = new StringBuilder();
        double finalPrice = 0;
        int finalQty = 0;
        
        for (CartItem item : cart) {
            finalQty += item.getQuantity();
            finalPrice += item.getTotalPrice();
            if (builder.length() > 0) builder.append(" | ");
            builder.append(item.getClothingName()).append(" x").append(item.getQuantity());
        }

        Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra("CART_DESCRIPTION", builder.toString());
        intent.putExtra("CART_TOTAL_PRICE", finalPrice);
        intent.putExtra("CART_TOTAL_QTY", finalQty);
        intent.putExtra("SERVICE_NAME", selectedServiceName);
        intent.putExtra("SERVICE_TYPE", selectedServiceType);
        intent.putExtra("SERVICE_PRICE", servicePriceBase);
        startActivity(intent);
    }

    // INTERNAL ADAPTER
    private static class GlassClothingAdapter extends RecyclerView.Adapter<GlassClothingAdapter.ViewHolder> {
        private List<ClothingItem> items;
        private OnQuantityChangeListener listener;
        private String serviceType;

        interface OnQuantityChangeListener {
            void onQuantityChanged(ClothingItem item, int newQty);
        }

        public GlassClothingAdapter(List<ClothingItem> items, String serviceType, OnQuantityChangeListener listener) {
            this.items = items;
            this.serviceType = serviceType;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clothing_glass, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ClothingItem item = items.get(position);
            holder.tvName.setText(item.getName());
            holder.tvPrice.setText(String.format(Locale.getDefault(), "₹%.0f", item.getPrice()));
            holder.ivIcon.setImageResource(item.getIconResId());
            
            // Dynamic Tinting based on Service Type
            int themeColor = Color.parseColor("#3b82f6"); // Default Wash Blue
            if ("dry_cleaning".equalsIgnoreCase(serviceType)) {
                themeColor = Color.parseColor("#c084fc"); // Purple
            } else if ("ironing".equalsIgnoreCase(serviceType)) {
                themeColor = Color.parseColor("#eab308"); // Amber/Gold
            } else if ("premium".equalsIgnoreCase(serviceType)) {
                themeColor = Color.parseColor("#a855f7"); // Indigo/Purple
            }
            holder.ivIcon.setColorFilter(themeColor, android.graphics.PorterDuff.Mode.SRC_IN);
            
            holder.tvQty.setText(String.valueOf(item.getQuantity()));

            holder.btnPlus.setOnClickListener(v -> {
                int q = item.getQuantity() + 1;
                item.setQuantity(q);
                holder.tvQty.setText(String.valueOf(q));
                listener.onQuantityChanged(item, q);
            });

            holder.btnMinus.setOnClickListener(v -> {
                if (item.getQuantity() > 0) {
                    int q = item.getQuantity() - 1;
                    item.setQuantity(q);
                    holder.tvQty.setText(String.valueOf(q));
                    listener.onQuantityChanged(item, q);
                }
            });
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvPrice, tvQty;
            ImageView ivIcon;
            View btnPlus, btnMinus;
            
            ViewHolder(View v) {
                super(v);
                tvName = v.findViewById(R.id.tvClothingName);
                tvPrice = v.findViewById(R.id.tvClothingPrice);
                tvQty = v.findViewById(R.id.tvQuantity);
                ivIcon = v.findViewById(R.id.ivClothingIcon);
                btnPlus = v.findViewById(R.id.btnIncrease);
                btnMinus = v.findViewById(R.id.btnDecrease);
            }
        }
    }
}
