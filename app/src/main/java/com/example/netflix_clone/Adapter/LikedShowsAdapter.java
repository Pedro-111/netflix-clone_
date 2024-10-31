package com.example.netflix_clone.Adapter;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.netflix_clone.Model.Content;
import com.example.netflix_clone.R;

import java.util.ArrayList;
import java.util.List;

public class LikedShowsAdapter extends RecyclerView.Adapter<LikedShowsAdapter.ViewHolder> {
    private List<Content> shows = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Content content);
    }

    public LikedShowsAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_me_gusta, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Content show = shows.get(position);
        holder.bind(show, listener);
    }
    // Método para actualizar los datos
    public void updateData(List<Content> newContentList) {
        shows.clear();
        shows.addAll(newContentList);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return shows.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageShowPoster;
        TextView textViewCompartir;
        ImageButton buttonShare;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageShowPoster = itemView.findViewById(R.id.imageShowPoster);
            buttonShare = itemView.findViewById(R.id.buttonShare_me_gusta);
            textViewCompartir = itemView.findViewById(R.id.textViewCompartir);
        }
        void bind(final Content content, final OnItemClickListener listener) {
            Glide.with(itemView.getContext()).load(content.getPoster_path())
                     .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_download_for_offline)
                    .error(R.drawable.ic_launcher_background).into(imageShowPoster);
            Log.d(TAG,"Url desde adaptador: "+content.getPoster_path());
            // Configurar el botón de compartir
            buttonShare.setVisibility(View.VISIBLE);
            buttonShare.setOnClickListener(v -> {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/uri-list");
                shareIntent.putExtra(Intent.EXTRA_TEXT, content.getPoster_path());
                itemView.getContext().startActivity(Intent.createChooser(shareIntent, "Compartir usando"));
            });

            textViewCompartir.setOnClickListener(v -> {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/uri-list");
                shareIntent.putExtra(Intent.EXTRA_TEXT, content.getPoster_path());
                itemView.getContext().startActivity(Intent.createChooser(shareIntent, "Compartir usando"));
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(content);
                }
            });
        }
    }
}
