package com.example.netflix_clone.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix_clone.Adapter.MiListaAdapter;
import com.example.netflix_clone.Model.Content;
import com.example.netflix_clone.Model.Response.MiListaResponse;
import com.example.netflix_clone.Model.Response.MovieDetailsResponse;
import com.example.netflix_clone.Model.RetrofitClient;
import com.example.netflix_clone.Model.TVShowDetails;
import com.example.netflix_clone.R;
import com.example.netflix_clone.Service.MiListaServiceApi;
import com.example.netflix_clone.Service.TheMovieDBApi;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MiListaActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MiListaAdapter adapter;
    private List<MiListaResponse> listaCompleta;
    private List<Content> listaContenido;
    private MiListaServiceApi miListaServiceApi;
    private TheMovieDBApi movieDBApi;
    private SharedPreferences sharedPreferences;
    private ChipGroup chipGroup;
    private final String API_KEY = "1bdc0004cdd2b29842a351fba6d0abcb";
    private ImageView arrow_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_lista);
        sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);

        recyclerView = findViewById(R.id.recyclerView_mi_lista);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        arrow_back = findViewById(R.id.milista_arrow_back);
        adapter = new MiListaAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        chipGroup = findViewById(R.id.chipGroup);
        Chip chipSeries = findViewById(R.id.chipSeries);
        Chip chipMovies = findViewById(R.id.chipMovies);

        chipSeries.setOnClickListener(v -> filtrarLista("Serie"));
        chipMovies.setOnClickListener(v -> filtrarLista("Película"));

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                mostrarListaCompleta();
            }
        });

        arrow_back.setOnClickListener(v->{
            finish();
        });

        miListaServiceApi = RetrofitClient.getMiListaServiceApi(this);
        movieDBApi = RetrofitClient.getMovieClient("https://api.themoviedb.org/3/").create(TheMovieDBApi.class);
        obtenerMiLista();
    }

    private void obtenerMiLista() {
        int idPerfil = sharedPreferences.getInt("idPerfil", -1);
        Call<List<MiListaResponse>> call = miListaServiceApi.obtenerMiListaPorUsuario(idPerfil);
        call.enqueue(new Callback<List<MiListaResponse>>() {
            @Override
            public void onResponse(Call<List<MiListaResponse>> call, Response<List<MiListaResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaCompleta = response.body();
                    procesarListaCompleta();
                }
            }

            @Override
            public void onFailure(Call<List<MiListaResponse>> call, Throwable t) {
                Toast.makeText(MiListaActivity.this, "Error al obtener la lista", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void procesarListaCompleta() {
        listaContenido = new ArrayList<>();
        AtomicInteger pendingRequests = new AtomicInteger(listaCompleta.size());

        for (MiListaResponse item : listaCompleta) {
            if ("Serie".equalsIgnoreCase(item.getTipo())) {
                fetchTVShowDetails(item, listaContenido, pendingRequests);
            } else if ("Película".equalsIgnoreCase(item.getTipo())) {
                fetchMovieDetails(item, listaContenido, pendingRequests);
            } else {
                pendingRequests.decrementAndGet();
            }
        }
    }

    private void fetchTVShowDetails(MiListaResponse item, List<Content> contentList, AtomicInteger pendingRequests) {
        movieDBApi.getTVShowDetails(item.getTmdbId(), API_KEY, "es-ES").enqueue(new Callback<TVShowDetails>() {
            @Override
            public void onResponse(Call<TVShowDetails> call, Response<TVShowDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TVShowDetails details = response.body();
                    Content content = new Content();
                    content.setId(details.getId());
                    content.setTitle(details.getName());
                    content.setPoster_path(details.getPoster_path());
                    //content.setTipo("Serie");
                    contentList.add(content);
                }
                checkAndUpdateAdapter(pendingRequests, contentList);
            }

            @Override
            public void onFailure(Call<TVShowDetails> call, Throwable t) {
                checkAndUpdateAdapter(pendingRequests, contentList);
            }
        });
    }

    private void fetchMovieDetails(MiListaResponse item, List<Content> contentList, AtomicInteger pendingRequests) {
        movieDBApi.getMovieDetails(item.getTmdbId(), API_KEY, "es-ES").enqueue(new Callback<MovieDetailsResponse>() {
            @Override
            public void onResponse(Call<MovieDetailsResponse> call, Response<MovieDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieDetailsResponse details = response.body();
                    Content content = new Content();
                    content.setId(details.getId());
                    content.setTitle(details.getTitle());
                    content.setPoster_path(details.getPosterPath());
                    //content.setTipo("Película");
                    contentList.add(content);
                }
                checkAndUpdateAdapter(pendingRequests, contentList);
            }

            @Override
            public void onFailure(Call<MovieDetailsResponse> call, Throwable t) {
                checkAndUpdateAdapter(pendingRequests, contentList);
            }
        });
    }

    private void checkAndUpdateAdapter(AtomicInteger pendingRequests, List<Content> contentList) {
        if (pendingRequests.decrementAndGet() == 0) {
            runOnUiThread(() -> {
                adapter.actualizarLista(contentList);
                if (contentList.isEmpty()) {
                    // Mostrar mensaje si la lista está vacía
                }
            });
        }
    }

    private void filtrarLista(String tipo) {
        if (listaCompleta != null && listaContenido != null) {
            List<Content> listaFiltrada = new ArrayList<>();
            for (int i = 0; i < listaCompleta.size(); i++) {
                if (listaCompleta.get(i).getTipo().equalsIgnoreCase(tipo) && i < listaContenido.size()) {
                    listaFiltrada.add(listaContenido.get(i));
                }
            }
            adapter.actualizarLista(listaFiltrada);
        }
    }

    private void mostrarListaCompleta() {
        if (listaContenido != null) {
            adapter.actualizarLista(listaContenido);
        }
    }
}