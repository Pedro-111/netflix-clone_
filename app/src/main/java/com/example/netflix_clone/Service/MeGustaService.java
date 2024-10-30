package com.example.netflix_clone.Service;

import com.example.netflix_clone.Model.Request.MeGustaDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MeGustaService {
    @GET("api/Perfil/{perfilId}/megusta/{tmdbId}/exists")
    Call<Boolean> existeMeGusta(
            @Path("perfilId") int perfilId,
            @Path("tmdbId") String tmdbId
    );

    @POST("api/Perfil/{perfilId}/megusta")
    Call<MeGustaDTO> agregarMeGusta(
            @Path("perfilId") int perfilId,
            @Body MeGustaDTO meGustaDto
    );

    @DELETE("api/Perfil/{perfilId}/megusta/{tmdbId}")
    Call<Void> eliminarMeGusta(
            @Path("perfilId") int perfilId,
            @Path("tmdbId") String tmdbId
    );
}
