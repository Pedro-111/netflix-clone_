package com.example.netflix_clone;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.netflix_clone.Model.Request.LoginRequest;
import com.example.netflix_clone.Model.Response.LoginResponse;
import com.example.netflix_clone.Model.Response.TokenResponse;
import com.example.netflix_clone.Model.RetrofitClient;
import com.example.netflix_clone.Service.AuthServiceApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText emailField, passwordField;
    private Button login, signupButton, forgotPasswordButton;
    private SharedPreferences sharedPreferences;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Botón de retroceso
        backButton = findViewById(R.id.backButton);


        // Otros botones y funcionalidad
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton);
        signupButton = findViewById(R.id.signupButton);
        login = findViewById(R.id.loginButton);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        setButtons();

    }
    private void setButtons(){
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
        signupButton.setOnClickListener(v->{
            Intent intent = new Intent(LoginActivity.this,WelcomeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void performLogin() {
        String correo = emailField.getText().toString().trim();
        String clave = passwordField.getText().toString().trim();

        if (correo.isEmpty() || clave.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Por favor, ingrese correo y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthServiceApi authServiceApi = RetrofitClient.getAuthClient("https://apilogin.somee.com", this).create(AuthServiceApi.class);
        LoginRequest loginRequest = new LoginRequest(correo, clave);
        Call<LoginResponse> call = authServiceApi.login(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse.isSuccess()) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token", loginResponse.getToken());
                        editor.putString("refreshToken", loginResponse.getRefreshToken());
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } else {
                        Toast.makeText(LoginActivity.this, "Correo o Contraseña Incorrectos " + loginResponse.isSuccess(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}