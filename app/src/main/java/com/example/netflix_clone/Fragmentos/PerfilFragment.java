package com.example.netflix_clone.Fragmentos;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
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
import com.example.netflix_clone.Activity.WelcomeActivity;
import com.example.netflix_clone.Fragmentos.Dialog.MenuPerfilBottomSheetFragment;
import com.example.netflix_clone.Fragmentos.Dialog.PerfilesBottomSheetFragment;
import com.example.netflix_clone.Model.AppDatabase;
import com.example.netflix_clone.Model.Perfiles;
import com.example.netflix_clone.R;

public class PerfilFragment extends Fragment implements MenuPerfilBottomSheetFragment.MenuPerfilListener{

    private TextView textNombre;
    private ImageView imagenPerfil;
    private AppDatabase perfilDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mi_perfil, container, false);
        textNombre = view.findViewById(R.id.textNombre);
        imagenPerfil = view.findViewById(R.id.imagen_perfil);

        perfilDatabase = AppDatabase.getInstance(getContext());

        int idPerfil = obtenerPerfilSeleccionado();
        if (idPerfil != -1) {
            cargarDatosPerfil(idPerfil);
        } else {
            Toast.makeText(requireContext(), "No se ha seleccionado un perfil", Toast.LENGTH_SHORT).show();
        }

        view.findViewById(R.id.layoutCambiarPerfil).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarPerfilesBottomSheet();
            }
        });

        ImageView buttonMenu= view.findViewById(R.id.menu_mi_perfil);
        buttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMenuPerfilBottomSheet();
            }
        });
        return view;
    }
    private void mostrarMenuPerfilBottomSheet(){
        MenuPerfilBottomSheetFragment bottomSheetFragment = new MenuPerfilBottomSheetFragment();
        bottomSheetFragment.setListener((MenuPerfilBottomSheetFragment.MenuPerfilListener) this);
        bottomSheetFragment.show(getChildFragmentManager(), "MenuPerfilBottomSheet");
    }
    @Override
    public void onCerrarSesionClicked() {
        cerrarSesion();
    }

    private void cerrarSesion() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(getContext(), WelcomeActivity.class);
        startActivity(intent);

        Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
    }
    private void mostrarPerfilesBottomSheet() {
        PerfilesBottomSheetFragment bottomSheetFragment = new PerfilesBottomSheetFragment();
        bottomSheetFragment.show(getChildFragmentManager(), "PerfilesBottomSheet");
    }

    public void cargarDatosPerfil(final int idPerfil) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Perfiles perfil = perfilDatabase.perfilDao().obtenerPerfilPorId(idPerfil);
                if (perfil != null) {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textNombre.setText(perfil.getNombre());
                            Glide.with(getContext()).load(perfil.getFotoPerfilUrl()).into(imagenPerfil);
                        }
                    });
                } else {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Error al cargar el perfil", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private int obtenerPerfilSeleccionado() {
        SharedPreferences prefs = getContext().getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        return prefs.getInt("idPerfil", -1);
    }
}
