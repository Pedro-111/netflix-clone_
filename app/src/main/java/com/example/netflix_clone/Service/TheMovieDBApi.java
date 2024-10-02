package com.example.netflix_clone.Service;

import com.example.netflix_clone.Model.Response.ApiResponse;
import com.example.netflix_clone.Model.Response.MovieDetailsResponse;
import com.example.netflix_clone.Model.SeasonDetails;
import com.example.netflix_clone.Model.TVShowDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
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

    @GET("discover/movie")
    Call<ApiResponse> getPopularContent(@Query("api_key") String apiKey, @Query("language") String language);

    @GET("search/multi")
    Call<ApiResponse> searchContent(
            @Query("api_key") String apiKey,
            @Query("query") String query,
            @Query("language") String language
    );
    @GET("tv/{tv_id}")
    Call<TVShowDetails> getTVShowDetails(
            @Path("tv_id") int tvId,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("movie/{movie_id}")
    Call<MovieDetailsResponse> getMovieDetails(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("tv/{tv_id}/season/{season_number}")
    Call<SeasonDetails> getSeasonDetails(
            @Path("tv_id") int tvId,
            @Path("season_number") int seasonNumber,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

}
