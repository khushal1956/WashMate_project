package com.example.washmate_laundary_service;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationPickerActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView tvCurrentAddress;
    private Button btnConfirm;
    private String fullAddress = "";
    private String selectedCity = "";
    private String selectedPincode = "";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final LatLng DEFAULT_LOCATION = new LatLng(21.1702, 72.8311); // Surat, India as a sensible default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);

        tvCurrentAddress = findViewById(R.id.tvCurrentAddress);
        btnConfirm = findViewById(R.id.btnConfirmLocation);
        View btnBack = findViewById(R.id.btnBack);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnBack.setOnClickListener(v -> finish());
        btnConfirm.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("address", fullAddress);
            resultIntent.putExtra("city", selectedCity);
            resultIntent.putExtra("pincode", selectedPincode);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Customize map
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        
        enableMyLocation();

        mMap.setOnCameraIdleListener(() -> {
            LatLng center = mMap.getCameraPosition().target;
            reverseGeocode(center.latitude, center.longitude);
        });
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f));
                    reverseGeocode(location.getLatitude(), location.getLongitude());
                } else {
                    // Fallback to default if last location is null
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15f));
                    reverseGeocode(DEFAULT_LOCATION.latitude, DEFAULT_LOCATION.longitude);
                }
            }).addOnFailureListener(e -> {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15f));
                reverseGeocode(DEFAULT_LOCATION.latitude, DEFAULT_LOCATION.longitude);
            });
        }
    }

    private void reverseGeocode(double lat, double lon) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                fullAddress = address.getAddressLine(0);
                selectedCity = address.getLocality();
                selectedPincode = address.getPostalCode();
                
                tvCurrentAddress.setText(fullAddress);
            } else {
                tvCurrentAddress.setText("Unable to fetch address");
            }
        } catch (IOException e) {
            tvCurrentAddress.setText("Error: " + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onMapReady(mMap);
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
