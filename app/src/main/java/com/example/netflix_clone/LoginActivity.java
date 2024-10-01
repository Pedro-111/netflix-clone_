package com.example.netflix_clone;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.netflix_clone.Model.Request.ConfirmarCorreoRequest;
import com.example.netflix_clone.Model.Request.LoginRequest;
import com.example.netflix_clone.Model.Response.ConfirmarCorreoResponse;
import com.example.netflix_clone.Model.Response.LoginResponse;
import com.example.netflix_clone.Model.Response.TokenResponse;
import com.example.netflix_clone.Model.RetrofitClient;
import com.example.netflix_clone.Service.AuthServiceApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText emailField, passwordField;
    private Button login, signupButton, forgotPasswordButton, confirmarCorreo, continueButton;
    private SharedPreferences sharedPreferences;
    private ImageButton backButton;
    private AuthServiceApi authServiceApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        initializeServices();
        setButtons();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton);
        signupButton = findViewById(R.id.signupButton);
        login = findViewById(R.id.loginButton);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        confirmarCorreo = findViewById(R.id.confirmacionCorreo);
    }

    private void initializeServices() {
        sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        authServiceApi = RetrofitClient.getAuthServiceApi(this);
    }

    private void setButtons() {
        backButton.setOnClickListener(v -> navigateToWelcome());
        login.setOnClickListener(v -> performLogin());
        signupButton.setOnClickListener(v -> navigateToWelcome());
        confirmarCorreo.setOnClickListener(v -> {
            if (emailField.getText().toString().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Email requerido", Toast.LENGTH_SHORT).show();
            } else {
                showEmailConfirmationDialog(emailField.getText().toString());
            }
        });
    }

    private void navigateToWelcome() {
        Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showEmailConfirmationDialog(String email) {
        final Dialog dialog = new Dialog(LoginActivity.this, R.style.FullScreenDialogStyle);
        dialog.setContentView(R.layout.dialog_confirmacion_email);

        TextView emailSentText = dialog.findViewById(R.id.emailSentText);
        if (emailSentText != null) {
            String message = "¡Ya casi terminamos! Te enviamos un email a " + email;
            SpannableString spannableString = new SpannableString(message);
            int emailStart = message.indexOf(email);
            int emailEnd = emailStart + email.length();
            spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), emailStart, emailEnd, 0);
            emailSentText.setText(spannableString);
        }

        setupDialogButtons(dialog, email);

        dialog.show();
    }

    private void setupDialogButtons(Dialog dialog, String email) {
        dialog.findViewById(R.id.helpText).setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://help.netflix.com"))));

        dialog.findViewById(R.id.loginText).setOnClickListener(v -> redirigirALogin());

        continueButton = dialog.findViewById(R.id.continueButton);
        if (continueButton != null) {
            continueButton.setOnClickListener(v -> openEmailApp());
        }

        Button confirmarCodigoButton = dialog.findViewById(R.id.boton_de_confirmacion);
        EditText codigoInput = dialog.findViewById(R.id.codigoDeConfirmacion);
        if (confirmarCodigoButton != null && codigoInput != null) {
            confirmarCodigoButton.setOnClickListener(v ->
                    confirmarCorreo(email, codigoInput.getText().toString()));
        }
    }

    private void openEmailApp() {
        Intent emailIntent = new Intent(Intent.ACTION_MAIN);
        emailIntent.addCategory(Intent.CATEGORY_APP_EMAIL);
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(Intent.createChooser(emailIntent, "Elige una aplicación de correo"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(LoginActivity.this, "No hay aplicaciones de correo instaladas.", Toast.LENGTH_SHORT).show();
        }
    }

    private void redirigirALogin() {
        // Ya estamos en LoginActivity, solo necesitamos refrescar la actividad
        recreate();
    }

    private void performLogin() {
        String correo = emailField.getText().toString().trim();
        String clave = passwordField.getText().toString().trim();

        if (correo.isEmpty() || clave.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Por favor, ingrese correo y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest loginRequest = new LoginRequest(correo, clave);
        Call<LoginResponse> call = authServiceApi.login(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse.isSuccess()) {
                        saveTokens(loginResponse.getToken(), loginResponse.getRefreshToken());
                        navigateToMain();
                    } else {
                        Toast.makeText(LoginActivity.this, "Correo o Contraseña Incorrectos", Toast.LENGTH_SHORT).show();
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

    private void saveTokens(String token, String refreshToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.putString("refreshToken", refreshToken);
        editor.apply();
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void confirmarCorreo(String correo, String codigo) {
        ConfirmarCorreoRequest confirmarCorreoRequest = new ConfirmarCorreoRequest(correo, codigo);
        Call<ConfirmarCorreoResponse> call = authServiceApi.confirmarCorreo(confirmarCorreoRequest);
        call.enqueue(new Callback<ConfirmarCorreoResponse>() {
            @Override
            public void onResponse(Call<ConfirmarCorreoResponse> call, Response<ConfirmarCorreoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(LoginActivity.this, "Correo confirmado. Por favor, inicie sesión.", Toast.LENGTH_SHORT).show();
                        redirigirALogin();
                    } else {
                        Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ConfirmarCorreoResponse> call, Throwable throwable) {
                Toast.makeText(LoginActivity.this, "Error. " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}