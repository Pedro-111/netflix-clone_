package com.example.netflix_clone.Activity;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix_clone.Adapter.IconoAdapter;
import com.example.netflix_clone.Adapter.SkeletonAdapter;
import com.example.netflix_clone.Model.Icono;
import com.example.netflix_clone.Model.Request.PerfilRequest;
import com.example.netflix_clone.Model.RetrofitClient;
import com.example.netflix_clone.R;
import com.example.netflix_clone.Service.PerfilServiceApi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeleccionarIconoActivity extends AppCompatActivity implements IconoAdapter.OnItemClickListener{

    private IconoAdapter adapterIconoClasicos;
    private IconoAdapter adapterIconoPerros;
    private IconoAdapter adapterIconoGatos;
    private IconoAdapter adapterIconoHamster;
    private IconoAdapter adapterIconoOtros;

    private RecyclerView recyclerViewClasicos;
    private RecyclerView recyclerViewPerros;
    private RecyclerView recyclerViewGatos;
    private RecyclerView recyclerViewHamster;
    private RecyclerView recyclerViewOtros;
    private List<Icono> clasicos;
    private List<Icono> perros;
    private List<Icono> gatos;
    private List<Icono> hamster;
    private List<Icono> otros;
    private int idPerfil;

    private PerfilServiceApi perfilServiceApi;
    private String nuevoNombrePerfil;

    private SkeletonAdapter skeletonAdapterClasicos;
    private SkeletonAdapter skeletonAdapterPerros;
    private SkeletonAdapter skeletonAdapterGatos;
    private SkeletonAdapter skeletonAdapterHamster;
    private SkeletonAdapter skeletonAdapterOtros;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_icono);
        initializeViews();
        showSkeletons();
        loadData();
    }
    private void initializeViews() {
        recyclerViewClasicos = findViewById(R.id.clasicos_recycler_view);
        recyclerViewPerros = findViewById(R.id.perros_recycler_view);
        recyclerViewGatos = findViewById(R.id.gatos_recycler_view);
        recyclerViewHamster = findViewById(R.id.hamsters_recycler_view);
        recyclerViewOtros = findViewById(R.id.otros_recycler_view);

        idPerfil = obtenerPerfilActual();
        nuevoNombrePerfil = obtenerNombreActual();
        perfilServiceApi = RetrofitClient.getPerfilServiceApi(this);

        // Configura los LayoutManagers
        setupLayoutManagers();
    }

    private void setupLayoutManagers() {
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager4 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager5 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerViewClasicos.setLayoutManager(layoutManager1);
        recyclerViewPerros.setLayoutManager(layoutManager2);
        recyclerViewGatos.setLayoutManager(layoutManager3);
        recyclerViewHamster.setLayoutManager(layoutManager4);
        recyclerViewOtros.setLayoutManager(layoutManager5);
    }
    private void loadData() {
        // Simula un tiempo de carga
        new Handler().postDelayed(() -> {
            crearListas();
            setRealAdapters();
        }, 1500); // 1.5 segundos de delay
    }
