package com.example.netflix_clone.Service;

import com.example.netflix_clone.Model.Response.PerfilResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PerfilServiceApi {
    @GET("/api/Perfil")
    Call<List<PerfilResponse>> obtenerPerfil();
}
