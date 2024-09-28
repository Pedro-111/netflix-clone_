package com.example.netflix_clone;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.netflix_clone.Adapter.ImagePagerAdapter;
import com.example.netflix_clone.Model.Response.PerfilResponse;
import com.example.netflix_clone.Model.Response.TokenValidationResponse;
import com.example.netflix_clone.Model.RetrofitClient;
import com.example.netflix_clone.Service.AuthServiceApi;
import com.example.netflix_clone.Service.PerfilServiceApi;

import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WelcomeActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private int[] imagenesIds = {R.drawable.imagen_cartelera, R.drawable.imagen_welcome_2, R.drawable.imagen_welcome_3, R.drawable.imagen_welcome_4};
    private ImagePagerAdapter adapter;
    private TextView title1, title2, title3, subtitle, subtitle2;
    private TextView iniciarSesion,privacidad;
    private SharedPreferences sharedPreferences;
    private AuthServiceApi authServiceApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        initializeViews();
        setupViewPager();
        initializeServices();
        setupListeners();

        verificarSesion();
    }

    private void initializeViews() {
        title1 = findViewById(R.id.titleText);
        title2 = findViewById(R.id.titleText_2);
        title3 = findViewById(R.id.titleText_3);
        subtitle = findViewById(R.id.subtitleText);
        subtitle2 = findViewById(R.id.subtitleText_2);
        iniciarSesion = findViewById(R.id.inicioSesion);
        viewPager = findViewById(R.id.viewPager);
        privacidad = findViewById(R.id.privacidad);
    }

    private void setupViewPager() {
        adapter = new ImagePagerAdapter(this, imagenesIds);
        viewPager.setAdapter(adapter);

        CircleIndicator indicator = findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                updateTextsForPage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initializeServices() {
        sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        authServiceApi = RetrofitClient.getAuthClient("https://apilogin.somee.com", this).create(AuthServiceApi.class);
    }

    private void setupListeners() {
        iniciarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        privacidad.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://help.netflix.com/legal/privacy?netflixsource=android&fromApp=true"));
            startActivity(intent);
        });
    }

    private void iniciarMainActivity() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    private void updateTextsForPage(int position) {
        switch (position) {
            case 0:
                updateTexts("Series y películas", "ilimitadas y", "mucho más", "Disfruta donde quieras. Cancela", "cuando quieras");
                break;
            case 1:
                updateTexts("", "Hay un plan para", "cada fan", "Planes desde S/ 24.90.", "");
                break;
            case 2:
                updateTexts("", "Cancela online", "cuando quieras", "Únete hoy, no hay motivos para", "esperar.");
                break;
            case 3:
                updateTexts("", "Disfruta donde", "quieras", "Ve contenido en tu teléfono, tablet,", "computadora y TV.");
                break;
        }
    }

    private void updateTexts(String text1, String text2, String text3, String subText1, String subText2) {
        title1.setText(text1);
        title2.setText(text2);
        title3.setText(text3);
        subtitle.setText(subText1);
        subtitle2.setText(subText2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferences == null) {
            sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        }
        verificarSesion();
    }
    private void verificarSesion() {
        Log.d(TAG, "verificarSesion: Iniciando verificación de sesión");
        if (sharedPreferences == null) {
            Log.e(TAG, "verificarSesion: sharedPreferences es nulo, inicializando");
            sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        }

        String token = sharedPreferences.getString("token", null);
        Log.d(TAG, "verificarSesion: Token: " + (token != null ? "presente" : "ausente"));

        if (token != null) {
            Log.d(TAG, "verificarSesion: Intentando validar token con la API");
            Call<TokenValidationResponse> call = authServiceApi.validarToken(token);
            call.enqueue(new Callback<TokenValidationResponse>() {
                @Override
                public void onResponse(Call<TokenValidationResponse> call, Response<TokenValidationResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Log.d(TAG, "verificarSesion: Token válido, iniciando MainActivity");
                        iniciarMainActivity();
                    } else {
                        Log.d(TAG, "verificarSesion: Token inválido");
                        sharedPreferences.edit().remove("token").apply();
                        // Opcional: Mostrar un mensaje al usuario
                        Toast.makeText(WelcomeActivity.this, "Sesión expirada, por favor inicie sesión nuevamente", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<TokenValidationResponse> call, Throwable t) {
                    Log.e(TAG, "verificarSesion: Error de red", t);
                    // Opcional: Mostrar un mensaje al usuario
                    // Toast.makeText(WelcomeActivity.this, "Error de conexión, por favor intente más tarde", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.d(TAG, "verificarSesion: No hay token, permaneciendo en WelcomeActivity");
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);


        }
    }
}