package com.example.washmate_laundary_service;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationPickerActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private EditText etAddress, etCity, etPincode;
    private View btnMyLocation, btnConfirm, btnBack;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);

        // Bind UI
        etAddress = findViewById(R.id.etAddress);
        etCity = findViewById(R.id.etCity);
        etPincode = findViewById(R.id.etPincode);
        btnMyLocation = findViewById(R.id.btnMyLocation);
        btnConfirm = findViewById(R.id.btnConfirmLocation);
        btnBack = findViewById(R.id.btnBack);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnBack.setOnClickListener(v -> finish());

        btnMyLocation.setOnClickListener(v -> fetchLiveLocation());

        btnConfirm.setOnClickListener(v -> {
            String addr = etAddress.getText().toString().trim();
            String city = etCity.getText().toString().trim();
            String pin = etPincode.getText().toString().trim();

            if (addr.isEmpty() || city.isEmpty() || pin.isEmpty()) {
                Toast.makeText(this, "Please complete all address fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("address", addr);
            resultIntent.putExtra("city", city);
            resultIntent.putExtra("pincode", pin);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void fetchLiveLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        Toast.makeText(this, "Fetching Live GPS...", Toast.LENGTH_SHORT).show();
        
        fusedLocationClient.getCurrentLocation(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener(this, location -> {
            if (location != null) {
                reverseGeocode(location.getLatitude(), location.getLongitude());
            } else {
                Toast.makeText(this, "Unable to get GPS. Try outdoors or type manually.", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "GPS Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void reverseGeocode(double lat, double lon) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                
                String fullAddress = address.getAddressLine(0);
                String city = address.getLocality();
                String zip = address.getPostalCode();
                
                if (fullAddress != null) etAddress.setText(fullAddress);
                if (city != null) etCity.setText(city);
                if (zip != null) etPincode.setText(zip);
                
                Toast.makeText(this, "Location Extracted!", Toast.LENGTH_SHORT).show();
            } else {
                 Toast.makeText(this, "Location found, but address translation failed.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Network Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLiveLocation();
            } else {
                Toast.makeText(this, "Permission Denied. Please input address manually.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
