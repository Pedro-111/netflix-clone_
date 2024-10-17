package com.example.netflix_clone.Fragmentos.Dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.netflix_clone.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MenuPerfilBottomSheetFragment extends BottomSheetDialogFragment {

    public interface MenuPerfilListener {
        void onCerrarSesionClicked();
    }

    private MenuPerfilListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_menu_perfil, container, false);

        view.findViewById(R.id.cerrarSesion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCerrarSesionClicked();
                }
                dismiss();
            }
        });

        // Aquí puedes agregar más listeners para las otras opciones del menú

        return view;
    }

    public void setListener(MenuPerfilListener listener) {
        this.listener = listener;
    }
}