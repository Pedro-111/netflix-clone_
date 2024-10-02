package com.example.netflix_clone.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.netflix_clone.R;

import com.example.netflix_clone.Model.Request.ConfirmarCorreoRequest;
import com.example.netflix_clone.Model.Request.LoginRequest;
import com.example.netflix_clone.Model.Request.RegisterRequest;
import com.example.netflix_clone.Model.Request.TokenRequest;
import com.example.netflix_clone.Model.Response.ConfirmarCorreoResponse;
import com.example.netflix_clone.Model.Response.LoginResponse;
import com.example.netflix_clone.Model.Response.RegisterResponse;
import com.example.netflix_clone.Model.Response.TokenResponse;
import com.example.netflix_clone.Model.Response.TokenValidationResponse;

import java.util.List;

public class PerfilAdapter extends RecyclerView.Adapter<PerfilAdapter.PerfilViewHolder> {
    private List<LoginResponse.Perfil> perfiles;
    private OnPerfilSelectedListener listener;

    public interface OnPerfilSelectedListener {
        void onPerfilSelected(LoginResponse.Perfil perfil);
    }

    public PerfilAdapter(List<LoginResponse.Perfil> perfiles, OnPerfilSelectedListener listener) {
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
        LoginResponse.Perfil perfil = perfiles.get(position);
        holder.bind(perfil);
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
                if (position != RecyclerView.NO_POSITION) {
                    listener.onPerfilSelected(perfiles.get(position));
                }
            });
        }

        void bind(LoginResponse.Perfil perfil) {
            nombrePerfilTextView.setText(perfil.getNombre());
            // Cargar la imagen del perfil usando Glide o Picasso
            Glide.with(itemView.getContext())
                    .load(perfil.getFotoPerfilUrl())
                    .circleCrop()
                    .into(perfilImageView);
        }
    }
}