//    private void loadData() {
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        Handler handler = new Handler(Looper.getMainLooper());
//
//        executor.execute(() -> {
//                crearListas();
//                handler.post(this::setRealAdapters);
//        });
//    }
    private void showSkeletons() {
        // Crear y establecer los adaptadores skeleton
        skeletonAdapterClasicos = new SkeletonAdapter(5);
        skeletonAdapterPerros = new SkeletonAdapter(5);
        skeletonAdapterGatos = new SkeletonAdapter(5);
        skeletonAdapterHamster = new SkeletonAdapter(5);
        skeletonAdapterOtros = new SkeletonAdapter(5);

        recyclerViewClasicos.setAdapter(skeletonAdapterClasicos);
        recyclerViewPerros.setAdapter(skeletonAdapterPerros);
        recyclerViewGatos.setAdapter(skeletonAdapterGatos);
        recyclerViewHamster.setAdapter(skeletonAdapterHamster);
        recyclerViewOtros.setAdapter(skeletonAdapterOtros);
    }
    private void setRealAdapters() {
        adapterIconoClasicos = new IconoAdapter(clasicos, this, this);
        adapterIconoPerros = new IconoAdapter(perros, this, this);
        adapterIconoGatos = new IconoAdapter(gatos, this, this);
        adapterIconoHamster = new IconoAdapter(hamster, this, this);
        adapterIconoOtros = new IconoAdapter(otros, this, this);

        recyclerViewClasicos.setAdapter(adapterIconoClasicos);
        recyclerViewPerros.setAdapter(adapterIconoPerros);
        recyclerViewGatos.setAdapter(adapterIconoGatos);
        recyclerViewHamster.setAdapter(adapterIconoHamster);
        recyclerViewOtros.setAdapter(adapterIconoOtros);
    }
    private void crearListas(){
        clasicos = new ArrayList<>();
        clasicos.add(new Icono("https://wallpapers.com/images/high/netflix-profile-pictures-1000-x-1000-2fg93funipvqfs9i.webp"));
        clasicos.add(new Icono("https://wallpapers.com/images/high/netflix-profile-pictures-1000-x-1000-qo9h82134t9nv0j0.webp"));
        clasicos.add(new Icono("https://wallpapers.com/images/high/netflix-profile-pictures-1000-x-1000-w3lqr61qe57e9yt8.webp"));
        clasicos.add(new Icono("https://wallpapers.com/images/high/netflix-profile-pictures-1000-x-1000-62wgyitks6f4l79m.webp"));
        clasicos.add(new Icono("https://wallpapers.com/images/high/netflix-profile-pictures-5yup5hd2i60x7ew3.webp"));

        perros = new ArrayList<>();
        perros.add(new Icono("https://i.postimg.cc/3wWz4bm8/66d75543dcb91.webp"));
        perros.add(new Icono("https://i.postimg.cc/d0F79S0f/mauro-en-un-plato.png"));
        perros.add(new Icono("https://i.postimg.cc/0Qf1mV23/nosewey.gif"));
        perros.add(new Icono("https://i.postimg.cc/ZnDwSJs6/af5017c3cc8e6baab0a827af497e82e7.jpg"));
        perros.add(new Icono("https://i.postimg.cc/jdqYTLtW/images.jpg"));
        perros.add(new Icono("https://i.postimg.cc/Nf3x7J3G/Perro.jpg"));
        perros.add(new Icono("https://i.postimg.cc/Bt91DyXd/644953bd35477cc1313a2d0219f7c841.jpg"));
        perros.add(new Icono("https://i.postimg.cc/0y57CTts/b-YQDq5-N8-400x400.jpg"));
        perros.add(new Icono("https://www.veterinariasanjuan.com.ar/wp-content/uploads/cuanto-puede-vivir-un-bulldog-ingles.webp"));


        gatos = new ArrayList<>();
        gatos.add(new Icono("https://i.postimg.cc/MGvK65GR/hqdefault.jpg"));
        gatos.add(new Icono("https://i.postimg.cc/63ZWRQnp/El-Gato-meme-6.png"));
        gatos.add(new Icono("https://i.postimg.cc/qvYHLTkh/Dk-Cwbjx-Ws-AAy-Wwu.jpg"));
        gatos.add(new Icono("https://i.postimg.cc/8C8ncPpH/images.jpg"));
        gatos.add(new Icono("https://i.postimg.cc/TwK9Jz3w/116db5bfb4d0213d661ad7d03fa65f1f.jpg"));

        hamster = new ArrayList<>();
        hamster.add(new Icono("https://i.postimg.cc/c4CsMTQz/stoic-hamster-face-meme-r9vvc7bg2pjanuko.jpg"));
        hamster.add(new Icono("https://i.postimg.cc/bNt5ht8J/6c2ec053-488b-45ae-a292-725134696718-1668883191026-pfarm-with-png-watermarked.webp"));
        hamster.add(new Icono("https://i.postimg.cc/0jJVPLT3/c11f8d694659b7826e802dfc1cb3d8a6.jpg"));
        hamster.add(new Icono("https://i.postimg.cc/Z5Zzsc3Z/6e83bb326aa5a52820467c7b4a3fe006.jpg"));

        otros = new ArrayList<>();
        otros.add(new Icono("https://i.postimg.cc/ncxnNpb6/rana-que-salta-meme-meme-rana.gif"));
        otros.add(new Icono("https://i.postimg.cc/135s5BzB/c36f213aeb810b61935c2d146ad8695e.jpg"));
        otros.add(new Icono("https://i.postimg.cc/d0hFG0rT/canguro.jpg"));
        otros.add(new Icono("https://i.postimg.cc/2S6Nf5HX/steamuserimages-a-akamaihd.jpg"));
        otros.add(new Icono("https://i.postimg.cc/C5Y9rMB7/aa76c2e228f5ec0b3b48fa6ac8a03e19.jpg"));
        otros.add(new Icono("https://i.postimg.cc/k5Ts8xW7/uh8k55l5cfr91.webp"));
        otros.add(new Icono("https://i.postimg.cc/zB9nSpvY/f3bfe93a99019c2744e60aecff4e6277.jpg"));
        otros.add(new Icono("https://i.postimg.cc/TPJcTYRw/Imagen-de-Whats-App-2024-11-03-a-las-21-43-40-8384072a.jpg"));
    }
    private void cambiarFotoDePerfil(Icono icono){
            String fotoNuevaUrl = icono.getUrl();
            PerfilRequest perfilRequest = new PerfilRequest(nuevoNombrePerfil,fotoNuevaUrl);
            Call<Void> call = perfilServiceApi.actualizarPerfil(idPerfil,perfilRequest);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful()&& response.code()==204){
                        Log.i(TAG,"Icono de perfil Actualizado");
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable throwable) {
                    Log.e("SeleccionarIcono","Error actualizando icono de perfil"+throwable.getMessage());
                }
            });

        }
    @Override
    public void onItemClick(Icono icono) {
        cambiarFotoDePerfil(icono);
        finish();
    }
    private int obtenerPerfilActual(){
        return getIntent().getIntExtra("idPerfil", -1); // -1 es el valor predeterminado si "idPerfil" no existe
    }
    private String obtenerNombreActual(){
        return getIntent().getStringExtra("nombrePerfil"); // -1 es el valor predeterminado si "idPerfil" no existe
    }
}
