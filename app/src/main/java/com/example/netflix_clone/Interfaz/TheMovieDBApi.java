package com.example.netflix_clone.Interfaz;

import com.example.netflix_clone.Model.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TheMovieDBApi {

    // Endpoint para series aclamadas
    @GET("tv/top_rated")
    Call<ApiResponse> getTopRatedSeries(@Query("api_key") String apiKey, @Query("language") String language);

    // Endpoint para series dramáticas (puedes utilizar 'discover' para filtrarlas por género)
    @GET("discover/tv")
    Call<ApiResponse> getDramaticSeries(@Query("api_key") String apiKey, @Query("language") String language, @Query("with_genres") String genreId);

    // Endpoint para obtener contenido personalizado
    @GET("discover/tv")
    Call<ApiResponse> getYourNextStory(@Query("api_key") String apiKey, @Query("language") String language);
}
