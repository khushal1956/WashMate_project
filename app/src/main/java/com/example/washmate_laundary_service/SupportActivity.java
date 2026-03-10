package com.example.washmate_laundary_service;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SupportActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        setupContacts();
        setupNavigation();
    }

    private void setupContacts() {
        // Anuj
        setupContactCard(findViewById(R.id.contactAnuj), "Anuj Sabhadiya", "123456789", "Anuj@gmail.com");

        // Jaimeen
        setupContactCard(findViewById(R.id.contactJaimeen), "Jaimeen Gondaliya", "123456789", "jaimeen@gmail.com");

        // Khushal
        setupContactCard(findViewById(R.id.contactKhushal), "Khushal Savaliya", "123456789", "Khushal@gmail.com");

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void setupContactCard(View cardView, String name, String phone, String email) {
        if (cardView == null) return;

        TextView tvName = cardView.findViewById(R.id.tvName);
        TextView tvPhone = cardView.findViewById(R.id.tvPhone);
        TextView tvEmail = cardView.findViewById(R.id.tvEmail);
        View llPhone = cardView.findViewById(R.id.llPhone);
        View llEmail = cardView.findViewById(R.id.llEmail);

        if (tvName != null) tvName.setText(name);
        if (tvPhone != null) tvPhone.setText(phone);
        if (tvEmail != null) tvEmail.setText(email);

        // Click to Call
        if (llPhone != null) {
            llPhone.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
            });
        }

        // Click to Email
        if (llEmail != null) {
            llEmail.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + email));
                startActivity(intent);
            });
        }
    }

    private void setupNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_support);

            bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(this, CustomerDashboardActivity.class));
                    finish(); // Close this activity
                    return true;
                } else if (itemId == R.id.nav_orders) {
                    startActivity(new Intent(this, CustomerOrdersActivity.class));
                    finish(); // Close this activity
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(this, ProfileActivity.class));
                    finish(); // Close this activity
                    return true;
                } else if (itemId == R.id.nav_support) {
                    // Already here
                    return true;
                }
                return false;
            });
        }
    }
}
