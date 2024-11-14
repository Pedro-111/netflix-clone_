package com.example.netflix_clone.Interceptor;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.netflix_clone.Model.Request.TokenRequest;
import com.example.netflix_clone.Model.Response.TokenResponse;
import com.example.netflix_clone.Model.Response.TokenValidationResponse;
import com.example.netflix_clone.Service.AuthServiceApi;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import java.io.IOException;
import android.content.SharedPreferences;
import android.util.Log;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
class ConnectionState {
    volatile boolean hasInternet = false;
    volatile String currentToken;
}


public class TokenInterceptor implements Interceptor {
    private static final String TAG = "TokenInterceptor";
    private SharedPreferences sharedPreferences;
    private AuthServiceApi authServiceApi;
    private Context context;
    public TokenInterceptor(SharedPreferences sharedPreferences, AuthServiceApi authServiceApi) {
        this.sharedPreferences = sharedPreferences;
        this.authServiceApi = authServiceApi;
    }
    public TokenInterceptor(SharedPreferences sharedPreferences, AuthServiceApi authServiceApi, Context context) {
        this.sharedPreferences = sharedPreferences;
        this.authServiceApi = authServiceApi;
        this.context = context;
    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            return chain.proceed(originalRequest);
        }

        // Estado compartido
        final ConnectionState state = new ConnectionState();
        state.currentToken = token;

        // Verificar conexión solo si es necesario
        if (needsTokenValidation()) {
            verifyConnectivity(state);

            if (state.hasInternet) {
                if (!isTokenValid(state.currentToken)) {
                    state.currentToken = renewToken();
                    if (state.currentToken == null) {
                        // Si después de la renovación no hay token, proceder sin autorización
                        return chain.proceed(originalRequest);
                    }
                }
            } else {
                // Si no hay internet, usar el token existente
                Log.i(TAG, "Sin conexión, usando token existente");
            }
        }

        // Proceder con el request
        Request.Builder builder = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + state.currentToken);
        Response response = chain.proceed(builder.build());

        // Manejar 401
        if (response.code() == 401) {
            verifyConnectivity(state);

            if (state.hasInternet) {
                state.currentToken = renewToken();
                if (state.currentToken != null) {
                    response.close();
                    return chain.proceed(originalRequest.newBuilder()
                            .header("Authorization", "Bearer " + state.currentToken)
                            .build());
                }
            }
        }

        return response;
    }

    // Método para determinar si necesitamos validar el token
    private boolean needsTokenValidation() {
        long lastValidation = sharedPreferences.getLong("last_token_validation", 0);
        long currentTime = System.currentTimeMillis();
        // Validar solo si han pasado más de 5 minutos desde la última validación
        return (currentTime - lastValidation) > TimeUnit.MINUTES.toMillis(5);
    }

    // Método para verificar conectividad
    private void verifyConnectivity(ConnectionState state) {
        CountDownLatch latch = new CountDownLatch(1);
        NetworkUtils.isConnectedAsync(context, isConnected -> {
            state.hasInternet = isConnected;
            latch.countDown();
        });

        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    private boolean isTokenValid(String token) {
        try {
            Call<TokenValidationResponse> call = authServiceApi.validarToken(token);
            retrofit2.Response<TokenValidationResponse> response = call.execute();
            return response.isSuccessful() && response.body() != null && response.body().isSuccess();
        } catch (IOException e) {
            Log.e(TAG, "Error validando el token", e);
            return false;
        }
    }

    private synchronized String renewToken() {
        String refreshToken = sharedPreferences.getString("refreshToken", null);
        if (refreshToken == null) {
            return null;
        }

        // Verificar conectividad primero
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] hasInternet = {false};

        NetworkUtils.isConnectedAsync(context, isConnected -> {
            hasInternet[0] = isConnected;
            latch.countDown();
        });

        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return sharedPreferences.getString("token", null); // Mantener token actual si hay error
        }

        // Si no hay internet, mantener tokens actuales
        if (!hasInternet[0]) {
            Log.i(TAG, "Sin conexión a internet, manteniendo tokens actuales");
            return sharedPreferences.getString("token", null);
        }

        try {
            String oldToken = sharedPreferences.getString("token", null);
            TokenRequest tokenRequest = new TokenRequest(oldToken, refreshToken);
            Call<TokenResponse> call = authServiceApi.renovarAcceso(tokenRequest);
            retrofit2.Response<TokenResponse> response = call.execute();

            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                String newToken = response.body().getToken();
                String newRefreshToken = response.body().getRefreshToken();

                sharedPreferences.edit()
                        .putString("token", newToken)
                        .putString("refreshToken", newRefreshToken)
                        .commit();
                Log.i(TAG, "Token Renovado exitosamente");
                return newToken;
            } else {
                // Contar intentos fallidos
                int failedAttempts = sharedPreferences.getInt("token_renewal_failures", 0);
                failedAttempts++;

                // Solo borrar después de varios intentos fallidos
                if (failedAttempts >= 3) {
                    Log.e(TAG, "Múltiples fallos de renovación, borrando tokens");
                    sharedPreferences.edit()
                            .remove("token")
                            .remove("refreshToken")
                            .remove("token_renewal_failures")
                            .apply();
                    return null;
                } else {
                    sharedPreferences.edit()
                            .putInt("token_renewal_failures", failedAttempts)
                            .apply();
                    return oldToken; // Mantener token actual
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error renovando el token", e);
            return sharedPreferences.getString("token", null); // Mantener token actual en caso de error
        }
    }
}