package com.example.washmate_laundary_service;

import android.app.Activity;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.washmate_laundary_service.adapters.ServiceSelectionAdapter;
import com.example.washmate_laundary_service.models.Order;
import com.example.washmate_laundary_service.models.ServiceItem;
import com.example.washmate_laundary_service.utils.FirebaseConstants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderActivity extends BaseActivity implements PaymentResultListener {


    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    
    private TextView tvCustomerName, tvServiceName, tvSelectedDate, tvServiceCharge, tvTotalAmount;
    private TextInputEditText etAddress, etCity, etPincode, etItemDescription; // Removed etQuantity
    private RecyclerView rvServices;
    private ServiceSelectionAdapter serviceAdapter;
    private List<ServiceItem> serviceList = new ArrayList<>();
    
    private LinearLayout cardDatePicker;
    private RadioGroup rgOnlinePaymentMethod;
    private MaterialButton btnPlaceOrder;
    private BottomNavigationView bottomNavigation;
    
    private String serviceName;
    private String serviceType;
    private String selectedDate = "";
    private boolean isCartFlow = false;
    private int cartQuantity = 0;
    private double serviceCharge = 0.0;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        
        try {
            // Initialize Firebase
            mAuth = FirebaseAuth.getInstance();
            mFirestore = FirebaseFirestore.getInstance();
            
            // Preload Razorpay
            Checkout.preload(getApplicationContext());

            
            // Get service details from intent
            serviceName = getIntent().getStringExtra("SERVICE_NAME");
            serviceType = getIntent().getStringExtra("SERVICE_TYPE");
            double servicePrice = getIntent().getDoubleExtra("SERVICE_PRICE", 299.0);
            
            // Check for Cart Data (New Flow)
            String cartDescription = getIntent().getStringExtra("CART_DESCRIPTION");
            double cartTotal = getIntent().getDoubleExtra("CART_TOTAL_PRICE", -1.0);
            int cartQty = getIntent().getIntExtra("CART_TOTAL_QTY", 0);

            if (cartDescription != null && cartTotal >= 0) {
                // Cart Flow
                isCartFlow = true;
                serviceName = "Multi-Item Order";
                serviceCharge = cartTotal;
                cartQuantity = cartQty;
            } else if (servicePrice > 0) {
                serviceCharge = servicePrice;
            }
            
            Log.d("OrderActivity", "Service: " + serviceName + ", Type: " + serviceType);
            
            initializeViews();
            Log.d("OrderActivity", "Views initialized");

            if (isCartFlow) {
                 // Cart Flow: Disable selection, use passed data
                 rvServices.setVisibility(View.GONE);
                 if (getIntent().hasExtra("CART_DESCRIPTION")) {
                     etItemDescription.setText(cartDescription);
                     etItemDescription.setEnabled(false); // Validated description
                 }
                 updateTotalAmount(); 
            } else {
                 // Standard Flow
                 fetchAvailableServices();
                 Log.d("OrderActivity", "Fetching available services");
            }
            
            setupListeners();
            Log.d("OrderActivity", "Listeners setup");
            
            // Initial update
            updateTotalAmount();
            
        } catch (Exception e) {
            Log.e("OrderActivity", "Error in onCreate", e);
            Toast.makeText(this, "Error initializing order screen", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeViews() {
        // Header
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvServiceName = findViewById(R.id.tvServiceName);
        if (tvServiceName != null) {
            tvServiceName.setText(serviceName != null ? serviceName : "Create Order");
        }
        
        // Fetch and display customer name
        fetchCustomerName();

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Form fields
        etItemDescription = findViewById(R.id.etItemDescription);
        
        // RecyclerView Setup
        rvServices = findViewById(R.id.rvServices);
        rvServices.setLayoutManager(new LinearLayoutManager(this));
        serviceAdapter = new ServiceSelectionAdapter(serviceList, this::updateTotalAmount);
        rvServices.setAdapter(serviceAdapter);

        etAddress = findViewById(R.id.etAddress);
        etCity = findViewById(R.id.etCity);
        etPincode = findViewById(R.id.etPincode);
        
        // Date picker
        cardDatePicker = findViewById(R.id.cardDatePicker);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        calendar = Calendar.getInstance();
        
        // Amount section
        tvServiceCharge = findViewById(R.id.tvServiceCharge);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        
        // Payment method
        rgOnlinePaymentMethod = findViewById(R.id.rgOnlinePaymentMethod);
        
        // Buttons
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        
        // Bottom navigation
        bottomNavigation = findViewById(R.id.bottomNavigation);
        setupBottomNavigation();
    }

    private void setupListeners() {
        // Date picker click
        cardDatePicker.setOnClickListener(v -> showDatePicker());
        
        // Place order button
        btnPlaceOrder.setOnClickListener(v -> validateAndPlaceOrder());
    }

    private void showDatePicker() {
        Calendar minDate = Calendar.getInstance();
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                    selectedDate = sdf.format(calendar.getTime());
                    tvSelectedDate.setText(selectedDate);
                    tvSelectedDate.setTextColor(getResources().getColor(android.R.color.black));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.show();
    }

    private void updateTotalAmount() {
        double total = 0.0;
        
        if (isCartFlow) {
            total = serviceCharge; // Already set in onCreate
        } else {
            for (ServiceItem item : serviceList) {
                total += (item.getPrice() * item.getQuantity());
            }
        }
        
        // Ensure minimum 0
        if (total < 0) total = 0;

        tvServiceCharge.setText("₹" + String.format(Locale.getDefault(), "%.0f", total));
        tvTotalAmount.setText("₹" + String.format(Locale.getDefault(), "%.0f", total));
    }

    private void validateAndPlaceOrder() {
        // Get form values
        String address = etAddress.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String pincode = etPincode.getText().toString().trim();
        String manualDescription = etItemDescription.getText().toString().trim();
        
        // Calculate total quantity and build description
        int totalQuantity = 0;
        StringBuilder servicesBuilder = new StringBuilder();
        double calculatedTotalAmount = 0.0;

        if (isCartFlow) {
            // Cart Flow: Use pre-calculated values
            totalQuantity = cartQuantity;
            calculatedTotalAmount = serviceCharge;
            // description is already in etItemDescription (disabled)
        } else {
            // Service Selection Flow
            for (ServiceItem item : serviceList) {
                if (item.getQuantity() > 0) {
                    totalQuantity += item.getQuantity();
                    if (servicesBuilder.length() > 0) servicesBuilder.append(", ");
                    servicesBuilder.append(item.getName())
                                   .append(" x")
                                   .append(item.getQuantity());
                    calculatedTotalAmount += (item.getPrice() * item.getQuantity());
                }
            }
        }
        
        // Validation
        if (totalQuantity <= 0) {
            Toast.makeText(this, "Please select at least one service", Toast.LENGTH_SHORT).show();
            return;
        }

        // Append manual description if exists (Only for standard flow, or appended to cart note)
        String finalItemDescription;
        if (isCartFlow) {
            finalItemDescription = manualDescription; // Already preset in onCreate
        } else {
            finalItemDescription = servicesBuilder.toString();
            if (!TextUtils.isEmpty(manualDescription)) {
                finalItemDescription += " | Note: " + manualDescription;
            }
        }
        
        if (TextUtils.isEmpty(address)) {
            etAddress.setError("Address is required");
            etAddress.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(city)) {
            etCity.setError("City is required");
            etCity.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(pincode)) {
            etPincode.setError("Pincode is required");
            etPincode.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(selectedDate)) {
            Toast.makeText(this, "Please select pickup date", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Payment is always Online - validate payment method selection
        String paymentMode = "Online";
        int selectedMethodId = rgOnlinePaymentMethod.getCheckedRadioButtonId();
        if (selectedMethodId == -1) {
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String paymentMethod = "";
        if (selectedMethodId == R.id.rbUPI) {
            paymentMethod = "UPI";
        } else if (selectedMethodId == R.id.rbCreditCard) {
            paymentMethod = "Credit Card";
        } else if (selectedMethodId == R.id.rbDebitCard) {
            paymentMethod = "Debit Card";
        }
        
        // Place order
        if (paymentMode.equals("Online")) {
            // Start Razorpay Payment
            startPayment(calculatedTotalAmount);
        } else {
             // For COD or other methods (if any in future)
             placeOrder(finalItemDescription, totalQuantity, address, city, pincode, selectedDate, paymentMode, paymentMethod, "Pending", calculatedTotalAmount);
        }
    }

    private void startPayment(double amount) {
        final Activity activity = this;
        final Checkout co = new Checkout();
        // Replace with your actual Test Key ID
        co.setKeyID("rzp_test_1DP5mmOlF5G5ag"); 

        try {
            JSONObject options = new JSONObject();
            options.put("name", "WashMate Laundry");
            options.put("description", "Laundry Service Charges");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("theme.color", "#48CAE4");
            options.put("currency", "INR");
            
            // Amount in paise (multiply by 100)
            options.put("amount", String.valueOf((int)(amount * 100)));
            
            JSONObject prefill = new JSONObject();
            if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getEmail() != null) {
                prefill.put("email", mAuth.getCurrentUser().getEmail());
            }
            // prefill.put("contact", "9876543210");
            options.put("prefill", prefill);

            co.open(activity, options);
            
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
        
        // Get stored values to place order
        String address = etAddress.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String pincode = etPincode.getText().toString().trim();
        String manualDescription = etItemDescription.getText().toString().trim();
        
        // Re-calculate details
        int totalQuantity = 0;
        StringBuilder servicesBuilder = new StringBuilder();
        double calculatedTotalAmount = 0.0;

        if (isCartFlow) {
              totalQuantity = cartQuantity;
              calculatedTotalAmount = serviceCharge;
        } else {
            for (ServiceItem item : serviceList) {
                if (item.getQuantity() > 0) {
                    totalQuantity += item.getQuantity();
                    if (servicesBuilder.length() > 0) servicesBuilder.append(", ");
                    servicesBuilder.append(item.getName())
                            .append(" x")
                            .append(item.getQuantity());
                    calculatedTotalAmount += (item.getPrice() * item.getQuantity());
                }
            }
        }
        
        String finalItemDescription;
        if (isCartFlow) {
            finalItemDescription = manualDescription; // Already has full cart description in EditText
        } else {
            finalItemDescription = servicesBuilder.toString();
            if (!TextUtils.isEmpty(manualDescription)) {
                finalItemDescription += " | Note: " + manualDescription;
            }
        }

        String paymentMode = "Online";
        
        int selectedMethodId = rgOnlinePaymentMethod.getCheckedRadioButtonId();
        String paymentMethod = "Online";
        if (selectedMethodId == R.id.rbUPI) paymentMethod = "UPI";
        else if (selectedMethodId == R.id.rbCreditCard) paymentMethod = "Credit Card";
        else if (selectedMethodId == R.id.rbDebitCard) paymentMethod = "Debit Card";
        
        // Place order in Firestore
        placeOrder(finalItemDescription, totalQuantity, address, city, pincode, selectedDate, paymentMode, paymentMethod, "Paid", calculatedTotalAmount);
    }

    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
            btnPlaceOrder.setEnabled(true);
            btnPlaceOrder.setText("Place Order");
        } catch (Exception e) {
            Log.e("OrderActivity", "Exception in onPaymentError", e);
        }
    }

    private void placeOrder(String itemDescription, int quantity, String address, String city, 
                           String pincode, String pickupDate, String paymentMode, String paymentMethod, String paymentStatus, double totalAmount) {

        btnPlaceOrder.setEnabled(false);
        btnPlaceOrder.setText("Processing...");

        
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login to place order", Toast.LENGTH_SHORT).show();
            btnPlaceOrder.setEnabled(true);
            btnPlaceOrder.setText("Place Order");
            return;
        }
        
        String customerId = mAuth.getCurrentUser().getUid();
        String orderId = mFirestore.collection("ORDERS").document().getId();
        
        // Just empty additional services string as we packed it into itemDescription
        final String finalAdditionalServices = "";
        final double finalAdditionalTotal = 0.0;
        final double finalServiceCharge = totalAmount; // We treat total as service charge for simplicity in this model or split it if needed. 
        // Better: finalServiceCharge = 0, totalAmount = totalAmount.
        
        
        mFirestore.collection(FirebaseConstants.COLLECTION_CUSTOMERS)
                .document(customerId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String customerName = "Customer";
                    if (documentSnapshot.exists()) {
                        customerName = documentSnapshot.getString("fullName");
                        if (customerName == null) customerName = "Customer";
                    }
                    
                    // Create order object
                    // We use "Multi-Service" as the Service Type/Name if multiple are selected, or the passed one.
                    String finalServiceName = (serviceName != null && !serviceName.isEmpty()) ? serviceName : "Multi-Service";
                    
                    Order order = new Order(
                            orderId,
                            customerId,
                            customerName,
                            finalServiceName,
                            finalServiceName,  // Use same for type
                            itemDescription,
                            quantity,
                            finalAdditionalServices,
                            finalAdditionalTotal,
                            address,
                            city,
                            pincode,
                            pickupDate,
                            paymentMode,
                            paymentMethod,
                            0.0, // Base Service charge is 0 as we calculated per item
                            totalAmount,
                            paymentStatus
                    );

                    
                    // Save to Firestore
                    mFirestore.collection("ORDERS")
                            .document(orderId)
                            .set(order)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(OrderActivity.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            })

                            .addOnFailureListener(e -> {
                                Log.e("OrderActivity", "Error placing order", e);
                                Toast.makeText(OrderActivity.this, "Failed to place order", Toast.LENGTH_SHORT).show();
                                btnPlaceOrder.setEnabled(true);
                                btnPlaceOrder.setText("Place Order");
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("OrderActivity", "Error fetching customer data", e);
                    Toast.makeText(this, "Error placing order", Toast.LENGTH_SHORT).show();
                    btnPlaceOrder.setEnabled(true);
                    btnPlaceOrder.setText("Place Order");
                });
    }

    private void setupBottomNavigation() {
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_home);
            bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(this, CustomerDashboardActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_orders) {
                    // Navigate to orders
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    // Navigate to profile
                    return true;
                }
                return false;
            });
        }
    }

    private void fetchCustomerName() {
        if (mAuth.getCurrentUser() != null) {
            String customerId = mAuth.getCurrentUser().getUid();
            mFirestore.collection(FirebaseConstants.COLLECTION_CUSTOMERS)
                    .document(customerId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("fullName");
                            if (name != null && tvCustomerName != null) {
                                tvCustomerName.setText(name);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("OrderActivity", "Error fetching customer name", e);
                    });
        } else {
            if (tvCustomerName != null) {
                tvCustomerName.setText("Guest");
            }
        }
    }

    private void fetchAvailableServices() {
        serviceList.clear();
        mFirestore.collection("LAUNDRY_SERVICES")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String name = document.getString("serviceName");
                            Double price = document.getDouble("price");
                            String id = document.getId();
                            
                            if (name != null && price != null) {
                                ServiceItem item = new ServiceItem(id, name, price);
                                
                                // Auto-select if matches the service passed from intent
                                if (serviceName != null && name.equalsIgnoreCase(serviceName)) {
                                    item.setQuantity(1);
                                }
                                
                                serviceList.add(item);
                            }
                        }
                        serviceAdapter.notifyDataSetChanged();
                        updateTotalAmount();
                    } else {
                        Toast.makeText(this, "No services available", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("OrderActivity", "Error fetching services", e);
                    Toast.makeText(this, "Failed to load services", Toast.LENGTH_SHORT).show();
                });
    }
}
