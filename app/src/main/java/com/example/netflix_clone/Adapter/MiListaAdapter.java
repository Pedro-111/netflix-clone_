package com.example.netflix_clone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.netflix_clone.Model.Content;
import com.example.netflix_clone.Model.Response.MiListaResponse;
import com.example.netflix_clone.R;

import java.util.List;

public class MiListaAdapter extends RecyclerView.Adapter<MiListaAdapter.ViewHolder> {
    private List<Content> lista;
    private Context context;

    public MiListaAdapter(List<Content> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mi_lista, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Content item = lista.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void actualizarLista(List<Content> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    public List<Content> getItems() {
        return lista;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imagenRecomendada;
        private TextView tituloRecomendado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenRecomendada = itemView.findViewById(R.id.imagen_recomendada_mi_lista);
            tituloRecomendado = itemView.findViewById(R.id.titulo_recomendado_mi_lista);
        }

        public void bind(Content item) {
            tituloRecomendado.setText(item.getTitle());

            String imagePath = "https://image.tmdb.org/t/p/w500" + item.getPoster_path();
            Glide.with(context)
                    .load(imagePath)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imagenRecomendada);
        }
    }
}