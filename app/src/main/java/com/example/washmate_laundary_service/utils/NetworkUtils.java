package com.example.washmate_laundary_service.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {

    /**
     * Checks if there is any active network connection.
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    /**
     * Checks if a connection can be established to Google's servers.
     * This is useful for identifying captive portals or DNS issues.
     * MUST be called from a background thread.
     */
    public static boolean isGooglePlayServicesReachable() {
        try {
            HttpURLConnection urlc = (HttpURLConnection) (new URL("https://www.google.com").openConnection());
            urlc.setRequestProperty("User-Agent", "Test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1500);
            urlc.connect();
            return (urlc.getResponseCode() == 200);
        } catch (IOException e) {
            return false;
        }
    }
}
