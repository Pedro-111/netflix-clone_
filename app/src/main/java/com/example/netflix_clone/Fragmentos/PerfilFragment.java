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
import com.example.netflix_clone.Model.AppDatabase;
import com.example.netflix_clone.Model.Perfiles;
import com.example.netflix_clone.R;

public class PerfilFragment extends Fragment {

    private TextView textNombre;
    private ImageView imagenPerfil;
    private AppDatabase perfilDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mi_perfil, container, false);
        textNombre = view.findViewById(R.id.textNombre);
        imagenPerfil = view.findViewById(R.id.imagen_perfil);

        // Inicializar la base de datos de Room
        perfilDatabase = AppDatabase.getInstance(getContext());

        // Obtener el ID del perfil seleccionado desde SharedPreferences
        int idPerfil = obtenerPerfilSeleccionado();
        if (idPerfil != -1) {
            cargarDatosPerfil(idPerfil);
        } else {
            Toast.makeText(requireContext(), "No se ha seleccionado un perfil", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void cargarDatosPerfil(int idPerfil) {
        // Consultar el perfil de la base de datos de Room
        new Thread(() -> {
            Perfiles perfil = perfilDatabase.perfilDao().obtenerPerfilPorId(idPerfil);
            if (perfil != null) {
                // Actualizar la interfaz de usuario en el hilo principal
                requireActivity().runOnUiThread(() -> {
                    textNombre.setText(perfil.getNombre());
                    Glide.with(getContext()).load(perfil.getFotoPerfilUrl()).into(imagenPerfil);
                });
            } else {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error al cargar el perfil", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private int obtenerPerfilSeleccionado() {
        SharedPreferences prefs = getContext().getSharedPreferences("MyApp", MODE_PRIVATE);
        return prefs.getInt("idPerfil", -1);  // Retorna -1 si no hay un perfil guardado
    }
}
