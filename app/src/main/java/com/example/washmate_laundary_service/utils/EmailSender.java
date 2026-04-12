package com.example.washmate_laundary_service.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EmailSender {

    private static final String TAG = "EmailSender";

    // --- EMAILJS CONFIGURATION ---
    // Please provide your own EmailJS credentials to enable real sending.
    // Register at: https://www.emailjs.com/
    private static final String SERVICE_ID = "service_tqh7l8g";
    private static final String TEMPLATE_ID = "__ejs-test-mail-service__";
    private static final String PUBLIC_KEY = "nKbPmlTj4jgor98ZZ";
    // ------------------------------

    private static final String API_URL = "https://api.emailjs.com/api/v1.0/email/send";

    public interface EmailListener {
        void onSuccess(boolean isDemoMode, String demoOtp);
        void onFailure(String error);
    }

    /**
     * Sends a real OTP via EmailJS API.
     */
    public static void sendOtp(Context context, String recipientEmail, String otp, EmailListener listener) {
        // If the configuration is still placeholder, run in Demo Mode
        if (SERVICE_ID.contains("xxxxxxx") || TEMPLATE_ID.contains("xxxxxxx") || PUBLIC_KEY.contains("xxxxxxx")) {
            Log.d(TAG, "################################################");
            Log.d(TAG, "DEMO MODE ENABLED: EmailJS credentials missing.");
            Log.d(TAG, "OTP for " + recipientEmail + " is: " + otp);
            Log.d(TAG, "################################################");
            
            // Simulate brief delay
            new android.os.Handler().postDelayed(() -> listener.onSuccess(true, otp), 1000);
            return;
        }

        try {
            RequestQueue queue = Volley.newRequestQueue(context);

            // Create Template Params
            JSONObject templateParams = new JSONObject();
            templateParams.put("to_email", recipientEmail);
            templateParams.put("otp_code", otp);
            templateParams.put("app_name", "WashMate Laundry Service");

            // Create Main Payload
            JSONObject payload = new JSONObject();
            payload.put("service_id", SERVICE_ID);
            payload.put("template_id", TEMPLATE_ID);
            payload.put("user_id", PUBLIC_KEY);
            payload.put("template_params", templateParams);

            com.android.volley.toolbox.StringRequest stringRequest = new com.android.volley.toolbox.StringRequest(
                Request.Method.POST, API_URL,
                response -> {
                    Log.d(TAG, "EmailJS Success Response: " + response);
                    listener.onSuccess(false, otp);
                },
                error -> {
                    String errorMsg = "API Error";
                    if (error.networkResponse != null) {
                        errorMsg = "Status Code: " + error.networkResponse.statusCode;
                        try {
                            String errorBody = new String(error.networkResponse.data, "UTF-8");
                            Log.e(TAG, "EmailJS Error Body: " + errorBody);
                            errorMsg += " - " + errorBody;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        errorMsg = error.getMessage() != null ? error.getMessage() : "Network or Timeout Error";
                    }
                    Log.e(TAG, "EmailJS Failure: " + error.toString());
                    listener.onFailure(errorMsg);
                }
            ) {
                @Override
                public byte[] getBody() {
                    return payload.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };

            queue.add(stringRequest);

        } catch (JSONException e) {
            Log.e(TAG, "Payload Construction failed: " + e.getMessage());
            listener.onFailure("Internal error preparing notification.");
        }
    }
}
