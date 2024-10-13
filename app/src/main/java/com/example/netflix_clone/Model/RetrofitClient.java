package com.example.netflix_clone.Model;

import android.content.Context;
import com.example.netflix_clone.Interceptor.TokenInterceptor;
import com.example.netflix_clone.Service.AuthServiceApi;
import com.example.netflix_clone.Service.MiListaServiceApi;
import com.example.netflix_clone.Service.PerfilServiceApi;
import com.example.netflix_clone.Service.TheMovieDBApi;
import com.example.netflix_clone.Service.TrailerServiceApi;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.content.SharedPreferences;

public class RetrofitClient {

    private static Retrofit retrofitAuth = null;
    private static Retrofit retrofitPerfil = null;
    private static Retrofit retrofitMiLista = null;
    private static Retrofit retrofitMovie = null;
    private static Retrofit retrofitTrailer=null;

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
    // Cliente Retrofit con interceptor para AuthServiceApi
    public static Retrofit getPerfilClient(String baseUrl, Context context) {
        if (retrofitPerfil == null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("MyApp", Context.MODE_PRIVATE);
            AuthServiceApi tempAuthService = createTempAuthApiService(baseUrl);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new TokenInterceptor(sharedPreferences, tempAuthService))
                    .build();

            retrofitPerfil = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitPerfil;
    }
    // Cliente Retrofit con interceptor para MiListaServiceApi
    public static Retrofit getMiListaClient(String baseUrl, Context context) {
        if (retrofitMiLista == null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("MyApp", Context.MODE_PRIVATE);
            AuthServiceApi tempAuthService = createTempAuthApiService(baseUrl);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new TokenInterceptor(sharedPreferences, tempAuthService))
                    .build();

            retrofitMiLista = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitMiLista;
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

    // Cliente Retrofit sin interceptor para TheMovieServiceApi
    public static Retrofit getTrailerClient(String baseUrl) {
        if (retrofitTrailer == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .build();

            retrofitTrailer = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitTrailer;
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

    // Método para obtener el servicio PerfilServiceApi
    public static PerfilServiceApi getPerfilServiceApi(Context context) {
        return getPerfilClient("https://apilogin.somee.com", context).create(PerfilServiceApi.class);
    }

    // Método para obtener el servicio MiListaServiceApi
    public static MiListaServiceApi getMiListaServiceApi(Context context) {
        return getMiListaClient("https://apilogin.somee.com", context).create(MiListaServiceApi.class);
    }

    // Método para obtener el servicio TheMovieServiceApi (sin interceptor)
    public static TheMovieDBApi getMovieServiceApi() {
        return getMovieClient("https://api.themoviedb.org/3/").create(TheMovieDBApi.class);
    }

    // Método para obtener el servicio TheTrailerServiceApi (sin interceptor)
    public static TrailerServiceApi getTrailerServiceApi() {
        return getTrailerClient("https://apilogin.somee.com").create(TrailerServiceApi.class);
    }
}