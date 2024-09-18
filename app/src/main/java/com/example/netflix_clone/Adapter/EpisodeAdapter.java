package com.example.netflix_clone.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.netflix_clone.Model.Episode;
import com.example.netflix_clone.R;

import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder> {

    private List<Episode> episodes;
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w200";

    public EpisodeAdapter(List<Episode> episodes) {
        this.episodes = episodes;
    }

    @NonNull
    @Override
    public EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_episode, parent, false);
        return new EpisodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeViewHolder holder, int position) {
        Episode episode = episodes.get(position);
        holder.episodeTitle.setText(episode.getName());
        holder.episodeDescription.setText(episode.getOverview());

        // Cargar la imagen del episodio
        if (episode.getStillPath() != null && !episode.getStillPath().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(IMAGE_BASE_URL + episode.getStillPath())
                    .into(holder.episodeThumbnail);
        } else {
            // Cargar una imagen por defecto si no hay imagen disponible
           // holder.episodeThumbnail.setImageResource(R.drawable.default_episode_image);
        }
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public void updateEpisodes(List<Episode> newEpisodes) {
        this.episodes = newEpisodes;
        notifyDataSetChanged();
    }

    static class EpisodeViewHolder extends RecyclerView.ViewHolder {
        TextView episodeTitle;
        TextView episodeDescription;
        ImageView episodeThumbnail;

        EpisodeViewHolder(View itemView) {
            super(itemView);
            episodeTitle = itemView.findViewById(R.id.episode_title);
            episodeDescription = itemView.findViewById(R.id.episode_description);
            episodeThumbnail = itemView.findViewById(R.id.episode_thumbnail);
        }
    }
}