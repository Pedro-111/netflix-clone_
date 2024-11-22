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
    private static final long SEARCH_DELAY_MS = 300; // Delay for search typing
    private static final long LOADING_DELAY_MS = 800; // Delay for loading more items
    private Handler searchHandler;
    private Handler loadingHandler;

    // Variables para la paginación
    private boolean isLoading = false;
    private int currentPage = 1;
    private String currentQuery = "";
    private boolean isSearchMode = false;
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

        apiService = RetrofitClient.getMovieServiceApi();
        searchHandler = new Handler(Looper.getMainLooper());
        loadingHandler = new Handler(Looper.getMainLooper());

        setupScrollListener();
        fetchPopularContent(false);
        setupSearchView();
    }

    private void setupScrollListener() {
        recommendedRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == contentList.size() - 1) {
                    if (!isLoading) {
                        currentPage++;
                        loadMoreItems();

                    }
                }
            }
        });
    }

    private void loadMoreItems() {
        if (!isLoading) {
            isLoading = true;
            searchAdapter.setLoadingMore(true);

            loadingHandler.postDelayed(() -> {
                if (isSearchMode && !currentQuery.isEmpty()) {
                    performSearch(currentQuery, true);
                } else {
                    fetchPopularContent(true);
                }
            }, LOADING_DELAY_MS);
        }
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchHandler.removeCallbacksAndMessages(null);
                loadingHandler.removeCallbacksAndMessages(null);
                currentQuery = newText;
                currentPage = 1;

                if (newText.isEmpty()) {
                    isSearchMode = false;
                    searchAdapter.clear();
                    fetchPopularContent(false);
                } else {
                    isSearchMode = true;
                    searchHandler.postDelayed(() -> performSearch(newText, false), SEARCH_DELAY_MS);
                }
                return true;
            }
        });
    }

    private void performSearch(String query, boolean isLoadingMore) {
        if (!isLoadingMore) {
            searchAdapter.clear();
        }

        apiService.searchContent(API_KEY, query, "es-ES", currentPage).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Content> searchResults = response.body().getResults();
                    if (!searchResults.isEmpty()) {
                        searchAdapter.addContent(searchResults);
                        recommendedRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        // No más resultados disponibles
                        currentPage--;
                    }
                    Log.d("Contenido", "Número de ítems: " + contentList.size() + ", Página: " + currentPage);
                } else {
                    Log.d("SearchActivity", "Búsqueda no exitosa o cuerpo vacío");
                    currentPage--;
                }
                isLoading = false;
                searchAdapter.setLoadingMore(false);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("SearchActivity", "Error en la búsqueda: " + t.getMessage());
                isLoading = false;
                searchAdapter.setLoadingMore(false);
                currentPage--;
            }
        });
    }


    private void fetchPopularContent(boolean isLoadingMore) {
        isLoading = true;
        if (!isLoadingMore) {
            searchAdapter.setLoadingMore(true);
        }

        apiService.getPopularContent(API_KEY, "es-ES", currentPage).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Content> content = response.body().getResults();
                    if (!content.isEmpty()) {
                        if (!isLoadingMore) {
                            searchAdapter.clear();
                        }
                        searchAdapter.addContent(content);
                        Log.d("Contenido", "Número de ítems populares: " + content.size() +
                                ", Página: " + currentPage);
                    } else {
                        // No más resultados disponibles
                        currentPage--;
                    }
                } else {
                    Log.d("Contenido", "Respuesta no exitosa o cuerpo vacío");
                    currentPage--;
                }
                isLoading = false;
                searchAdapter.setLoadingMore(false);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("SearchActivity", "Error en la solicitud: " + t.getMessage());
                isLoading = false;
                searchAdapter.setLoadingMore(false);
                currentPage--;
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Limpiamos los handlers para evitar memory leaks
        searchHandler.removeCallbacksAndMessages(null);
        loadingHandler.removeCallbacksAndMessages(null);
    }
}