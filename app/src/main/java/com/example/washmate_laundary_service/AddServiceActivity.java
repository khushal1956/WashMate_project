package com.example.washmate_laundary_service;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.washmate_laundary_service.models.LaundryService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.UUID;

public class AddServiceActivity extends BaseActivity {

    private TextInputEditText etName, etPrice;
    private Spinner spnStatus;
    private Button btnSubmit;
    private ImageButton btnBack;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);

        mFirestore = FirebaseFirestore.getInstance();

        // Initialize Views
        etName = findViewById(R.id.etServiceName);
        etPrice = findViewById(R.id.etServicePrice);
        spnStatus = findViewById(R.id.spnServiceStatus);
        btnSubmit = findViewById(R.id.btnAddServiceSubmit);
        btnBack = findViewById(R.id.btnBack);

        // Setup Spinner
        String[] statuses = {"Active", "Inactive"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnStatus.setAdapter(statusAdapter);

        // Listeners
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> saveService());
        }
    }

    private void saveService() {
        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String status = spnStatus.getSelectedItem().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr)) {
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            return;
        }

        btnSubmit.setEnabled(false);
        btnSubmit.setText("Adding...");

        String serviceId = UUID.randomUUID().toString();
        LaundryService service = new LaundryService(serviceId, name, price, status);

        mFirestore.collection("LAUNDRY_SERVICES").document(serviceId)
                .set(service)
                .addOnSuccessListener(aVoid -> {
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Add Service");
                });
    }
}
