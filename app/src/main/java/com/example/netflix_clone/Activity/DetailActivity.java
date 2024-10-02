package com.example.netflix_clone.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.netflix_clone.Adapter.EpisodeAdapter;
import com.example.netflix_clone.Model.Request.MiListaRequest;
import com.example.netflix_clone.Model.Response.MiListaResponse;
import com.example.netflix_clone.Model.RetrofitClient;
import com.example.netflix_clone.R;
import com.example.netflix_clone.Service.MiListaServiceApi;
import com.example.netflix_clone.Service.TheMovieDBApi;
import com.example.netflix_clone.Model.Content;
import com.example.netflix_clone.Model.Episode;
import com.example.netflix_clone.Model.Season;
import com.example.netflix_clone.Model.SeasonDetails;
import com.example.netflix_clone.Model.TVShowDetails;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";
    private Spinner seasonSpinner;
    private RecyclerView episodesRecyclerView;
    private EpisodeAdapter episodeAdapter;
    private List<Season> seasons;
    private TheMovieDBApi api;
    private MiListaServiceApi miListaServiceApi;
    private int seriesId,idPerfilActual;
    private final String API_KEY = "1bdc0004cdd2b29842a351fba6d0abcb";
    private SharedPreferences sharedPreferences;
    private ImageButton buttonMiLista;
    private Content content;
    private boolean isInMyList = false;
    private int idElemento = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView contentImage = findViewById(R.id.content_image);
        TextView contentTitle = findViewById(R.id.content_title);
        TextView contentDescription = findViewById(R.id.content_description);
        Button watchButton = findViewById(R.id.watch_button);
        ImageButton searchButton = findViewById(R.id.search_button);
        ImageView arrowBack = findViewById(R.id.arrow_back);
        seasonSpinner = findViewById(R.id.season_spinner);
        episodesRecyclerView = findViewById(R.id.episodes_recycler_view);
        buttonMiLista = findViewById(R.id.add_to_list_button);
        idPerfilActual = obtenerPerfilActual();
        inicarServicios();

        content = (Content) getIntent().getSerializableExtra("content");
        if (content != null && idPerfilActual != -1) {
            checkIfInMyList();
        }
        buttonMiLista.setOnClickListener(v -> {
            if (isInMyList && idElemento != -1) {
                eliminarDeMiLista(idElemento);
            } else {
                agregarAMiLista(content);
            }
        });
        if (content != null) {
            Log.d(TAG, "Content received: " + content.getTitle() + ", ID: " + content.getId());
            contentTitle.setText(content.getTitle());
            contentDescription.setText(content.getOverview());
            loadImage("https://image.tmdb.org/t/p/w500" + content.getPoster_path(), contentImage);

            // Asignar el ID de la serie
            this.seriesId = content.getId();

            // Obtener temporadas y episodios
            fetchSeasons(this.seriesId);
        } else {
            Log.e(TAG, "No content received from intent");
            Toast.makeText(this, "Error: No se pudo cargar el contenido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // Ir a la actividad de Search_activity
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });
        // Regresar a la actividad anterior
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        watchButton.setOnClickListener(v -> {
            Toast.makeText(this, "Reproduciendo " + content.getTitle(), Toast.LENGTH_SHORT).show();
            // Aquí iría la lógica para reproducir el contenido
        });

        // Configurar RecyclerView
        episodeAdapter = new EpisodeAdapter(new ArrayList<>());
        episodesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        episodesRecyclerView.setAdapter(episodeAdapter);
    }
    private void checkIfInMyList() {
        Call<List<MiListaResponse>> call = miListaServiceApi.obtenerMiListaPorUsuario(idPerfilActual);
        call.enqueue(new Callback<List<MiListaResponse>>() {
            @Override
            public void onResponse(Call<List<MiListaResponse>> call, Response<List<MiListaResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (MiListaResponse item : response.body()) {
                        if (item.getTmdbId() == content.getId()) {
                            isInMyList = true;
                            idElemento = item.getIdElemento(); // Guardamos el idElemento
                            updateMiListaButton();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MiListaResponse>> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Error al verificar Mi Lista", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void agregarAMiLista(Content content){
        if (idPerfilActual == -1) {
            Toast.makeText(this, "Error: Perfil no seleccionado", Toast.LENGTH_SHORT).show();
            return;
        }
        String tipo = identificarPeliculaSerie();
        MiListaRequest miListaRequest = new MiListaRequest(content.getId(), tipo);

        Log.d(TAG,"ID: "+content.getId());
        Call<MiListaResponse> call = miListaServiceApi.agregarSeriePelicula(idPerfilActual,miListaRequest);
        call.enqueue(new Callback<MiListaResponse>() {
            @Override
            public void onResponse(Call<MiListaResponse> call, Response<MiListaResponse> response) {
                // Dentro del onResponse exitoso:
                if (response.isSuccessful()) {
                    isInMyList = true;
                    updateMiListaButton();
                    Toast.makeText(DetailActivity.this, "Añadido a Mi Lista", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailActivity.this,
                            "Error al añadir a Mi Lista", Toast.LENGTH_SHORT).show();
                }
                if(response.code()==201){
                   Log.d(TAG,"Elemento de mi Lista cargado correctamente: "+response.body().getIdElemento());
                }
            }

            @Override
            public void onFailure(Call<MiListaResponse> call, Throwable throwable) {
                Toast.makeText(DetailActivity.this,
                        "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void eliminarDeMiLista(int idElemento) {
        Call<Void> call = miListaServiceApi.eliminarDeMiLista(idPerfilActual, idElemento);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    isInMyList = false;
                    // Removemos el reseteo de idElemento
                    updateMiListaButton();
                    Toast.makeText(DetailActivity.this, "Eliminado de Mi Lista", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailActivity.this, "Error al eliminar de Mi Lista", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMiListaButton() {
        if (isInMyList) {
            buttonMiLista.setImageResource(R.drawable.ic_check);
        } else {
            buttonMiLista.setImageResource(R.drawable.ic_add);
        }
    }

    // Si necesitas verificar el estado para otras operaciones, puedes usar un método como este:
    private boolean canRemoveFromList() {
        return isInMyList && idElemento != -1;
    }
    private int obtenerPerfilActual(){
        sharedPreferences = getSharedPreferences("MyApp",MODE_PRIVATE);
        int idPerfil = sharedPreferences.getInt("idPerfil",-1);
        return idPerfil;
    }
    private void inicarServicios(){
        // Inicializar Retrofit y API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(TheMovieDBApi.class);
        miListaServiceApi = RetrofitClient.getMiListaServiceApi(this);

    }
    private String identificarPeliculaSerie() {
        if (seasons == null || seasons.isEmpty()) {
            return "Película";
        } else {
            return "Serie";
        }
    }

    private void fetchSeasons(int seriesId) {
        api.getTVShowDetails(seriesId, API_KEY, "es-ES").enqueue(new Callback<TVShowDetails>() {
            @Override
            public void onResponse(Call<TVShowDetails> call, Response<TVShowDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    seasons = response.body().getSeasons();
                    Log.d(TAG, "Seasons fetched: " + seasons.size());
                    setupSeasonSpinner();
                } else {
                    Log.e(TAG, "Error fetching seasons: " + response.code());
                    Toast.makeText(DetailActivity.this, "Error al cargar las temporadas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TVShowDetails> call, Throwable t) {
                Log.e(TAG, "Error fetching seasons", t);
                Toast.makeText(DetailActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSeasonSpinner() {
        List<String> seasonNames = new ArrayList<>();
        for (Season season : seasons) {
            seasonNames.add("Temporada " + season.getSeasonNumber());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, seasonNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seasonSpinner.setAdapter(adapter);

        seasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fetchEpisodes(seasons.get(position).getSeasonNumber());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Seleccionar la última temporada por defecto
        if (!seasons.isEmpty()) {
            seasonSpinner.setSelection(seasons.size() - 1);
        }
    }

    private void fetchEpisodes(int seasonNumber) {
        api.getSeasonDetails(seriesId, seasonNumber, API_KEY, "es-ES").enqueue(new Callback<SeasonDetails>() {
            @Override
            public void onResponse(Call<SeasonDetails> call, Response<SeasonDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Episode> episodes = response.body().getEpisodes();
                    Log.d(TAG, "Episodes fetched: " + episodes.size());
                    episodeAdapter.updateEpisodes(episodes);
                } else {
                    Log.e(TAG, "Error fetching episodes: " + response.code());
                    Toast.makeText(DetailActivity.this, "Error al cargar los episodios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SeasonDetails> call, Throwable t) {
                Log.e(TAG, "Error fetching episodes", t);
                Toast.makeText(DetailActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadImage(String imagePath, ImageView imageView) {
        if (imagePath != null && !imagePath.isEmpty()) {
            Log.d(TAG, "Loading image: " + imagePath);
            Glide.with(this)
                    .load(imagePath)
                    .into(imageView);
        } else {
            Log.e(TAG, "Invalid image path");

        }
    }
}