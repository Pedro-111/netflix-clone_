package com.example.netflix_clone.Service;

import com.example.netflix_clone.Model.Request.MiListaRequest;
import com.example.netflix_clone.Model.Response.MiListaResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MiListaServiceApi {
   @GET("/api/MiLista/{idPerfil}")
   Call<List<MiListaResponse>> obtenerMiListaPorUsuario(@Path("idPerfil") int idPerfil);

   @POST("/api/MiLista/{idPerfil}")
   Call<MiListaResponse> agregarSeriePelicula(@Path("idPerfil") int idPerfil, @Body MiListaRequest miListaRequest);
}
