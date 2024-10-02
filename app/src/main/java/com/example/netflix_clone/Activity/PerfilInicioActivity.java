package com.example.netflix_clone.Activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.netflix_clone.Adapter.PerfilAdapter;
import com.example.netflix_clone.MainActivity;
import com.example.netflix_clone.Model.GridSpacingItemDecoration;
import com.example.netflix_clone.Model.Perfil;
import com.example.netflix_clone.Model.RetrofitClient;
import com.example.netflix_clone.R;
import com.example.netflix_clone.Service.PerfilServiceApi;

import java.util.List;
import com.example.netflix_clone.Adapter.PerfilSeleccionAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilInicioActivity extends AppCompatActivity {
    private static final String TAG = "PerfilInicioActivity";
    private static final int MAX_RETRIES = 3;
    private int currentRetry = 0;

    private RecyclerView recyclerView;
    private PerfilSeleccionAdapter perfilAdapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_perfil);

        recyclerView = findViewById(R.id.perfilesRecyclerView_inicio);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true));

        sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);

        cargarPerfiles();
    }

    private void cargarPerfiles() {
        PerfilServiceApi service = RetrofitClient.getPerfilServiceApi(this);
        Call<List<Perfil>> call = service.obtenerPerfiles();

        call.enqueue(new Callback<List<Perfil>>() {
            @Override
            public void onResponse(Call<List<Perfil>> call, Response<List<Perfil>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Perfil> perfiles = response.body();
                    setupRecyclerView(perfiles);
                    currentRetry = 0; // Reiniciar el contador de reintentos si es exitoso
                } else {
                    handleError("Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Perfil>> call, Throwable t) {
                handleError("Error de conexión: " + t.getMessage());
            }
        });
    }

    private void handleError(String errorMessage) {
        Log.e(TAG, errorMessage);
        if (currentRetry < MAX_RETRIES) {
            currentRetry++;
            Toast.makeText(PerfilInicioActivity.this,
                    "Reintentando cargar perfiles... Intento " + currentRetry,
                    Toast.LENGTH_SHORT).show();

            // Esperar un momento antes de reintentar
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    cargarPerfiles();
                }
            }, 1000 * currentRetry); // Incrementar el tiempo de espera con cada reintento
        } else {
            Toast.makeText(PerfilInicioActivity.this,
                    "No se pudieron cargar los perfiles después de " + MAX_RETRIES + " intentos",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void setupRecyclerView(List<Perfil> perfiles) {
        perfilAdapter = new PerfilSeleccionAdapter(perfiles, perfil -> {
            try {
                int idPerfilServidor = perfil.getIdPerfil();
                guardarPerfilSeleccionado(idPerfilServidor);

                Toast.makeText(PerfilInicioActivity.this,
                        "Perfil seleccionado: " + perfil.getNombre(), Toast.LENGTH_SHORT).show();
                navigateToMain();
            } catch (Exception e) {
                Log.e(TAG, "Error al procesar la selección de perfil: " + e.getMessage());
                Toast.makeText(PerfilInicioActivity.this,
                        "Error al procesar la selección de perfil", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(perfilAdapter);
    }

    private void guardarPerfilSeleccionado(int idPerfil) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("idPerfil", idPerfil);
            editor.apply();
            Log.d(TAG, "ID de perfil guardado: " + idPerfil);
        } catch (Exception e) {
            Log.e(TAG, "Error al guardar el ID del perfil: " + e.getMessage());
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(PerfilInicioActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
