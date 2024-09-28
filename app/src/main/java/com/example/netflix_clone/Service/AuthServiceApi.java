package com.example.netflix_clone.Service;

import com.example.netflix_clone.Model.Request.LoginRequest;
import com.example.netflix_clone.Model.Request.TokenRequest;
import com.example.netflix_clone.Model.Response.LoginResponse;
import com.example.netflix_clone.Model.Response.TokenResponse;
import com.example.netflix_clone.Model.Response.TokenValidationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthServiceApi {
    @POST("/api/Acceso/Login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("/api/Acceso/RenovarToken")
    Call<TokenResponse> renovarAcceso(@Body TokenRequest tokenRequest);

    @GET("/api/Acceso/ValidarToken")
    Call<TokenValidationResponse> validarToken(@Query("token") String token);
}
