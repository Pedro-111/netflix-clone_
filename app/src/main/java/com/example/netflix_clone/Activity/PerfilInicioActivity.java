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
import com.example.netflix_clone.Interceptor.NetworkUtils;
import com.example.netflix_clone.MainActivity;
import com.example.netflix_clone.Model.AppDatabase;
import com.example.netflix_clone.Model.GridSpacingItemDecoration;
import com.example.netflix_clone.Model.Perfil;
import com.example.netflix_clone.Model.Perfiles;
import com.example.netflix_clone.Model.RetrofitClient;
import com.example.netflix_clone.R;
import com.example.netflix_clone.Service.PerfilServiceApi;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
        NetworkUtils.isConnectedAsync(PerfilInicioActivity.this,isConnected -> {
            if(isConnected){
                PerfilServiceApi service = RetrofitClient.getPerfilServiceApi(this);
                Call<List<Perfiles>> call = service.obtenerPerfiles();

                call.enqueue(new Callback<List<Perfiles>>() {
                    @Override
                    public void onResponse(Call<List<Perfiles>> call, Response<List<Perfiles>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Perfiles> perfiles = response.body();
                            setupRecyclerView(perfiles);
                            currentRetry = 0; // Reiniciar el contador de reintentos si es exitoso
                        } else {
                            handleError("Error en la respuesta: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Perfiles>> call, Throwable t) {
                        // Intentar cargar perfiles de la base de datos local
                        cargarPerfilesDesdeBaseDeDatos();
                    }
                });
            }else{
                if(!isDestroyed()&&!isFinishing()){
                    cargarPerfilesDesdeBaseDeDatos();
                }
            }
        });

    }

    private void cargarPerfilesDesdeBaseDeDatos() {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase database = AppDatabase.getInstance(this);
            List<Perfiles> perfiles = database.perfilDao().obtenerPerfiles();

            runOnUiThread(() -> {
                if (perfiles != null && !perfiles.isEmpty()) {
                    setupRecyclerView(perfiles);
                } else {
                    Toast.makeText(this, "No se encontraron perfiles, intenta conectarte a Internet.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }


    private void handleError(String errorMessage) {
        Log.e(TAG, errorMessage);
        if (currentRetry < MAX_RETRIES) {
            currentRetry++;
            Toast.makeText(PerfilInicioActivity.this,
                    "Reintentando cargar perfiles... Intento " + currentRetry,
                    Toast.LENGTH_SHORT).show();

            // Usar exponential backoff para los reintentos
            long delayMillis = (long) Math.pow(2, currentRetry - 1) * 1000;

            new Handler().postDelayed(() -> {
                cargarPerfiles();
            }, delayMillis);
        } else {
            Toast.makeText(PerfilInicioActivity.this,
                    "Usando datos guardados localmente",
                    Toast.LENGTH_LONG).show();

            // Guardar timestamp del último intento fallido
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("last_failed_server_attempt", System.currentTimeMillis());
            editor.apply();

            cargarPerfilesDesdeBaseDeDatos();
        }
    }

    private void setupRecyclerView(List<Perfiles> perfiles) {
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
