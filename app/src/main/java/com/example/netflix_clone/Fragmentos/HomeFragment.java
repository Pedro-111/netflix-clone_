package com.example.netflix_clone.Fragmentos;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix_clone.Adapter.ContentAdapter;
import com.example.netflix_clone.DetailActivity;
import com.example.netflix_clone.Interfaz.TheMovieDBApi;
import com.example.netflix_clone.Model.ApiResponse;
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

        // Llamar a la API para obtener los datos
        fetchData();

        return view;
    }

    private void setupRecyclerViews() {
        acclaimedSeriesAdapter = new ContentAdapter(new ArrayList<>(), getContext(), this);
        dramaticSeriesAdapter = new ContentAdapter(new ArrayList<>(), getContext(), this);
        yourNextStoryAdapter = new ContentAdapter(new ArrayList<>(), getContext(), this);

        acclaimedSeriesRecyclerView.setAdapter(acclaimedSeriesAdapter);
        dramaticSeriesRecyclerView.setAdapter(dramaticSeriesAdapter);
        yourNextStoryRecyclerView.setAdapter(yourNextStoryAdapter);
    }

    // Método para obtener los datos de la API
    private void fetchData() {
        TheMovieDBApi apiService = RetrofitClient.getClient("https://api.themoviedb.org/3/").create(TheMovieDBApi.class);

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
                // Manejar el error
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
                // Manejar el error
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
                // Manejar el error
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