package com.example.netflix_clone.Service;

import com.example.netflix_clone.Model.Response.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TrailerServiceApi {
    @GET("/api/Trailer/{mediaType}/{tmdbId}")
    Call<TrailerResponse> ObtenerTrailer(@Path("mediaType") String mediaType, @Path("tmdbId") int tmdbId);
}
