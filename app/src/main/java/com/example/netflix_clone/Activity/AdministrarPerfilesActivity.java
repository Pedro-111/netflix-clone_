package com.example.netflix_clone.Activity;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.netflix_clone.Model.Perfiles;
import com.example.netflix_clone.Model.Request.PerfilRequest;
import com.example.netflix_clone.Model.RetrofitClient;
import com.example.netflix_clone.R;
import com.example.netflix_clone.Service.PerfilServiceApi;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;


public class AdministrarPerfilesActivity extends AppCompatActivity {
    private ImageButton btnVolver;
    private PerfilServiceApi perfilServiceApi;
    private ImageView imageViewFotoPerfil;
    private TextInputEditText textInputNombrePerfil;
    private String nombreOriginal;
    private int idPerfil;
    Perfiles perfiles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrar_perfiles);
       inicializar();

       cargarPerfil();
       volver();

        textInputNombrePerfil.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No necesitamos implementar esto
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No necesitamos implementar esto
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Verificamos que el nuevo texto no esté vacío
                if (!s.toString().trim().isEmpty()) {
                    verificarYCambiarNombre();
                }
            }
        });


    }
    private int obtenerPerfilActual(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp",MODE_PRIVATE);

        return sharedPreferences.getInt("idPerfil",-1);
    }
    private void cargarPerfil(){
        idPerfil = obtenerPerfilActual();
        if(idPerfil==-1){
            Log.i(TAG,"IdPerfil en AdminstrarPerfiles: "+idPerfil);
        }
        Call<Perfiles> call = perfilServiceApi.obtenerPerfil(obtenerPerfilActual());

        call.enqueue(new Callback<Perfiles>() {
            @Override
            public void onResponse(Call<Perfiles> call, Response<Perfiles> response) {
                if(response.isSuccessful()&& response.body()!=null){
                    perfiles = response.body();
                    nombreOriginal = perfiles.getNombre();
                    Glide.with(AdministrarPerfilesActivity.this).load(perfiles.getFotoPerfilUrl()).into(imageViewFotoPerfil);
                    textInputNombrePerfil.setText(nombreOriginal);
                }
            }

            @Override
            public void onFailure(Call<Perfiles> call, Throwable throwable) {

            }
        });
    }
    private void verificarYCambiarNombre(){
        String nuevoNombrePerfil = textInputNombrePerfil.getText().toString().trim();

        if(!nuevoNombrePerfil.equals(nombreOriginal)){
            PerfilRequest perfilRequest = new PerfilRequest(nuevoNombrePerfil,perfiles.getFotoPerfilUrl());
            Call<Void> call = perfilServiceApi.actualizarPerfil(idPerfil,perfilRequest);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful()&& response.code()==204){
                        Log.i(TAG,"Nombre Actualizado");

                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable throwable) {

                }
            });

        }
    }
    private void inicializar(){
        btnVolver = findViewById(R.id.backButton_administrar);

        perfilServiceApi = RetrofitClient.getPerfilServiceApi(AdministrarPerfilesActivity.this);

        imageViewFotoPerfil = findViewById(R.id.foto_administrar_perfil);
        textInputNombrePerfil = findViewById(R.id.textNombre_administrar_perfil);
    }
    private void volver(){
        btnVolver.setOnClickListener(v->{
            finish();
        });
    }
}