package com.example.netflix_clone.Interceptor;

import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.netflix_clone.Model.Request.TokenRequest;
import com.example.netflix_clone.Model.Response.TokenResponse;
import com.example.netflix_clone.Model.RetrofitClient;
import com.example.netflix_clone.Service.AuthServiceApi;

import java.io.IOException;


import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;

public class TokenInterceptor implements Interceptor {
    private SharedPreferences sharedPreferences;
    private AuthServiceApi authServiceApi;
    public TokenInterceptor(SharedPreferences sharedPreferences,AuthServiceApi authServiceApi){
        this.sharedPreferences = sharedPreferences;
        this.authServiceApi = authServiceApi;
    }
    @Override
    public Response intercept(Chain chain) throws IOException {

        Request originalRequest = chain.request();
        String token =sharedPreferences.getString("token",null);

        if(token==null){
            return chain.proceed(originalRequest);
        }
        Request.Builder builder = originalRequest.newBuilder().header("Authorization","Bearer "+token);
        Response response = chain.proceed(builder.build());

        if(response.code() ==401){
            String refreshToken = sharedPreferences.getString("refreshToken",null);
            if(refreshToken==null){
                return response;
            }
            synchronized (this){
                TokenRequest tokenRequest = new TokenRequest(token,refreshToken);
                Call<TokenResponse> call = authServiceApi.renovarAcceso(tokenRequest);

                try {
                    retrofit2.Response<TokenResponse> refreshResponse  = call.execute();
                    if(refreshResponse.isSuccessful() && refreshResponse.body() !=null && refreshResponse.body().isSuccess()){
                        String newToken = refreshResponse.body().getToken();
                        sharedPreferences.edit().putString("token",newToken).apply();

                        Request newRequest = originalRequest.newBuilder()
                                .header("Authorization","Bearer "+newToken)
                                .build();
                        response.close();
                        return chain.proceed(newRequest);
                    }
                }catch (IOException e){
                    //
                }
            }
        }
        return response;
    }
}
