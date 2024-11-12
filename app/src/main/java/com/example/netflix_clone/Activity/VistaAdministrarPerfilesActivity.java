package com.example.netflix_clone.Activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix_clone.Adapter.PerfilManageAdapter;
import com.example.netflix_clone.Model.GridSpacingItemDecoration;
import com.example.netflix_clone.Model.Perfiles;
import com.example.netflix_clone.Model.RetrofitClient;
import com.example.netflix_clone.R;
import com.example.netflix_clone.Service.PerfilServiceApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VistaAdministrarPerfilesActivity extends AppCompatActivity {
    private RecyclerView recyclerViewProfiles;
    private PerfilManageAdapter adapter;
    private PerfilServiceApi perfilServiceApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_profiles);

        recyclerViewProfiles = findViewById(R.id.recycler_view_profiles_manage);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerViewProfiles.setLayoutManager(layoutManager);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        recyclerViewProfiles.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true));

        adapter = new PerfilManageAdapter(this, perfil -> {
            Intent intent = new Intent(this, AdministrarPerfilesActivity.class);
            intent.putExtra("idPerfil", perfil.getIdPerfil());
            startActivity(intent);
        });

        recyclerViewProfiles.setAdapter(adapter);
        perfilServiceApi = RetrofitClient.getPerfilServiceApi(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarPerfiles();
    }

    private void cargarPerfiles() {
        Call<List<Perfiles>> call = perfilServiceApi.obtenerPerfiles();
        call.enqueue(new Callback<List<Perfiles>>() {
            @Override
            public void onResponse(Call<List<Perfiles>> call, Response<List<Perfiles>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updatePerfiles(response.body());
                    Log.i(TAG, "Perfiles en Vista de Administracion de perfiles cargado");
                }
            }

            @Override
            public void onFailure(Call<List<Perfiles>> call, Throwable t) {
                Toast.makeText(VistaAdministrarPerfilesActivity.this,
                        "Error al cargar los perfiles", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
