package com.example.washmate_laundary_service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import com.example.washmate_laundary_service.utils.LocaleHelper;
import com.google.android.material.card.MaterialCardView;

public class LanguageSelectionActivity extends BaseActivity {

    private String selectedLanguage = "en";
    private MaterialCardView cardEnglish, cardHindi, cardGujarati, cardMarathi;
    private Button btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);

        cardEnglish = findViewById(R.id.cardEnglish);
        cardHindi = findViewById(R.id.cardHindi);
        cardGujarati = findViewById(R.id.cardGujarati);
        cardMarathi = findViewById(R.id.cardMarathi);
        btnContinue = findViewById(R.id.btnContinue);

        cardEnglish.setOnClickListener(v -> selectLanguage("en"));
        cardHindi.setOnClickListener(v -> selectLanguage("hi"));
        cardGujarati.setOnClickListener(v -> selectLanguage("gu"));
        cardMarathi.setOnClickListener(v -> selectLanguage("mr"));

        btnContinue.setOnClickListener(v -> {
            // Save selection
            LocaleHelper.setLocale(this, selectedLanguage);
            
            SharedPreferences prefs = getSharedPreferences("WashMate_Prefs", MODE_PRIVATE);
            prefs.edit().putBoolean("IS_LANGUAGE_SET", true).apply();
            
            // Go to Splash to handle auth routing
            startActivity(new Intent(LanguageSelectionActivity.this, SplashActivity.class));
            finish();
        });
        
        // Default selection
        selectLanguage("en");
    }

    private void selectLanguage(String lang) {
        selectedLanguage = lang;
        
        // Reset all
        resetCard(cardEnglish);
        resetCard(cardHindi);
        resetCard(cardGujarati);
        resetCard(cardMarathi);
        
        // Highlight selection
        if (lang.equals("en")) highlightCard(cardEnglish);
        else if (lang.equals("hi")) highlightCard(cardHindi);
        else if (lang.equals("gu")) highlightCard(cardGujarati);
        else if (lang.equals("mr")) highlightCard(cardMarathi);
    }

    private void resetCard(MaterialCardView card) {
        card.setStrokeWidth(convertDpToPx(1));
        card.setCardBackgroundColor(android.graphics.Color.parseColor("#0F172A"));
    }

    private void highlightCard(MaterialCardView card) {
        card.setStrokeWidth(convertDpToPx(2));
        card.setCardBackgroundColor(android.graphics.Color.parseColor("#1E293B"));
    }

    private int convertDpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
