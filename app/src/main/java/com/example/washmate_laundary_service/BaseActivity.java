package com.example.washmate_laundary_service;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import com.example.washmate_laundary_service.utils.LocaleHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    protected void setupBottomNavigation(int selectedItemId) {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(selectedItemId);
            bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == selectedItemId) return true;

                Intent intent = null;
                if (itemId == R.id.nav_home) {
                    intent = new Intent(this, CustomerDashboardActivity.class);
                } else if (itemId == R.id.nav_orders) {
                    intent = new Intent(this, CustomerOrdersActivity.class);
                } else if (itemId == R.id.nav_profile) {
                    intent = new Intent(this, ProfileActivity.class);
                } else if (itemId == R.id.nav_support) {
                    intent = new Intent(this, SupportActivity.class);
                }

                if (intent != null) {
                    // Avoid creating a stack of the same activities
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    // Don't finish() here if you want to keep back stack, 
                    // but for a main navbar usually ReorderToFront is better.
                    // If moving from a deep page to home, finish() might be better.
                    return true;
                }
                return false;
            });
        }

        View fabStartOrder = findViewById(R.id.fabStartOrder);
        if (fabStartOrder != null) {
            fabStartOrder.setOnClickListener(v -> {
                Intent intent = new Intent(this, ClothingSelectionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            });
        }
    }
}
