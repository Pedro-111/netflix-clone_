package com.example.netflix_clone.Adapter;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.netflix_clone.Model.Episode;
import com.example.netflix_clone.R;

import java.util.ArrayList;
import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "EpisodeAdapter";
    private List<Episode> episodes;
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w200";
    private boolean isLoadingMore = false;
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    public EpisodeAdapter() {
        this.episodes = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_episode, parent, false);
        return new EpisodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EpisodeViewHolder) {
            Episode episode = episodes.get(position);
            ((EpisodeViewHolder) holder).bind(episode);
            ((EpisodeViewHolder) holder).loadImage(episode.getStillPath());
        }
    }

    @Override
    public int getItemCount() {
        return episodes.size() + (isLoadingMore ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == episodes.size() && isLoadingMore) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setLoadingMore(boolean loadingMore) {
        if (this.isLoadingMore != loadingMore) {
            this.isLoadingMore = loadingMore;
            if (loadingMore) {
                notifyItemInserted(episodes.size());
            } else {
                notifyItemRemoved(episodes.size());
            }
        }
    }

    public void addEpisodes(List<Episode> newEpisodes) {
        if (newEpisodes != null && !newEpisodes.isEmpty()) {
            int startPosition = episodes.size();
            episodes.addAll(newEpisodes);
            notifyItemRangeInserted(startPosition, newEpisodes.size());
        }
    }

    public void clear() {
        int size = episodes.size();
        episodes.clear();
        notifyItemRangeRemoved(0, size);
    }

    public static class EpisodeViewHolder extends RecyclerView.ViewHolder {
        TextView episodeTitle;
        TextView episodeDescription;
        ImageView episodeThumbnail;
        private RequestBuilder<Drawable> currentRequest;

        EpisodeViewHolder(View itemView) {
            super(itemView);
            episodeTitle = itemView.findViewById(R.id.episode_title);
            episodeDescription = itemView.findViewById(R.id.episode_description);
            episodeThumbnail = itemView.findViewById(R.id.episode_thumbnail);
        }

        void bind(Episode episode) {
            if (episode != null) {
                if (currentRequest != null) {
                    Glide.with(itemView.getContext()).clear(episodeThumbnail);
                }

                episodeTitle.setText(String.format("Episodio %d: %s",
                        episode.getEpisodeNumber(),
                        episode.getName()));

                String description = episode.getOverview();
                if (description != null && !description.isEmpty()) {
                    episodeDescription.setText(description);
                } else {
                    episodeDescription.setText("Sin descripción disponible");
                }
            }
        }

        void loadImage(String stillPath) {
            if (stillPath != null && !stillPath.isEmpty()) {
                String fullUrl = IMAGE_BASE_URL + stillPath;

                // Configurar Glide para cargar la imagen de manera más eficiente
                Glide.with(itemView.getContext())
                        .load(fullUrl)
                        .override(300, 200) // Limitar el tamaño de la imagen
                        .thumbnail(0.1f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_download_for_offline)
                        .error(R.drawable.ic_launcher_background)
                        .dontAnimate() // Evitar animaciones para mejor rendimiento
                        .into(episodeThumbnail);
            } else {
                episodeThumbnail.setImageResource(R.drawable.ic_launcher_background);
            }
        }
    }

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}
