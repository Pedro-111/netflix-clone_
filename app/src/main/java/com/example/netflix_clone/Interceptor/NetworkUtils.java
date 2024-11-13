package com.example.netflix_clone.Interceptor;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;

public class NetworkUtils {
    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (isAirplaneModeOn(context)) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return false;
            }

            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED); // Añadido esta línea
        } else {
            android.net.NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null &&
                    activeNetworkInfo.isConnected() &&
                    isInternetAvailable(); // Añadido verificación adicional
        }
    }

    private static boolean isAirplaneModeOn(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }

    // Método adicional para verificar conexión real a internet
    private static boolean isInternetAvailable() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }
    public static void isConnectedAsync(Context context, ConnectivityCallback callback) {
        new Thread(() -> {
            boolean isConnected = isConnected(context);
            new Handler(Looper.getMainLooper()).post(() -> {
                callback.onResult(isConnected);
            });
        }).start();
    }

    public interface ConnectivityCallback {
        void onResult(boolean isConnected);
    }
}
