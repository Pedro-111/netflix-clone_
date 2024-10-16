package com.example.netflix_clone.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.netflix_clone.Model.Perfil;
import com.example.netflix_clone.Model.Perfiles;
import com.example.netflix_clone.R;

import java.util.List;

public class PerfilSeleccionAdapter extends RecyclerView.Adapter<PerfilSeleccionAdapter.PerfilViewHolder> {
    private List<Perfiles> perfiles;
    private OnPerfilSelectedListener listener;

    public interface OnPerfilSelectedListener {
        void onPerfilSelected(Perfiles perfil);
    }

    public PerfilSeleccionAdapter(List<Perfiles> perfiles, OnPerfilSelectedListener listener) {
        this.perfiles = perfiles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PerfilViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_perfil_seleccion, parent, false);
        return new PerfilViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PerfilViewHolder holder, int position) {
        holder.bind(perfiles.get(position));
    }

    @Override
    public int getItemCount() {
        return perfiles.size();
    }

    class PerfilViewHolder extends RecyclerView.ViewHolder {
        ImageView perfilImageView;
        TextView nombrePerfilTextView;

        PerfilViewHolder(@NonNull View itemView) {
            super(itemView);
            perfilImageView = itemView.findViewById(R.id.perfilImageView);
            nombrePerfilTextView = itemView.findViewById(R.id.nombrePerfilTextView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onPerfilSelected(perfiles.get(position));
                }
            });
        }

        void bind(Perfiles perfil) {
            nombrePerfilTextView.setText(perfil.getNombre());

            Glide.with(itemView.getContext())
                    .load(perfil.getFotoPerfilUrl())
                    .transform(new RoundedCorners(16))
                    .into(perfilImageView);
        }
    }
}