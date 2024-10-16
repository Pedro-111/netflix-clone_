package com.example.netflix_clone.Activity;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.netflix_clone.Adapter.EpisodeAdapter;
import com.example.netflix_clone.Model.AppDatabase;
import com.example.netflix_clone.Model.Descarga;
import com.example.netflix_clone.Model.Request.MiListaRequest;
import com.example.netflix_clone.Model.Response.MiListaResponse;
import com.example.netflix_clone.Model.Response.TrailerResponse;
import com.example.netflix_clone.Model.RetrofitClient;
import com.example.netflix_clone.Model.VideoData;
import com.example.netflix_clone.Model.VideoDownloader;
import com.example.netflix_clone.Model.VideoItem;
import com.example.netflix_clone.Model.VideoStorageManager;
import com.example.netflix_clone.R;
import com.example.netflix_clone.Service.MiListaServiceApi;
import com.example.netflix_clone.Service.TheMovieDBApi;
import com.example.netflix_clone.Model.Content;
import com.example.netflix_clone.Model.Episode;
import com.example.netflix_clone.Model.Season;
import com.example.netflix_clone.Model.SeasonDetails;
import com.example.netflix_clone.Model.TVShowDetails;
import com.example.netflix_clone.Service.TrailerServiceApi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";
    private Spinner seasonSpinner;
    private RecyclerView episodesRecyclerView;
    private EpisodeAdapter episodeAdapter;
    private List<Season> seasons;
    private TheMovieDBApi api;
    private MiListaServiceApi miListaServiceApi;
    private TrailerServiceApi trailerServiceApi;
    private int seriesId, idPerfilActual;
    private final String API_KEY = "1bdc0004cdd2b29842a351fba6d0abcb";
    private SharedPreferences sharedPreferences;
    private ImageButton buttonMiLista;
    private Content content;
    private boolean isInMyList = false;
    private int idElemento = -1;
    private String mediaType;
    private ImageView posterImageView;
    private WebView videoView;
    private ImageButton download_video;
    private ProgressBar progressBar;
    private TextView progressText, fileNameText;
    private VideoDownloader videoDownloader;
    private String videoUrl;
    private VideoStorageManager videoStorageManager;
    private String nombreSeriePelicula;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initializeViews();
        initializeServices();

        content = (Content) getIntent().getSerializableExtra("content");
        if (content == null) {
            Log.e(TAG, "No content received from intent");
            Toast.makeText(this, "Error: No se pudo cargar el contenido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupContent();
        setupListeners();
        setupRecyclerView();
    }

    private void initializeViews() {
        seasonSpinner = findViewById(R.id.season_spinner);
        episodesRecyclerView = findViewById(R.id.episodes_recycler_view);
        buttonMiLista = findViewById(R.id.add_to_list_button);
        progressBar = findViewById(R.id.progress_bar);
        progressText = findViewById(R.id.progress_text);
        fileNameText = findViewById(R.id.file_name_text);
        download_video = findViewById(R.id.download_video_button);
        posterImageView = findViewById(R.id.posterImageView);
        videoView = findViewById(R.id.webView);
    }

    private void initializeServices() {
        idPerfilActual = obtenerPerfilActual();
        videoStorageManager = new VideoStorageManager(this);
        videoDownloader = new VideoDownloader(this);
        api = RetrofitClient.getMovieServiceApi();
        miListaServiceApi = RetrofitClient.getMiListaServiceApi(this);
        trailerServiceApi = RetrofitClient.getTrailerServiceApi();

    }

    private void setupContent() {
        TextView contentTitle = findViewById(R.id.content_title);
        TextView contentDescription = findViewById(R.id.content_description);

        nombreSeriePelicula = content.getTitle() != null ? content.getTitle() : content.getName();
        contentTitle.setText(nombreSeriePelicula);
        contentDescription.setText(content.getOverview());

        seriesId = content.getId();
        fetchSeasons(seriesId);

        if (idPerfilActual != -1) {
            checkIfInMyList();
        }
    }

    private void setupListeners() {
        findViewById(R.id.search_button).setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        findViewById(R.id.arrow_back).setOnClickListener(v -> finish());
        findViewById(R.id.watch_button).setOnClickListener(v -> Toast.makeText(this, "Reproduciendo " + content.getTitle(), Toast.LENGTH_SHORT).show());

        buttonMiLista.setOnClickListener(v -> {
            if (isInMyList && idElemento != -1) {
                eliminarDeMiLista(idElemento);
            } else {
                agregarAMiLista(content);
            }
        });

        download_video.setOnClickListener(v -> downloadVideo());
    }

    private void setupRecyclerView() {
        episodeAdapter = new EpisodeAdapter(new ArrayList<>());
        episodesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        episodesRecyclerView.setAdapter(episodeAdapter);
        episodesRecyclerView.setHasFixedSize(true);
        episodesRecyclerView.setItemViewCacheSize(20);
        episodesRecyclerView.setDrawingCacheEnabled(true);
        episodesRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        // Agregar un ScrollListener para cargar imágenes cuando sean visibles
        episodesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                loadVisibleEpisodeImages();
            }
        });
    }
    List<Episode> episodes;
    private void setEpisodios(List<Episode> episodios){
        this.episodes = episodios;
    }
    private int numeroEpisodios(){
        return episodes.size();
    }
    private void fetchEpisodes(int seasonNumber) {
        api.getSeasonDetails(seriesId, seasonNumber, API_KEY, "es-ES").enqueue(new Callback<SeasonDetails>() {
            @Override
            public void onResponse(Call<SeasonDetails> call, Response<SeasonDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Episode> episodes = response.body().getEpisodes();
                    episodeAdapter.updateEpisodes(episodes);
                    setEpisodios(episodes);
                    loadVisibleEpisodeImages();
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
    private void loadVisibleEpisodeImages() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) episodesRecyclerView.getLayoutManager();
        if (layoutManager != null) {
            int firstVisible = layoutManager.findFirstVisibleItemPosition();
            int lastVisible = layoutManager.findLastVisibleItemPosition();

            for (int i = firstVisible; i <= lastVisible; i++) {
                episodeAdapter.loadImageForPosition(i);
            }
        }
    }
    private void downloadVideo() {
        if (videoUrl == null || videoUrl.isEmpty()) {
            Toast.makeText(this, "No hay video disponible para descargar", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        progressText.setVisibility(View.VISIBLE);
        progressText.setText("Preparando descarga...");
        fileNameText.setVisibility(View.VISIBLE);
        fileNameText.setText("Obteniendo información del archivo...");
        download_video.setEnabled(false);

        videoDownloader.downloadVideo(videoUrl, new VideoDownloader.DownloadCallback() {
            @Override
            public void onDownloadStarted(String fileName) {
                runOnUiThread(() -> fileNameText.setText("Nombre: " + fileName));
            }

            @Override
            public void onProgressUpdate(int progress, String downloadedSize, String totalSize) {
                runOnUiThread(() -> {
                    progressBar.setProgress(progress);
                    progressText.setText(downloadedSize + " MB / " + totalSize + " MB");
                });
            }

            @Override
            public void onDownloadComplete(boolean success, String fileName) {
                runOnUiThread(() -> {
                    if (success) {
                        File videoFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
                        Log.d("PerfilId", "Perfil ID actual: " + idPerfilActual);
                        // Crear objeto Descarga
                        Descarga descarga = new Descarga();
                        descarga.idPerfil = idPerfilActual; // Método para obtener el ID del perfil actual
                        descarga.tmdbId = content.getId(); // Método para obtener el TMDB ID
                        descarga.tipo = identificarPeliculaSerie();
                        descarga.estado = "Completado";
                        descarga.fechaDescarga = System.currentTimeMillis();
                        descarga.rutaArchivo = videoFile.getAbsolutePath();
                        descarga.tamanoArchivo = videoFile.length();
                        descarga.nombreArchivo = fileName;
                        descarga.posterPath = content.getPoster_path(); // Método para obtener la ruta del póster
                        Log.d(TAG,"Ruta absoluta: "+videoFile.getAbsolutePath());
                        // Guardar en Room
                        insertarDescarga(descarga);  // Llama al método que usa Executor

                        Toast.makeText(DetailActivity.this, "Descarga completada con éxito: " + fileName, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(DetailActivity.this, "Error al guardar el archivo descargado", Toast.LENGTH_SHORT).show();
                    }
                    finalizarDescarga();
                });
            }


            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(DetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    finalizarDescarga();
                });
            }
        });
    }
    private void insertarDescarga(Descarga descarga) {
        Executors.newSingleThreadExecutor().execute(() -> {
            long result = AppDatabase.getInstance(DetailActivity.this).descargaDao().insert(descarga);
            if (result > 0) {
                runOnUiThread(() -> Toast.makeText(DetailActivity.this, "Descarga guardada correctamente", Toast.LENGTH_SHORT).show());
            } else {
                runOnUiThread(() -> Toast.makeText(DetailActivity.this, "Error al guardar la descarga", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void finalizarDescarga() {
        progressBar.setVisibility(View.GONE);
        progressText.setVisibility(View.GONE);
        fileNameText.setVisibility(View.GONE);
        download_video.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoDownloader.shutdown();
    }

    private void setupWebView() {
        WebSettings webSettings = videoView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(false);
        videoView.setWebChromeClient(new WebChromeClient());
        videoView.setWebViewClient(new WebViewClient());
    }

    private void playYouTubeVideo(String youtubeUrl, int width, int height) {
        String videoId = extractYouTubeVideoId(youtubeUrl);
        int videoHeight = (width * 9) / 16;

        String htmlContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\">" +
                "<style>" +
                "body { margin: 0; padding: 0; }" +
                ".video-container { position: relative; padding-bottom: 56.25%; height: 0; overflow: hidden; }" +
                ".video-container iframe { position: absolute; top: 0; left: 0; width: 100%; height: 100%; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"video-container\">" +
                "<div id=\"player\"></div>" +
                "</div>" +
                "<script src=\"https://www.youtube.com/iframe_api\"></script>" +
                "<script>" +
                "var player;" +
                "function onYouTubeIframeAPIReady() {" +
                "    player = new YT.Player('player', {" +
                "        videoId: '" + videoId + "'," +
                "        playerVars: {" +
                "            'autoplay': 1," +
                "            'playsinline': 1," +
                "            'controls': 1" +
                "        }," +
                "        events: {" +
                "            'onReady': onPlayerReady" +
                "        }" +
                "    });" +
                "}" +
                "function onPlayerReady(event) {" +
                "    event.target.playVideo();" +
                "}" +
                "</script>" +
                "</body>" +
                "</html>";

        videoView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
    }

    private String extractYouTubeVideoId(String youtubeUrl) {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youtubeUrl);
        return matcher.find() ? matcher.group() : "";
    }

    private void fetchAndPlayTrailer() {
        Call<TrailerResponse> call = trailerServiceApi.ObtenerTrailer(mediaType, content.getId());
        call.enqueue(new Callback<TrailerResponse>() {
            @Override
            public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<VideoData> videos = response.body().getVideos();
                    if (!videos.isEmpty()) {
                        videoUrl = videos.get(0).getUrl();
                        setupWebView();
                        videoView.setVisibility(View.VISIBLE);
                        posterImageView.setVisibility(View.GONE);
                        videoView.post(() -> playYouTubeVideo(videoUrl, videoView.getWidth(), videoView.getHeight()));
                    } else {
                        showPoster();
                    }
                } else {
                    showPoster();
                }
            }

            @Override
            public void onFailure(Call<TrailerResponse> call, Throwable t) {
                Log.e(TAG, "Error de conexión", t);
                runOnUiThread(() -> Toast.makeText(DetailActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void showPoster() {
        runOnUiThread(() -> {
            Toast.makeText(DetailActivity.this, "No se encontró tráiler, mostrando póster", Toast.LENGTH_SHORT).show();
            videoView.setVisibility(View.GONE);
            posterImageView.setVisibility(View.VISIBLE);
            loadImage(content.getPoster_path(), posterImageView);
        });
    }

    private void loadImage(String imagePath, ImageView imageView) {
        if (imagePath != null && !imagePath.isEmpty()) {
            if (!isFinishing() && !isDestroyed()) {  // Verificar que la actividad no está destruida
                Glide.with(this)
                        .load(imagePath)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(imageView);
            } else {
                Log.e(TAG, "Activity is finishing or destroyed, not loading the image.");
            }
        } else {
            Log.e(TAG, "Invalid image path");
        }
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
                            idElemento = item.getIdElemento();
                            updateMiListaButton();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MiListaResponse>> call, Throwable t) {
                runOnUiThread(() -> Toast.makeText(DetailActivity.this, "Error al verificar Mi Lista", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void agregarAMiLista(Content content) {
        if (idPerfilActual == -1) {
            Toast.makeText(this, "Error: Perfil no seleccionado", Toast.LENGTH_SHORT).show();
            return;
        }
        String tipo = identificarPeliculaSerie();
        MiListaRequest miListaRequest = new MiListaRequest(content.getId(), tipo);

        Call<MiListaResponse> call = miListaServiceApi.agregarSeriePelicula(idPerfilActual, miListaRequest);
        call.enqueue(new Callback<MiListaResponse>() {
            @Override
            public void onResponse(Call<MiListaResponse> call, Response<MiListaResponse> response) {
                if (response.isSuccessful()) {
                    isInMyList = true;
                    updateMiListaButton();
                    Toast.makeText(DetailActivity.this, "Añadido a Mi Lista", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailActivity.this, "Error al añadir a Mi Lista", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MiListaResponse> call, Throwable throwable) {
                Toast.makeText(DetailActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
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

    private boolean canRemoveFromList() {
        return isInMyList && idElemento != -1;
    }
    private int obtenerPerfilActual(){
        sharedPreferences = getSharedPreferences("MyApp",MODE_PRIVATE);
        int idPerfil = sharedPreferences.getInt("idPerfil",-1);
        return idPerfil;
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

                    // Luego de cargar temporadas se identifica si es Serie o Película
                    mediaType = identificarPeliculaSerie();
                    fetchAndPlayTrailer();
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
}