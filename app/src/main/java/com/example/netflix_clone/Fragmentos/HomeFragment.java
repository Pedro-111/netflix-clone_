package com.example.netflix_clone.Fragmentos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        acclaimedSeriesRecyclerView = view.findViewById(R.id.acclaimed_series_recycler_view);
        dramaticSeriesRecyclerView = view.findViewById(R.id.dramatic_series_recycler_view);
        yourNextStoryRecyclerView = view.findViewById(R.id.your_next_story_recycler_view);
        milistaRecyclerView = view.findViewById(R.id.milista_recycler_view);

        ImageButton searchButton = view.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });
        iniciarServices();
        setupRecyclerViews();
        loadData();  // Inicialmente cargamos los datos de la API
        return view;
    }

    // Configuración de los RecyclerViews y adaptadores
    private void setupRecyclerViews() {
        acclaimedSeriesAdapter = new ContentAdapter(new ArrayList<>(), getContext(), this);
        dramaticSeriesAdapter = new ContentAdapter(new ArrayList<>(), getContext(), this);
        yourNextStoryAdapter = new ContentAdapter(new ArrayList<>(), getContext(), this);
        milistaAdapter = new ContentAdapter(new ArrayList<>(),getContext(),this);

        acclaimedSeriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        dramaticSeriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        yourNextStoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        milistaRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        acclaimedSeriesRecyclerView.setAdapter(acclaimedSeriesAdapter);
        dramaticSeriesRecyclerView.setAdapter(dramaticSeriesAdapter);
        yourNextStoryRecyclerView.setAdapter(yourNextStoryAdapter);
        milistaRecyclerView.setAdapter(milistaAdapter);
    }
    private void iniciarServices() {
        miListaServiceApi = RetrofitClient.getMiListaServiceApi(getContext());
        movieDBApi = RetrofitClient.getMovieClient("https://api.themoviedb.org/3/").create(TheMovieDBApi.class);
    }
    private int getIdPerfil(){
        sharedPreferences = getContext().getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("idPerfil",-1);
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
                    //content.setTipo("serie");
                    // Set other necessary fields
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
                Toast.makeText(getContext(),"Codigo desde Home Fragment"+response.code(),Toast.LENGTH_SHORT).show();
                if (response.isSuccessful() && response.body() != null) {
                    MovieDetailsResponse details = response.body();
                    Content content = new Content();
                    content.setId(details.getId());
                    content.setTitle(details.getTitle());
                    content.setOverview(details.getOverview());
                    //content.set("pelicula");
                    content.setPoster_path(details.getPosterPath());
                    // Set other necessary fields
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
            requireActivity().runOnUiThread(() -> {
                milistaAdapter.updateData(miListaContent);
                if (miListaContent.isEmpty()) {
                    // Opcional: Mostrar un mensaje si la lista está vacía
                    showEmptyListMessage();
                }
            });
        }
    }

    private void showEmptyListMessage() {
        // Implementa aquí la lógica para mostrar un mensaje cuando la lista está vacía
//        // Por ejemplo, puedes tener un TextView que se hace visible
//        //View emptyView = getView().findViewById(R.id.empty_milista_view);
//        if (emptyView != null) {
//            emptyView.setVisibility(View.VISIBLE);
//            milistaRecyclerView.setVisibility(View.GONE);
//        }
    }

    // Método para intentar recargar los datos si alguna lista está vacía
    private void retryLoadingData() {
        if (acclaimedSeriesAdapter.getItemCount() == 0 ||
                dramaticSeriesAdapter.getItemCount() == 0 ||
                yourNextStoryAdapter.getItemCount() == 0 ||
                milistaAdapter.getItemCount() ==0
        ) {
            Toast.makeText(getContext(), "Reintentando cargar los datos...", Toast.LENGTH_SHORT).show();
            fetchData();
        }
    }

    // Método para obtener los datos de la API
    private void fetchData() {
        movieDBApi = RetrofitClient.getMovieClient("https://api.themoviedb.org/3/").create(TheMovieDBApi.class);

        // Llamada a Series Aclamadas
        movieDBApi.getTopRatedSeries(API_KEY, "es-ES").enqueue(new Callback<ApiResponse>() {
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
        movieDBApi.getDramaticSeries(API_KEY, "es-ES", "18").enqueue(new Callback<ApiResponse>() {
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
        movieDBApi.getYourNextStory(API_KEY, "es-ES").enqueue(new Callback<ApiResponse>() {
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
