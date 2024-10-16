package com.example.netflix_clone.Activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix_clone.Adapter.VideoAdapter;
import com.example.netflix_clone.Model.AppDatabase;
import com.example.netflix_clone.Model.Descarga;
import com.example.netflix_clone.Model.VideoItem;
import com.example.netflix_clone.Model.VideoStorageManager;
import com.example.netflix_clone.R;
import com.google.android.material.appbar.AppBarLayout;

import java.util.List;
import java.util.concurrent.Executors;

public class DownloadedVideosActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private PlayerView playerView;
    private ExoPlayer player;
    private boolean isFullscreen = false;
    private FrameLayout playerContainer;
    private AppBarLayout appBarLayout;
    private CoordinatorLayout rootLayout;
    private ImageView backButton;
    private ConstraintLayout constraintLayout;
    private int perfilId;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descargas);
        perfilId = obtenerPerfilActual();
        Log.d("PerfilId", "Perfil ID actual: " + perfilId);
        initializeViews();
        setupRecyclerView();
        initializePlayer();

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isFullscreen) {
                    exitFullscreen();
                    playerContainer.setVisibility(View.GONE);
                    releasePlayer();
                } else {
                    finish();
                }
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private int obtenerPerfilActual() {
        sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);

        return sharedPreferences.getInt("idPerfil", -1);
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.videos_descargados);
        playerView = findViewById(R.id.player_view);
        playerContainer = findViewById(R.id.player_container);
        appBarLayout = findViewById(R.id.appbar_layout);
        rootLayout = findViewById(R.id.root_layout);
        constraintLayout = findViewById(R.id.constraint_layout);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadDownloadedVideos();
    }

    private void loadDownloadedVideos() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Descarga> descargas = AppDatabase.getInstance(DownloadedVideosActivity.this)
                    .descargaDao()
                    .getDescargasByPerfil(perfilId);

            // Agrega esta línea para verificar el tamaño de la lista
            Log.d("DownloadedVideos", "Número de descargas: " + (descargas != null ? descargas.size() : 0));

            runOnUiThread(() -> {
                adapter = new VideoAdapter(descargas);
                recyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener(DownloadedVideosActivity.this::playVideo);
            });
        });
    }


    private void initializePlayer() {
        if (player == null) {
            player = new ExoPlayer.Builder(this).build();
            playerView.setPlayer(player);
            player.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int state) {
                    if (state == Player.STATE_ENDED) {
                        playerContainer.setVisibility(View.GONE);
                        exitFullscreen();
                    }
                }
            });
        }
    }

    private void enterFullscreen() {
        isFullscreen = true;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        appBarLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        if (playerContainer.getParent() != null) {
            ((ViewGroup) playerContainer.getParent()).removeView(playerContainer);
        }

        rootLayout.addView(playerContainer, new CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        rootLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void exitFullscreen() {
        isFullscreen = false;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        appBarLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

        if (playerContainer.getParent() != null) {
            ((ViewGroup) playerContainer.getParent()).removeView(playerContainer);
        }

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.player_height)
        );
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        constraintLayout.addView(playerContainer, 0, params);

        rootLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        playerContainer.setVisibility(View.GONE);
        releasePlayer();
    }

    private void playVideo(Descarga descarga) {
        if (player == null) {
            initializePlayer();
        }

        Uri videoUri = Uri.parse(descarga.rutaArchivo);
        if (videoUri != null) {
            MediaItem mediaItem = MediaItem.fromUri(videoUri);
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();

            playerContainer.setVisibility(View.VISIBLE);
            enterFullscreen();
        } else {
            Toast.makeText(this, "Video not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            enterFullscreen();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            exitFullscreen();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null && playerContainer.getVisibility() == View.VISIBLE) {
            player.play();
        }
    }
}
