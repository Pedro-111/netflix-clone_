package com.example.netflix_clone.Fragmentos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix_clone.Adapter.ContentAdapter;
import com.example.netflix_clone.DetailActivity;
import com.example.netflix_clone.Service.TheMovieDBApi;
import com.example.netflix_clone.Model.Response.ApiResponse;
import com.example.netflix_clone.Model.Content;
import com.example.netflix_clone.Model.RetrofitClient;
import com.example.netflix_clone.R;
import com.example.netflix_clone.SearchActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements ContentAdapter.OnItemClickListener {

    private RecyclerView acclaimedSeriesRecyclerView;
    private RecyclerView dramaticSeriesRecyclerView;
    private RecyclerView yourNextStoryRecyclerView;
    private ContentAdapter acclaimedSeriesAdapter;
    private ContentAdapter dramaticSeriesAdapter;
    private ContentAdapter yourNextStoryAdapter;
    private final String API_KEY = "1bdc0004cdd2b29842a351fba6d0abcb";  // Agrega tu API Key aquí

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        acclaimedSeriesRecyclerView = view.findViewById(R.id.acclaimed_series_recycler_view);
        dramaticSeriesRecyclerView = view.findViewById(R.id.dramatic_series_recycler_view);
        yourNextStoryRecyclerView = view.findViewById(R.id.your_next_story_recycler_view);

        ImageButton searchButton = view.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        setupRecyclerViews();
        loadData();  // Inicialmente cargamos los datos de la API
        return view;
    }

    // Configuración de los RecyclerViews y adaptadores
    private void setupRecyclerViews() {
        acclaimedSeriesAdapter = new ContentAdapter(new ArrayList<>(), getContext(), this);
        dramaticSeriesAdapter = new ContentAdapter(new ArrayList<>(), getContext(), this);
        yourNextStoryAdapter = new ContentAdapter(new ArrayList<>(), getContext(), this);

        acclaimedSeriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        dramaticSeriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        yourNextStoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        acclaimedSeriesRecyclerView.setAdapter(acclaimedSeriesAdapter);
        dramaticSeriesRecyclerView.setAdapter(dramaticSeriesAdapter);
        yourNextStoryRecyclerView.setAdapter(yourNextStoryAdapter);
    }

    // Método para obtener los datos de la API
    private void loadData() {
        fetchData();  // Llama a la API
    }

    // Método para intentar recargar los datos si alguna lista está vacía
    private void retryLoadingData() {
        if (acclaimedSeriesAdapter.getItemCount() == 0 ||
                dramaticSeriesAdapter.getItemCount() == 0 ||
                yourNextStoryAdapter.getItemCount() == 0) {
            Toast.makeText(getContext(), "Reintentando cargar los datos...", Toast.LENGTH_SHORT).show();
            fetchData();
        }
    }

    // Método para obtener los datos de la API
    private void fetchData() {
        TheMovieDBApi apiService = RetrofitClient.getMovieClient("https://api.themoviedb.org/3/").create(TheMovieDBApi.class);

        // Llamada a Series Aclamadas
        apiService.getTopRatedSeries(API_KEY, "es-ES").enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Content> series = response.body().getResults();
                    acclaimedSeriesAdapter.updateData(series);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Error al cargar series aclamadas", Toast.LENGTH_SHORT).show();
                retryLoadingData();  // Reintentar la carga si falla
            }
        });

        // Llamada a Series Dramáticas
        apiService.getDramaticSeries(API_KEY, "es-ES", "18").enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Content> series = response.body().getResults();
                    dramaticSeriesAdapter.updateData(series);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Error al cargar series dramáticas", Toast.LENGTH_SHORT).show();
                retryLoadingData();  // Reintentar la carga si falla
            }
        });

        // Llamada para "Tu Próxima Historia"
        apiService.getYourNextStory(API_KEY, "es-ES").enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Content> series = response.body().getResults();
                    yourNextStoryAdapter.updateData(series);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Error al cargar tu próxima historia", Toast.LENGTH_SHORT).show();
                retryLoadingData();  // Reintentar la carga si falla
            }
        });
    }

    @Override
    public void onItemClick(Content content) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("content", content);
        startActivity(intent);
    }
}
