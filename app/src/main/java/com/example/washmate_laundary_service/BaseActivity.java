package com.example.washmate_laundary_service;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

import com.example.washmate_laundary_service.utils.LocaleHelper;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
