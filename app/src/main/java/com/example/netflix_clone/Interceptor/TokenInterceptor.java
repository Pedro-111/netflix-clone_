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
        String ShowRefreshToken = sharedPreferences.getString("refreshToken", null);
        Log.i(TAG, "Token desde interceptor: " + token);
        Log.i(TAG, "Refresh Token desde interceptor: " + ShowRefreshToken);

        if (token == null) {
            return chain.proceed(originalRequest);
        }

        // Usamos un objeto contenedor para mantener el estado
        class ConnectionState {
            volatile boolean hasInternet = false;
            volatile String currentToken;
        }

        final ConnectionState state = new ConnectionState();
        state.currentToken = token;

        // Primera verificación de conectividad
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

        Log.i(TAG, "Internet: " + state.hasInternet);

        // Si no hay conexión, procedemos con el token actual
        if (!state.hasInternet) {
            Log.i(TAG, "Sin Internet");
            return chain.proceed(originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + state.currentToken)
                    .build());
        }

        // Validar el token antes de usarlo

        Log.i(TAG,"Token es : "+ (isTokenValid(state.currentToken) ? "Valido":"Invalido"));
        if (!isTokenValid(state.currentToken)) {
            state.currentToken = renewToken();
            if (state.currentToken == null) {
                return chain.proceed(originalRequest);
            }
        }

        // Añade el token válido a la solicitud
        Request.Builder builder = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + state.currentToken);
        Response response = chain.proceed(builder.build());

        Log.i(TAG, "Código de respuesta: " + response.code());

        // Manejo de respuesta 401
        if (response.code() == 401) {
            // Verificamos la conexión nuevamente
            CountDownLatch newLatch = new CountDownLatch(1);
            NetworkUtils.isConnectedAsync(context, isConnected -> {
                state.hasInternet = isConnected;
                newLatch.countDown();
            });

            try {
                newLatch.await(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (state.hasInternet) {
                Log.i(TAG, "Con Internet");
                Log.i(TAG, "Token Invalido, renovando");
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
                Log.i(TAG, "Token Renovado");
                return newToken;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error renovando el token", e);
        }

        // Si la renovación falla, limpiamos los tokens
        sharedPreferences.edit().remove("token").remove("refreshToken").apply();
        return null;
    }
}