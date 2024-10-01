package com.example.netflix_clone;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.netflix_clone.Adapter.ImagePagerAdapter;
import com.example.netflix_clone.Model.Request.ConfirmarCorreoRequest;
import com.example.netflix_clone.Model.Request.RegisterRequest;
import com.example.netflix_clone.Model.Response.ConfirmarCorreoResponse;
import com.example.netflix_clone.Model.Response.RegisterResponse;
import com.example.netflix_clone.Model.RetrofitClient;
import com.example.netflix_clone.Service.AuthServiceApi;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = "WelcomeActivity";
    private ViewPager viewPager;
    private int[] imagenesIds = {R.drawable.imagen_cartelera, R.drawable.imagen_welcome_2, R.drawable.imagen_welcome_3, R.drawable.imagen_welcome_4};
    private ImagePagerAdapter adapter;
    private TextView title1, title2, title3, subtitle, subtitle2;
    private TextView iniciarSesion, privacidad;
    private SharedPreferences sharedPreferences;
    private AuthServiceApi authServiceApi;
    private Button startButton;
    private EditText emailInput, passwordInput;

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
        startButton = findViewById(R.id.startButton);
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
        authServiceApi = RetrofitClient.getAuthServiceApi(this);
    }

    private void setupListeners() {
        if (iniciarSesion != null) {
            iniciarSesion.setOnClickListener(v -> {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
            });
        }

        if (privacidad != null) {
            privacidad.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://help.netflix.com/legal/privacy?netflixsource=android&fromApp=true"));
                startActivity(intent);
            });
        }

        if (startButton != null) {
            startButton.setOnClickListener(v -> showCustomDialog());
        }
    }

    private void verificarSesion() {
        String token = sharedPreferences.getString("token", null);

        if (token != null) {
            // Hay un token, asumimos que la sesión es válida
            // El interceptor se encargará de renovar el token si es necesario
            iniciarMainActivity();
        } else {
            // No hay token, permanecemos en WelcomeActivity
            Log.d(TAG, "verificarSesion: No hay token, permaneciendo en WelcomeActivity");
        }
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
        if (title1 != null) title1.setText(text1);
        if (title2 != null) title2.setText(text2);
        if (title3 != null) title3.setText(text3);
        if (subtitle != null) subtitle.setText(subText1);
        if (subtitle2 != null) subtitle2.setText(subText2);
    }

    private void showCustomDialog() {
        final Dialog dialog = new Dialog(WelcomeActivity.this, R.style.FullScreenDialogStyle);
        dialog.setContentView(R.layout.dialog_registrar_cuenta_email);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        TextView closeButton = dialog.findViewById(R.id.closeButton);
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> dialog.dismiss());
        }

        Button submitButton = dialog.findViewById(R.id.submitButton);
        if (submitButton != null) {
            submitButton.setOnClickListener(v -> {
                emailInput = dialog.findViewById(R.id.emailInput);
                passwordInput = dialog.findViewById(R.id.passswordInput);

                if (emailInput != null && passwordInput != null) {
                    verificarEmailCreado(emailInput.getText().toString(), passwordInput.getText().toString());
                } else {
                    Log.e(TAG, "showCustomDialog: emailInput or passwordInput is null");
                }
            });
        }

        dialog.show();
    }

    private void verificarEmailCreado(String email, String password) {
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            Log.e(TAG, "verificarEmailCreado: email or password is null or empty");
            Toast.makeText(WelcomeActivity.this, "Por favor, ingrese email y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterRequest usuario = new RegisterRequest("", email, password);
        Call<RegisterResponse> call = authServiceApi.registrarUsuario(usuario);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isDuplicate()) {
                        // Si el correo es duplicado ya existe y se redirige a Iniciar Sesión
                        redirigirALogin();
                    } else {
                        // Aquí abrimos el segundo diálogo sin cerrar el primero
                        showEmailConfirmationDialog(email);
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable throwable) {
                Log.e(TAG, "verificarEmailCreado: Error en la llamada a la API", throwable);
                Toast.makeText(WelcomeActivity.this, "Error al verificar el email. Intente nuevamente.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmailConfirmationDialog(String email) {
        final Dialog dialog = new Dialog(WelcomeActivity.this, R.style.FullScreenDialogStyle);
        dialog.setContentView(R.layout.dialog_confirmacion_email);

        TextView emailSentText = dialog.findViewById(R.id.emailSentText);
        Button confirmarCodigoButton = dialog.findViewById(R.id.boton_de_confirmacion);
        EditText codigoInput = dialog.findViewById(R.id.codigoDeConfirmacion);

        if (emailSentText != null) {
            String message = "¡Ya casi terminamos! Te enviamos un email a " + email;
            SpannableString spannableString = new SpannableString(message);

            int emailStart = message.indexOf(email);
            int emailEnd = emailStart + email.length();
            spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), emailStart, emailEnd, 0);

            emailSentText.setText(spannableString);
        }

        if (confirmarCodigoButton != null && codigoInput != null) {
            confirmarCodigoButton.setOnClickListener(v -> {
                confirmarCorreo(email, codigoInput.getText().toString());
            });
        }

        TextView helpText = dialog.findViewById(R.id.helpText);
        if (helpText != null) {
            helpText.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://help.netflix.com"));
                startActivity(intent);
            });
        }

        TextView loginText = dialog.findViewById(R.id.loginText);
        if (loginText != null) {
            loginText.setOnClickListener(v -> {
                redirigirALogin();
            });
        }

        Button continueButton = dialog.findViewById(R.id.continueButton);
        if (continueButton != null) {
            continueButton.setOnClickListener(v -> {
                Intent emailIntent = new Intent(Intent.ACTION_MAIN);
                emailIntent.addCategory(Intent.CATEGORY_APP_EMAIL);
                emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                try {
                    startActivity(Intent.createChooser(emailIntent, "Elige una aplicación de correo"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(WelcomeActivity.this, "No hay aplicaciones de correo instaladas.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        dialog.show();
    }

    private void confirmarCorreo(String correo, String codigo) {
        ConfirmarCorreoRequest confirmarCorreoRequest = new ConfirmarCorreoRequest(correo, codigo);
        Call<ConfirmarCorreoResponse> call = authServiceApi.confirmarCorreo(confirmarCorreoRequest);
        call.enqueue(new Callback<ConfirmarCorreoResponse>() {
            @Override
            public void onResponse(Call<ConfirmarCorreoResponse> call, Response<ConfirmarCorreoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(WelcomeActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(WelcomeActivity.this, "Redirigiendo a Iniciar Sesión", Toast.LENGTH_SHORT).show();
                        redirigirALogin();
                    } else {
                        Toast.makeText(WelcomeActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ConfirmarCorreoResponse> call, Throwable throwable) {
                Toast.makeText(WelcomeActivity.this, "Error. " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void redirigirALogin() {
        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}