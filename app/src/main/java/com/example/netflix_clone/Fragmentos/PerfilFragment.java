package com.example.netflix_clone.Fragmentos;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.netflix_clone.Model.Perfil;
import com.example.netflix_clone.Model.RetrofitClient;
import com.example.netflix_clone.R;
import com.example.netflix_clone.Service.PerfilServiceApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilFragment extends Fragment {

    private TextView textNombre;
    private ImageView imagenPerfil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mi_perfil, container, false);
        textNombre = view.findViewById(R.id.textNombre);
        imagenPerfil = view.findViewById(R.id.imagen_perfil);

        int idPerfil = obtenerPerfilSeleccionado();
        if (idPerfil != -1) {
            cargarDatosPerfil(idPerfil);
        } else {
            Toast.makeText(requireContext(), "No se ha seleccionado un perfil", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void cargarDatosPerfil(int idPerfil) {
        PerfilServiceApi apiService = RetrofitClient.getPerfilClient("https://apilogin.somee.com", getContext()).create(PerfilServiceApi.class);

        Call<Perfil> call = apiService.obtenerPerfil(idPerfil);

        call.enqueue(new Callback<Perfil>() {
            @Override
            public void onResponse(Call<Perfil> call, Response<Perfil> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Perfil perfil = response.body();
                    // Mostrar los datos del perfil
                    textNombre.setText(perfil.getNombre());
                    Glide.with(getContext()).load(perfil.getFotoPerfilUrl()).into(imagenPerfil);
                    Toast.makeText(requireContext(), "ID: " + perfil.getIdPerfil(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(requireContext(), "Nombre: " + perfil.getNombre(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(requireContext(), "Foto URL: " + perfil.getFotoPerfilUrl(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error al cargar el perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Perfil> call, Throwable t) {
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int obtenerPerfilSeleccionado() {
        SharedPreferences prefs = getContext().getSharedPreferences("MyApp", MODE_PRIVATE);
        return prefs.getInt("idPerfil", -1);  // Retorna -1 si no hay un perfil guardado
    }
}
