package com.example.netflix_clone.Service;

import com.example.netflix_clone.Model.Perfil;
import com.example.netflix_clone.Model.Response.PerfilResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PerfilServiceApi {
    @GET("/api/Perfil/{id}")
    Call<Perfil> obtenerPerfil(@Path("id") int idPerfil);

    @GET("/api/Perfil")
    Call<List<Perfil>> obtenerPerfiles();
}
