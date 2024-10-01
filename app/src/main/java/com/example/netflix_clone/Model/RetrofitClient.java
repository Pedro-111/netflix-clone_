package com.example.netflix_clone.Model;

import android.content.Context;
import com.example.netflix_clone.Interceptor.TokenInterceptor;
import com.example.netflix_clone.Service.AuthServiceApi;
import com.example.netflix_clone.Service.TheMovieDBApi;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.content.SharedPreferences;

public class RetrofitClient {

    private static Retrofit retrofitAuth = null;
    private static Retrofit retrofitMovie = null;

    // Cliente Retrofit con interceptor para AuthServiceApi
    public static Retrofit getAuthClient(String baseUrl, Context context) {
        if (retrofitAuth == null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("MyApp", Context.MODE_PRIVATE);
            AuthServiceApi tempAuthService = createTempAuthApiService(baseUrl);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new TokenInterceptor(sharedPreferences, tempAuthService))
                    .build();

            retrofitAuth = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitAuth;
    }

    // Cliente Retrofit sin interceptor para TheMovieServiceApi
    public static Retrofit getMovieClient(String baseUrl) {
        if (retrofitMovie == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .build();

            retrofitMovie = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitMovie;
    }

    // Servicio temporal de API de autenticación (sin interceptor)
    private static AuthServiceApi createTempAuthApiService(String baseUrl) {
        Retrofit tempRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return tempRetrofit.create(AuthServiceApi.class);
    }

    // Método para obtener el servicio AuthServiceApi
    public static AuthServiceApi getAuthServiceApi(Context context) {
        return getAuthClient("https://apilogin.somee.com", context).create(AuthServiceApi.class);
    }

    // Método para obtener el servicio TheMovieServiceApi (sin interceptor)
    public static TheMovieDBApi getMovieServiceApi() {
        return getMovieClient("https://api.themoviedb.org/3/").create(TheMovieDBApi.class);
    }
}