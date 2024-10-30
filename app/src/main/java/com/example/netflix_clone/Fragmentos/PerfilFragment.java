package com.example.netflix_clone.Fragmentos;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.netflix_clone.Activity.DetailActivity;
import com.example.netflix_clone.Activity.WelcomeActivity;
import com.example.netflix_clone.Adapter.ContentAdapter;
import com.example.netflix_clone.Adapter.LikedShowsAdapter;
import com.example.netflix_clone.Fragmentos.Dialog.MenuPerfilBottomSheetFragment;
import com.example.netflix_clone.Fragmentos.Dialog.PerfilesBottomSheetFragment;
import com.example.netflix_clone.Model.AppDatabase;
import com.example.netflix_clone.Model.Content;
import com.example.netflix_clone.Model.Perfiles;
import com.example.netflix_clone.Model.Request.MeGustaDTO;
import com.example.netflix_clone.Model.Response.MovieDetailsResponse;
import com.example.netflix_clone.Model.RetrofitClient;
import com.example.netflix_clone.Model.TVShowDetails;
import com.example.netflix_clone.R;
import com.example.netflix_clone.Service.MeGustaService;
import com.example.netflix_clone.Service.TheMovieDBApi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilFragment extends Fragment implements MenuPerfilBottomSheetFragment.MenuPerfilListener,
        PerfilesBottomSheetFragment.PerfilSeleccionadoListener{

    private TextView textNombre;
    private ImageView imagenPerfil;
    private AppDatabase perfilDatabase;
    private RecyclerView recyclerViewLikedShows;
    private MeGustaService meGustaService;
    private TheMovieDBApi theMovieDBApi;
    private LikedShowsAdapter likedShowsAdapter;
    private final String API_KEY = "1bdc0004cdd2b29842a351fba6d0abcb";
    private View emptyStateView;
    private ProgressBar progressBar;
    @Override
    public void onResume() {
        super.onResume();
        // Actualizar la lista cada vez que el fragmento vuelve a estar visible
        mostrarSeriesPeliculasFavoritas();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mi_perfil, container, false);
        textNombre = view.findViewById(R.id.textNombre);
        imagenPerfil = view.findViewById(R.id.imagen_perfil);
        recyclerViewLikedShows = view.findViewById(R.id.recyclerViewLikedShows);
        emptyStateView = view.findViewById(R.id.empty_state_view);
        progressBar = view.findViewById(R.id.progressBar_mi_perfil);

        setupRecyclerView();
        setupServices();

        int idPerfil = obtenerPerfilSeleccionado();
        if (idPerfil != -1) {
            cargarDatosPerfil(idPerfil);
            // La carga inicial se hará en onResume
        } else {
            Toast.makeText(requireContext(), "No se ha seleccionado un perfil", Toast.LENGTH_SHORT).show();
        }

        setupClickListeners(view);

        return view;
    }
    private void setupServices() {
        perfilDatabase = AppDatabase.getInstance(getContext());
        meGustaService = RetrofitClient.getMeGustaServiceApi(requireContext());
        theMovieDBApi = RetrofitClient.getMovieServiceApi();
    }

    private void setupClickListeners(View view) {
        view.findViewById(R.id.layoutCambiarPerfil).setOnClickListener(v -> mostrarPerfilesBottomSheet());
        ImageView buttonMenu = view.findViewById(R.id.menu_mi_perfil);
        buttonMenu.setOnClickListener(v -> mostrarMenuPerfilBottomSheet());
    }
    private void setupRecyclerView() {
        likedShowsAdapter = new LikedShowsAdapter(content -> {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra("content", content);
            startActivity(intent);
        });
        recyclerViewLikedShows.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        recyclerViewLikedShows.setAdapter(likedShowsAdapter);
    }

    private void mostrarSeriesPeliculasFavoritas() {
        // Mostrar estado de carga si lo deseas
        showLoadingState();

        Call<List<MeGustaDTO>> call = meGustaService.obtenerMeGustasPorPerfil(obtenerPerfilSeleccionado());
        call.enqueue(new Callback<List<MeGustaDTO>>() {
            @Override
            public void onResponse(Call<List<MeGustaDTO>> call, Response<List<MeGustaDTO>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<MeGustaDTO> meGustaDTOList = response.body();
                    if (meGustaDTOList.isEmpty()) {
                        showEmptyState();
                    } else {
                        processMiListaResponse(meGustaDTOList);
                    }
                } else {
                    showEmptyState();
                    Log.e(TAG, "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<MeGustaDTO>> call, Throwable t) {
                if (!isAdded()) return;
                showEmptyState();
                Log.e(TAG, "Error de conexión", t);
            }
        });
    }
    private void showEmptyState() {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> {
            recyclerViewLikedShows.setVisibility(View.GONE);
            emptyStateView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        });
    }
    private void showContent(List<Content> content) {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> {
            recyclerViewLikedShows.setVisibility(View.VISIBLE);
            emptyStateView.setVisibility(View.GONE);
            likedShowsAdapter.updateData(content);
            progressBar.setVisibility(View.GONE);
        });
    }

    private void showLoadingState() {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> {
            recyclerViewLikedShows.setVisibility(View.GONE);
            emptyStateView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        });
    }

    private void processMiListaResponse(List<MeGustaDTO> meGustaList) {
        List<Content> miListaContent = new ArrayList<>();
        AtomicInteger pendingRequests = new AtomicInteger(meGustaList.size());

        for (MeGustaDTO item : meGustaList) {
            if ("Serie".equalsIgnoreCase(item.getTipo())) {
                fetchTVShowDetails(item, miListaContent, pendingRequests);
            } else if ("Película".equalsIgnoreCase(item.getTipo())) {
                fetchMovieDetails(item, miListaContent, pendingRequests);
            } else {
                if (pendingRequests.decrementAndGet() == 0) {
                    showContent(miListaContent);
                }
            }
        }
    }

    private void fetchTVShowDetails(MeGustaDTO item, List<Content> likedContent, AtomicInteger pendingRequests) {
        theMovieDBApi.getTVShowDetails(Integer.parseInt(item.getTmdbId()), API_KEY, "es-ES").enqueue(new Callback<TVShowDetails>() {
            @Override
            public void onResponse(Call<TVShowDetails> call, Response<TVShowDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TVShowDetails details = response.body();
                    Content content = new Content();
                    content.setId(details.getId());
                    content.setTitle(details.getName());
                    content.setOverview(details.getOverview());
                    content.setPoster_path(details.getPoster_path());
                    likedContent.add(content);
                }
                checkAndUpdateAdapter(pendingRequests, likedContent);
            }

            @Override
            public void onFailure(Call<TVShowDetails> call, Throwable t) {
                checkAndUpdateAdapter(pendingRequests, likedContent);
            }
        });
    }

    private void fetchMovieDetails(MeGustaDTO item, List<Content> likedContent, AtomicInteger pendingRequests) {
        theMovieDBApi.getMovieDetails(Integer.parseInt(item.getTmdbId()), API_KEY, "es-ES").enqueue(new Callback<MovieDetailsResponse>() {
            @Override
            public void onResponse(Call<MovieDetailsResponse> call, Response<MovieDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieDetailsResponse details = response.body();
                    Content content = new Content();
                    content.setId(details.getId());
                    content.setTitle(details.getTitle());
                    content.setOverview(details.getOverview());
                    content.setPoster_path(details.getPosterPath());
                    likedContent.add(content);
                }
                checkAndUpdateAdapter(pendingRequests, likedContent);
            }

            @Override
            public void onFailure(Call<MovieDetailsResponse> call, Throwable t) {
                checkAndUpdateAdapter(pendingRequests, likedContent);
            }
        });
    }

    private void checkAndUpdateAdapter(AtomicInteger pendingRequests, List<Content> miListaContent) {
        if (pendingRequests.decrementAndGet() == 0 && isAdded()) {
            showContent(miListaContent);
        }
    }

    private void mostrarMenuPerfilBottomSheet(){
        MenuPerfilBottomSheetFragment bottomSheetFragment = new MenuPerfilBottomSheetFragment();
        bottomSheetFragment.setListener((MenuPerfilBottomSheetFragment.MenuPerfilListener) this);
        bottomSheetFragment.show(getChildFragmentManager(), "MenuPerfilBottomSheet");
    }
    @Override
    public void onCerrarSesionClicked() {
        cerrarSesion();
    }

    private void cerrarSesion() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(getContext(), WelcomeActivity.class);
        startActivity(intent);

        Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
    }
    private void mostrarPerfilesBottomSheet() {
        PerfilesBottomSheetFragment bottomSheetFragment = new PerfilesBottomSheetFragment();
        bottomSheetFragment.setPerfilSeleccionadoListener((PerfilesBottomSheetFragment.PerfilSeleccionadoListener) this);
        bottomSheetFragment.show(getChildFragmentManager(), "PerfilesBottomSheet");
    }

    @Override
    public void onPerfilSeleccionado(int idPerfil) {
        cargarDatosPerfil(idPerfil);
        mostrarSeriesPeliculasFavoritas();
    }

    public void cargarDatosPerfil(final int idPerfil) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Perfiles perfil = perfilDatabase.perfilDao().obtenerPerfilPorId(idPerfil);
                if (perfil != null) {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textNombre.setText(perfil.getNombre());
                            Glide.with(requireActivity()).load(perfil.getFotoPerfilUrl()).into(imagenPerfil);
                        }
                    });
                } else {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Error al cargar el perfil", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private int obtenerPerfilSeleccionado() {
        SharedPreferences prefs = getContext().getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        return prefs.getInt("idPerfil", -1);
    }
}
