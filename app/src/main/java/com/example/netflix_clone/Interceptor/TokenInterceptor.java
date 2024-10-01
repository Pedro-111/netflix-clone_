package com.example.netflix_clone.Interceptor;

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

public class TokenInterceptor implements Interceptor {
    private static final String TAG = "TokenInterceptor";
    private SharedPreferences sharedPreferences;
    private AuthServiceApi authServiceApi;

    public TokenInterceptor(SharedPreferences sharedPreferences, AuthServiceApi authServiceApi) {
        this.sharedPreferences = sharedPreferences;
        this.authServiceApi = authServiceApi;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            return chain.proceed(originalRequest);
        }

        // Validar el token antes de usarlo
        if (!isTokenValid(token)) {
            token = renewToken();
            if (token == null) {
                // Si la renovación falla, procede con la solicitud original
                // Esto probablemente resultará en un error 401, pero permite que la app maneje esto
                return chain.proceed(originalRequest);
            }
        }

        // Añade el token válido a la solicitud
        Request.Builder builder = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token);
        Response response = chain.proceed(builder.build());

        // Si aún así recibimos un 401, intentamos renovar el token una vez más
        if (response.code() == 401) {
            token = renewToken();
            if (token != null) {
                response.close();
                return chain.proceed(originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + token)
                        .build());
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

    private String renewToken() {
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
                        .apply();

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