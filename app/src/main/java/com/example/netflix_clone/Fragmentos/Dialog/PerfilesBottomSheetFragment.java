package com.example.netflix_clone.Fragmentos.Dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix_clone.Adapter.PerfilAdapter;
import com.example.netflix_clone.Fragmentos.PerfilFragment;
import com.example.netflix_clone.Model.AppDatabase;
import com.example.netflix_clone.Model.Dao.PerfilDao;
import com.example.netflix_clone.Model.Perfiles;
import com.example.netflix_clone.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class PerfilesBottomSheetFragment extends BottomSheetDialogFragment {

    private RecyclerView recyclerView;
    private PerfilAdapter perfilAdapter;
    private PerfilDao perfilDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_perfiles, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewPerfiles);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        perfilDao = AppDatabase.getInstance(requireContext()).perfilDao();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        perfilAdapter = new PerfilAdapter(new ArrayList<>(), new PerfilAdapter.OnPerfilSelectedListener() {
            @Override
            public void onPerfilSelected(Perfiles perfil) {
                actualizarPerfilSeleccionado(perfil);
                dismiss();
            }
        });
        recyclerView.setAdapter(perfilAdapter);

        cargarPerfiles();
    }

    private void cargarPerfiles() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Perfiles> perfiles = perfilDao.obtenerPerfiles();
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        perfilAdapter = new PerfilAdapter(perfiles, new PerfilAdapter.OnPerfilSelectedListener() {
                            @Override
                            public void onPerfilSelected(Perfiles perfil) {
                                actualizarPerfilSeleccionado(perfil);
                                dismiss();
                            }
                        });
                        recyclerView.setAdapter(perfilAdapter);
                    }
                });
            }
        }).start();
    }

    private void actualizarPerfilSeleccionado(Perfiles perfil) {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        prefs.edit().putInt("idPerfil", perfil.getIdPerfil()).apply();
        if (getParentFragment() instanceof PerfilFragment) {
            ((PerfilFragment) getParentFragment()).cargarDatosPerfil(perfil.getIdPerfil());
        }
    }
}
