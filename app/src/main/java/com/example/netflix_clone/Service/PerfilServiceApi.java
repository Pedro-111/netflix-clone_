package com.example.netflix_clone.Service;

import com.example.netflix_clone.Model.Perfil;
import com.example.netflix_clone.Model.Perfiles;
import com.example.netflix_clone.Model.Response.PerfilResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PerfilServiceApi {
    @GET("/api/Perfil/{id}")
    Call<Perfiles> obtenerPerfil(@Path("id") int idPerfil);

    @GET("/api/Perfil")
    Call<List<Perfiles>> obtenerPerfiles();
}
