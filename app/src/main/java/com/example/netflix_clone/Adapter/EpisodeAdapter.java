package com.example.netflix_clone.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.netflix_clone.Model.Episode;
import com.example.netflix_clone.R;

import java.util.ArrayList;
import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder> {

    private static final String TAG = "EpisodeAdapter";
    private List<Episode> episodes;
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w200";

    public EpisodeAdapter(List<Episode> episodes) {
        this.episodes = episodes != null ? new ArrayList<>(episodes) : new ArrayList<>();
    }

    @NonNull
    @Override
    public EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_episode, parent, false);
        return new EpisodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeViewHolder holder, int position) {
        if (position < episodes.size()) {
            Episode episode = episodes.get(position);
            Log.d(TAG, "Binding episode at position " + position + ": " + episode.getName());
            holder.bind(episode);
            holder.loadImage(episode.getStillPath());
        }
    }

    @Override
    public int getItemCount() {
        return episodes != null ? episodes.size() : 0;
    }

    public void updateEpisodes(List<Episode> newEpisodes) {
        if (newEpisodes != null) {
            this.episodes = new ArrayList<>(newEpisodes);
            notifyDataSetChanged();
            Log.d(TAG, "Updated episodes list. New size: " + episodes.size());
        } else {
            Log.w(TAG, "Attempted to update with null episodes list");
        }
    }

    public void addEpisodes(List<Episode> moreEpisodes) {
        if (moreEpisodes != null && !moreEpisodes.isEmpty()) {
            int startPosition = episodes.size();
            episodes.addAll(moreEpisodes);
            Log.d(TAG, "Adding " + moreEpisodes.size() + " episodes starting at position " + startPosition);
            notifyItemRangeInserted(startPosition, moreEpisodes.size());
        }
    }

    public void loadImageForPosition(int position) {
        notifyItemChanged(position);
    }

    public static class EpisodeViewHolder extends RecyclerView.ViewHolder {
        TextView episodeTitle;
        TextView episodeDescription;
        ImageView episodeThumbnail;

        EpisodeViewHolder(View itemView) {
            super(itemView);
            episodeTitle = itemView.findViewById(R.id.episode_title);
            episodeDescription = itemView.findViewById(R.id.episode_description);
            episodeThumbnail = itemView.findViewById(R.id.episode_thumbnail);
        }

        void bind(Episode episode) {
            if (episode != null) {
                episodeTitle.setText(String.format("Episodio %d: %s", episode.getEpisodeNumber(), episode.getName()));
                episodeDescription.setText(episode.getOverview());
            }
        }

        void loadImage(String stillPath) {
            if (stillPath != null && !stillPath.isEmpty()) {
                String fullUrl = IMAGE_BASE_URL + stillPath;
                Log.d(TAG, "Loading image from: " + fullUrl);

                Glide.with(itemView.getContext())
                        .load(fullUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_download_for_offline)
                        .error(R.drawable.ic_launcher_background)
                        .listener(new RequestListener<android.graphics.drawable.Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                                Log.e(TAG, "Error loading image: " + (e != null ? e.getMessage() : "unknown error"));
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, Target<android.graphics.drawable.Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d(TAG, "Image loaded successfully");
                                return false;
                            }
                        })
                        .into(episodeThumbnail);
            } else {
                Log.w(TAG, "Still path is null or empty");
                episodeThumbnail.setImageResource(R.drawable.ic_launcher_background);
            }
        }
    }
}