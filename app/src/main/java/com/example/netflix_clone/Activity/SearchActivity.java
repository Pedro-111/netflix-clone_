package com.example.netflix_clone.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix_clone.Adapter.SearchAdapter;
import com.example.netflix_clone.R;
import com.example.netflix_clone.Service.TheMovieDBApi;
import com.example.netflix_clone.Model.Response.ApiResponse;
import com.example.netflix_clone.Model.Content;
import com.example.netflix_clone.Model.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recommendedRecyclerView;
    private SearchAdapter searchAdapter;
    private TheMovieDBApi apiService;
    private List<Content> contentList;
    private final String API_KEY = "1bdc0004cdd2b29842a351fba6d0abcb";
    private SearchView searchView;
    private static final long SEARCH_DELAY_MS = 300; // Delay in milliseconds
    private Handler searchHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        recommendedRecyclerView = findViewById(R.id.recommendedRecyclerView);
        searchView = findViewById(R.id.searchView);

        contentList = new ArrayList<>();
        searchAdapter = new SearchAdapter(contentList, this);

        recommendedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recommendedRecyclerView.setAdapter(searchAdapter);

        apiService = RetrofitClient.getMovieClient("https://api.themoviedb.org/3/").create(TheMovieDBApi.class);
        searchHandler = new Handler(Looper.getMainLooper());

        fetchRecommendedContent();
        setupSearchView();
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // We don't need this anymore as we're searching in real-time
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchHandler.removeCallbacksAndMessages(null); // Remove any pending searches
                if (newText.isEmpty()) {
                    fetchRecommendedContent();
                    recommendedRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    recommendedRecyclerView.setVisibility(View.GONE);
                    // Delay the search to avoid making an API call for every single character
                    searchHandler.postDelayed(() -> performSearch(newText), SEARCH_DELAY_MS);
                }
                return true;
            }
        });
    }

    private void performSearch(String query) {
        apiService.searchContent(API_KEY, query, "es-ES").enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Content> searchResults = response.body().getResults();
                    contentList.clear();
                    contentList.addAll(searchResults);
                    searchAdapter.notifyDataSetChanged();
                    recommendedRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    Log.d("SearchActivity", "Búsqueda no exitosa o cuerpo vacío");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("SearchActivity", "Error en la búsqueda: " + t.getMessage());
            }
        });
    }
    private void fetchRecommendedContent() {
        apiService.getPopularContent(API_KEY, "es-ES").enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Content> content = response.body().getResults();
                    Log.d("Contenido", "Número de ítems: " + content.size()); // Verifica el tamaño
                    if (!content.isEmpty()) {
                        contentList.clear();
                        contentList.addAll(content);
                        searchAdapter.notifyDataSetChanged(); // Notifica al adaptador
                    }
                } else {
                    Log.d("Contenido", "Respuesta no exitosa o cuerpo vacío");
                }
            }


            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Manejar el error
                Log.e("SearchActivity", "Error en la solicitud: " + t.getMessage());
            }
        });
    }
}
