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
import com.example.netflix_clone.R;

import java.util.List;

public class PerfilAdapter extends RecyclerView.Adapter<PerfilAdapter.PerfilViewHolder> {
    private static final String TAG = "PerfilAdapter";
    private List<Perfil> perfiles;
    private OnPerfilSelectedListener listener;

    public interface OnPerfilSelectedListener {
        void onPerfilSelected(Perfil perfil);
    }

    public PerfilAdapter(List<Perfil> perfiles, OnPerfilSelectedListener listener) {
        this.perfiles = perfiles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PerfilViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_perfil, parent, false);
        return new PerfilViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PerfilViewHolder holder, int position) {
        try {
            Perfil perfil = perfiles.get(position);
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
                perfilImageView = itemView.findViewById(R.id.perfilImageView);
                nombrePerfilTextView = itemView.findViewById(R.id.nombrePerfilTextView);

                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        Perfil perfilSeleccionado = perfiles.get(position);
                        Log.d(TAG, "Perfil seleccionado: ID=" + perfilSeleccionado.getIdPerfil());
                        listener.onPerfilSelected(perfilSeleccionado);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error en constructor de PerfilViewHolder: " + e.getMessage());
            }
        }

        void bind(Perfil perfil) {
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