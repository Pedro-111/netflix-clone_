package com.example.netflix_clone.Adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import com.example.netflix_clone.Model.Icono;
import com.example.netflix_clone.R;

import java.util.List;

public class IconoAdapter extends RecyclerView.Adapter<IconoAdapter.ViewHolder> {
    private List<Icono> lista;
    private Context context;
    private IconoAdapter.OnItemClickListener listener;
    private LoadingCompleteListener loadingCompleteListener;
    private int loadedItems = 0;

    public interface LoadingCompleteListener {
        void onAllItemsLoaded();
    }

    public IconoAdapter(List<Icono> lista, Context context, OnItemClickListener listener) {
        this.lista = lista;
        this.context = context;
        this.listener = listener;
    }

    public IconoAdapter(List<Icono> lista, Context context, OnItemClickListener listener, LoadingCompleteListener loadingCompleteListener) {
        this.lista = lista;
        this.context = context;
        this.listener = listener;
        this.loadingCompleteListener = loadingCompleteListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Icono icono);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_perfil_icono, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Icono icono = lista.get(position);
        holder.bind(icono, listener);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void actualizarLista(List<Icono> nuevaLista) {
        this.lista = nuevaLista;
        loadedItems = 0; // Resetear contador cuando se actualiza la lista
        notifyDataSetChanged();
    }

    public List<Icono> getItems() {
        return lista;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imagenRecomendada;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenRecomendada = itemView.findViewById(R.id.icono_perfil);
        }

        void bind(final Icono icono, final OnItemClickListener listener) {
            // Carga la imagen, considerando si es un GIF o no
            if (icono.getUrl().endsWith(".gif")) {
                Glide.with(context)
                        .asGif()
                        .load(icono.getUrl())
                        .placeholder(R.drawable.skeleton_background)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .fitCenter()
                        .into(imagenRecomendada);
            } else {
                Glide.with(context)
                        .load(icono.getUrl())
                        .placeholder(R.drawable.skeleton_background)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .fitCenter()
                        .into(imagenRecomendada);
            }

            // Configura el listener para el clic en el elemento
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(icono);
                }
            });
        }
    }
}