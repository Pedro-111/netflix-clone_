package com.example.netflix_clone.Service;

import com.example.netflix_clone.Model.Request.MiListaRequest;
import com.example.netflix_clone.Model.Response.MiListaResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MiListaServiceApi {
   @GET("/api/MiLista/{idPerfil}")
   Call<List<MiListaResponse>> obtenerMiListaPorUsuario(@Path("idPerfil") int idPerfil);

   @POST("/api/MiLista/{idPerfil}")
   Call<MiListaResponse> agregarSeriePelicula(@Path("idPerfil") int idPerfil, @Body MiListaRequest miListaRequest);

   @DELETE("/api/MiLista/{idPerfil}/{idElemento}")
   Call<Void> eliminarDeMiLista(
           @Path("idPerfil") int idPerfil,
           @Path("idElemento") int tmdbId
   );
}
