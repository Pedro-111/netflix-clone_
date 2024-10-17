package com.example.netflix_clone.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.netflix_clone.Model.Perfil;
import com.example.netflix_clone.Model.Perfiles;
import com.example.netflix_clone.R;

import java.util.List;

public class PerfilAdapter extends RecyclerView.Adapter<PerfilAdapter.PerfilViewHolder> {
    private static final String TAG = "PerfilAdapter";
    private List<Perfiles> perfiles;
    private OnPerfilSelectedListener listener;

    public interface OnPerfilSelectedListener {
        void onPerfilSelected(Perfiles perfil);
    }

    public PerfilAdapter(List<Perfiles> perfiles, OnPerfilSelectedListener listener) {
        this.perfiles = perfiles;
        this.listener = listener;
    }
    public PerfilAdapter(List<Perfiles> perfiles) {
        this.perfiles = perfiles;
    }

    @NonNull
    @Override
    public PerfilViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_perfil_dialog, parent, false);
        return new PerfilViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PerfilViewHolder holder, int position) {
        try {
            Perfiles perfil = perfiles.get(position);
            holder.bind(perfil);
        } catch (Exception e) {
            Log.e(TAG, "Error en onBindViewHolder: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return perfiles != null ? perfiles.size() : 0;
    }

    class PerfilViewHolder extends RecyclerView.ViewHolder {
        ImageView perfilImageView;
        TextView nombrePerfilTextView;

        PerfilViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                perfilImageView = itemView.findViewById(R.id.imagenPerfil);
                nombrePerfilTextView = itemView.findViewById(R.id.nombrePerfil);

                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        Perfiles perfilSeleccionado = perfiles.get(position);
                        Log.d(TAG, "Perfil seleccionado: ID=" + perfilSeleccionado.getIdPerfil());
                        listener.onPerfilSelected(perfilSeleccionado);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error en constructor de PerfilViewHolder: " + e.getMessage());
            }
        }

        void bind(Perfiles perfil) {
            try {
                if (perfil != null) {
                    nombrePerfilTextView.setText(perfil.getNombre());

                    String fotoUrl = perfil.getFotoPerfilUrl();
                    if (fotoUrl != null && !fotoUrl.isEmpty()) {
                        Glide.with(itemView.getContext())
                                .load(fotoUrl)
                                .circleCrop()
                                .error(R.drawable.ic_launcher_background)
                                .into(perfilImageView);
                    } else {
                        perfilImageView.setImageResource(R.drawable.ic_launcher_background);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error al vincular perfil: " + e.getMessage());
            }
        }
    }
}