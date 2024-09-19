package com.example.netflix_clone;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        LottieAnimationView animationView = findViewById(R.id.lottieAnimationView);
        animationView.setAnimation(R.raw.inicio_netflix);
        animationView.playAnimation();

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 4300); // Duración de la animación en milisegundos
    }
}
