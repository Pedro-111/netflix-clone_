package com.example.netflix_clone.Fragmentos;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix_clone.Activity.DownloadedVideosActivity;
import com.example.netflix_clone.Activity.MiListaActivity;
import com.example.netflix_clone.Adapter.ContentAdapter;
import com.example.netflix_clone.Activity.DetailActivity;
import com.example.netflix_clone.Model.Response.MiListaResponse;
import com.example.netflix_clone.Model.Response.MovieDetailsResponse;
import com.example.netflix_clone.Model.TVShowDetails;
import com.example.netflix_clone.Service.MiListaServiceApi;
import com.example.netflix_clone.Service.TheMovieDBApi;
import com.example.netflix_clone.Model.Response.ApiResponse;
import com.example.netflix_clone.Model.Content;
import com.example.netflix_clone.Model.RetrofitClient;
import com.example.netflix_clone.R;
import com.example.netflix_clone.Activity.SearchActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements ContentAdapter.OnItemClickListener {

    private RecyclerView acclaimedSeriesRecyclerView;
    private RecyclerView dramaticSeriesRecyclerView;
    private RecyclerView yourNextStoryRecyclerView;
    private RecyclerView milistaRecyclerView;
    private ContentAdapter acclaimedSeriesAdapter;
    private ContentAdapter dramaticSeriesAdapter;
    private ContentAdapter yourNextStoryAdapter;
    private ContentAdapter milistaAdapter;
    private final String API_KEY = "1bdc0004cdd2b29842a351fba6d0abcb";  // Agrega tu API Key aquí
    private SharedPreferences sharedPreferences;
    private int idPerfil;
    private MiListaServiceApi miListaServiceApi;
    private TheMovieDBApi movieDBApi;
    private ImageView verTodos;
    private ImageButton downloadButtonHome;
    private TextView textViewListaVerTodos;

    // Máximo número de intentos de carga de datos
    private final int MAX_RETRY_COUNT = 4;

    // Contadores para los intentos de carga de datos
    private int acclaimedSeriesRetryCount = 0;
    private int dramaticSeriesRetryCount = 0;
    private int yourNextStoryRetryCount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        acclaimedSeriesRecyclerView = view.findViewById(R.id.acclaimed_series_recycler_view);
        dramaticSeriesRecyclerView = view.findViewById(R.id.dramatic_series_recycler_view);
        yourNextStoryRecyclerView = view.findViewById(R.id.your_next_story_recycler_view);
        milistaRecyclerView = view.findViewById(R.id.milista_recycler_view);
        verTodos = view.findViewById(R.id.imageViewVerTodos);
        downloadButtonHome = view.findViewById(R.id.download_button_home);
        textViewListaVerTodos = view.findViewById(R.id.textViewListaVerTodos);

        ImageButton searchButton = view.findViewById(R.id.search_button);
        searchButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });

        downloadButtonHome.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), DownloadedVideosActivity.class);
            startActivity(intent);
        });

        verTodos.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MiListaActivity.class);
            startActivity(intent);
        });

        textViewListaVerTodos.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MiListaActivity.class);
            startActivity(intent);
        });

        iniciarServices();
        setupRecyclerViews();
        loadData();  // Inicialmente cargamos los datos de la API
        Log.d("Perfil id Actual: ",""+idPerfil);
        return view;
    }

    // Configuración de los RecyclerViews y adaptadores
    private void setupRecyclerViews() {
        acclaimedSeriesAdapter = new ContentAdapter(new ArrayList<>(), getContext(), this);
        dramaticSeriesAdapter = new ContentAdapter(new ArrayList<>(), getContext(), this);
        yourNextStoryAdapter = new ContentAdapter(new ArrayList<>(), getContext(), this);
        milistaAdapter = new ContentAdapter(new ArrayList<>(), getContext(), this);

        acclaimedSeriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        dramaticSeriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        yourNextStoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        milistaRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        acclaimedSeriesRecyclerView.setAdapter(acclaimedSeriesAdapter);
        dramaticSeriesRecyclerView.setAdapter(dramaticSeriesAdapter);
        yourNextStoryRecyclerView.setAdapter(yourNextStoryAdapter);
        milistaRecyclerView.setAdapter(milistaAdapter);
    }

    private void iniciarServices() {
        miListaServiceApi = RetrofitClient.getMiListaServiceApi(getContext());
        movieDBApi = RetrofitClient.getMovieClient("https://api.themoviedb.org/3/").create(TheMovieDBApi.class);
    }

    private int getIdPerfil() {
        sharedPreferences = getContext().getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("idPerfil", -1);
    }

    // Método para obtener los datos de la API
    private void loadData() {
        fetchMiListaContent();
        fetchData();
    }

    private void fetchMiListaContent() {
        int idPerfil = getIdPerfil();
        if (idPerfil == -1) return;

        miListaServiceApi.obtenerMiListaPorUsuario(idPerfil).enqueue(new Callback<List<MiListaResponse>>() {
            @Override
            public void onResponse(Call<List<MiListaResponse>> call, Response<List<MiListaResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    processMiListaResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<MiListaResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al cargar Mi Lista", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processMiListaResponse(List<MiListaResponse> miListaResponses) {
        List<Content> miListaContent = new ArrayList<>();
        AtomicInteger pendingRequests = new AtomicInteger(miListaResponses.size());

        for (MiListaResponse item : miListaResponses) {
            if ("Serie".equalsIgnoreCase(item.getTipo())) {
                fetchTVShowDetails(item, miListaContent, pendingRequests);
            } else if ("Película".equalsIgnoreCase(item.getTipo())) {
                fetchMovieDetails(item, miListaContent, pendingRequests);
            } else {
                pendingRequests.decrementAndGet();
            }
        }
    }

    private void fetchTVShowDetails(MiListaResponse item, List<Content> miListaContent, AtomicInteger pendingRequests) {
        movieDBApi.getTVShowDetails(item.getTmdbId(), API_KEY, "es-ES").enqueue(new Callback<TVShowDetails>() {
            @Override
            public void onResponse(Call<TVShowDetails> call, Response<TVShowDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TVShowDetails details = response.body();
                    Content content = new Content();
                    content.setId(details.getId());
                    content.setTitle(details.getName());
                    content.setOverview(details.getOverview());
                    content.setPoster_path(details.getPoster_path());
                    miListaContent.add(content);
                }
                checkAndUpdateAdapter(pendingRequests, miListaContent);
            }

            @Override
            public void onFailure(Call<TVShowDetails> call, Throwable t) {
                checkAndUpdateAdapter(pendingRequests, miListaContent);
            }
        });
    }

    private void fetchMovieDetails(MiListaResponse item, List<Content> miListaContent, AtomicInteger pendingRequests) {
        movieDBApi.getMovieDetails(item.getTmdbId(), API_KEY, "es-ES").enqueue(new Callback<MovieDetailsResponse>() {
            @Override
            public void onResponse(Call<MovieDetailsResponse> call, Response<MovieDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieDetailsResponse details = response.body();
                    Content content = new Content();
                    content.setId(details.getId());
                    content.setTitle(details.getTitle());
                    content.setOverview(details.getOverview());
                    content.setPoster_path(details.getPosterPath());
                    miListaContent.add(content);
                }
                checkAndUpdateAdapter(pendingRequests, miListaContent);
            }

            @Override
            public void onFailure(Call<MovieDetailsResponse> call, Throwable t) {
                checkAndUpdateAdapter(pendingRequests, miListaContent);
            }
        });
    }

    private void checkAndUpdateAdapter(AtomicInteger pendingRequests, List<Content> miListaContent) {
        if (pendingRequests.decrementAndGet() == 0) {
            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    milistaAdapter.updateData(miListaContent);
                });
            }
        }
    }

    private void fetchData() {
        fetchAcclaimedSeries();
        fetchDramaticSeries();
        fetchYourNextStory();
    }

    private void fetchAcclaimedSeries() {
        movieDBApi.getTopRatedSeries(API_KEY, "es-ES").enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    acclaimedSeriesAdapter.updateData(response.body().getResults());
                } else {
                    retryAcclaimedSeries();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                retryAcclaimedSeries();
            }
        });
    }

    private void retryAcclaimedSeries() {
        if (acclaimedSeriesRetryCount < MAX_RETRY_COUNT) {
            acclaimedSeriesRetryCount++;
            fetchAcclaimedSeries();
        } else {
            Log.e(TAG, "Failed to load acclaimed series after " + MAX_RETRY_COUNT + " attempts.");
        }
    }

    private void fetchDramaticSeries() {
        movieDBApi.getDramaticSeries(API_KEY, "es-ES","18").enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dramaticSeriesAdapter.updateData(response.body().getResults());
                } else {
                    retryDramaticSeries();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                retryDramaticSeries();
            }
        });
    }

    private void retryDramaticSeries() {
        if (dramaticSeriesRetryCount < MAX_RETRY_COUNT) {
            dramaticSeriesRetryCount++;
            fetchDramaticSeries();
        } else {
            Log.e(TAG, "Failed to load dramatic series after " + MAX_RETRY_COUNT + " attempts.");
        }
    }

    private void fetchYourNextStory() {
        movieDBApi.getYourNextStory(API_KEY, "es-ES").enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    yourNextStoryAdapter.updateData(response.body().getResults());
                } else {
                    retryYourNextStory();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                retryYourNextStory();
            }
        });
    }

    private void retryYourNextStory() {
        if (yourNextStoryRetryCount < MAX_RETRY_COUNT) {
            yourNextStoryRetryCount++;
            fetchYourNextStory();
        } else {
            Log.e(TAG, "Failed to load 'Your Next Story' after " + MAX_RETRY_COUNT + " attempts.");
        }
    }

    @Override
    public void onItemClick(Content content) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("content", content);
        startActivity(intent);
    }
}
