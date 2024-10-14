package com.example.netflix_clone.Activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
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
import com.example.netflix_clone.Model.VideoItem;
import com.example.netflix_clone.Model.VideoStorageManager;
import com.example.netflix_clone.R;
import com.google.android.material.appbar.AppBarLayout;

import java.util.List;

public class DownloadedVideosActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private VideoStorageManager videoStorageManager;
    private PlayerView playerView;
    private ExoPlayer player;
    private boolean isFullscreen = false;
    private FrameLayout playerContainer;
    private AppBarLayout appBarLayout;
    private CoordinatorLayout rootLayout;
    private ImageView backButton;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descargas);

        initializeViews();
        setupRecyclerView();
        initializePlayer();

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        // Manejo de retroceso con OnBackPressedCallback
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isFullscreen) {
                    exitFullscreen();
                    playerContainer.setVisibility(View.GONE); // Asegúrate de ocultar el reproductor
                    releasePlayer(); // Libera los recursos al retroceder
                } else {
                    // Si no estás en fullscreen, puedes manejarlo de otra manera o llamar a finish()
                    finish(); // Por ejemplo, finaliza la actividad
                }
            }
        };

        // Agregar el callback al dispatcher
        getOnBackPressedDispatcher().addCallback(this, callback);
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
        videoStorageManager = new VideoStorageManager(this);
        List<VideoItem> videoItems = videoStorageManager.getVideos();
        adapter = new VideoAdapter(videoItems);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this::playVideo);
    }

    private void initializePlayer() {
        if (player == null) { // Verifica si el reproductor ya está inicializado
            player = new ExoPlayer.Builder(this).build();
            playerView.setPlayer(player);
            player.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int state) {
                    if (state == Player.STATE_ENDED) {
                        // Ocultar el contenedor del reproductor cuando el video termina
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

        // Eliminar el reproductor de su padre actual
        if (playerContainer.getParent() != null) {
            ((ViewGroup) playerContainer.getParent()).removeView(playerContainer);
        }

        // Agregar el reproductor directamente al layout raíz
        rootLayout.addView(playerContainer, new CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // Ocultar la UI del sistema
        rootLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void exitFullscreen() {
        isFullscreen = false;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        appBarLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

        // Eliminar el reproductor del layout raíz
        if (playerContainer.getParent() != null) {
            ((ViewGroup) playerContainer.getParent()).removeView(playerContainer);
        }

        // Agregar el reproductor de nuevo al layout de restricción
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.player_height)
        );
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        constraintLayout.addView(playerContainer, 0, params);

        // Mostrar la UI del sistema
        rootLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        // Asegúrate de que el reproductor esté oculto
        playerContainer.setVisibility(View.GONE);

        // Liberar los recursos del reproductor
        releasePlayer();
    }

    private void playVideo(VideoItem item) {
        // Verifica si el reproductor es nulo y lo inicializa
        if (player == null) {
            initializePlayer(); // Asegúrate de que este método inicialice el reproductor
        }

        Uri videoUri = videoStorageManager.getVideoUri(item.getVideoPath());
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
