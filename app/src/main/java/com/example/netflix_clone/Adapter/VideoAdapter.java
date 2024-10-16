package com.example.netflix_clone.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.netflix_clone.Model.Descarga;
import com.example.netflix_clone.Model.VideoItem;
import com.example.netflix_clone.R;

import java.util.List;


public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<Descarga> descargas;
    private OnItemClickListener listener;

    public VideoAdapter(List<Descarga> descargas) {
        this.descargas = descargas;
    }

    public interface OnItemClickListener {
        void onItemClick(Descarga item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Descarga item = descargas.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return descargas.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        private TextView seriesTitle;
        private ImageView posterImage;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            seriesTitle = itemView.findViewById(R.id.series_title);
            posterImage = itemView.findViewById(R.id.poster_image);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(descargas.get(position));
                }
            });
        }

        public void bind(Descarga item) {
            seriesTitle.setText(item.nombreArchivo);

            // Cargar el póster usando Glide
            if (item.posterPath != null && !item.posterPath.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(item.posterPath)
                        .placeholder(R.drawable.ic_download_for_offline) // Asegúrate de tener una imagen placeholder
                        .error(R.drawable.ic_launcher_background) // Asegúrate de tener una imagen de error
                        .into(posterImage);
            } else {
                posterImage.setImageResource(R.drawable.ic_launcher_foreground); // Asegúrate de tener una imagen por defecto
            }

            // Puedes agregar más información si lo deseas
            // Por ejemplo, mostrar si es una película o serie
            String tipo = item.tipo != null ? item.tipo : "Desconocido";
            seriesTitle.setText(item.nombreArchivo + " (" + tipo + ")");
        }
    }

    public void updateItems(List<Descarga> newItems) {
        descargas.clear();
        descargas.addAll(newItems);
        notifyDataSetChanged();
    }
}