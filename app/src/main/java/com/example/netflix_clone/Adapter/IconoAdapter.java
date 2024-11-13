package com.example.netflix_clone.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.netflix_clone.Model.Content;
import com.example.netflix_clone.Model.Icono;
import com.example.netflix_clone.Model.Perfiles;
import com.example.netflix_clone.R;

import java.util.List;

public class IconoAdapter extends RecyclerView.Adapter<IconoAdapter.ViewHolder> {
    private List<Icono> lista;
    private Context context;
    private IconoAdapter.OnItemClickListener listener;
    public IconoAdapter(List<Icono> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }
    public IconoAdapter(List<Icono> lista, Context context,OnItemClickListener listener) {
        this.lista = lista;
        this.context = context;
        this.listener  = listener;
    }
    public interface OnItemClickListener {
        void onItemClick(Icono icono);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mi_lista, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Icono icono = lista.get(position);
        holder.bind(icono,listener);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void actualizarLista(List<Icono> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    public List<Icono> getItems() {
        return lista;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imagenRecomendada;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenRecomendada = itemView.findViewById(R.id.imagen_recomendada_mi_lista);
        }
        void bind(final Icono icono, final OnItemClickListener listener) {
            // Carga la imagen, considerando si es un GIF o no
            if (icono.getUrl().endsWith(".gif")) {
                Glide.with(context).asGif().load(icono.getUrl()).into(imagenRecomendada);
            } else {
                Glide.with(context).load(icono.getUrl()).into(imagenRecomendada);
            }

            // Configura el listener para el clic en el elemento
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(icono);
                }
            });
        }
    }
}