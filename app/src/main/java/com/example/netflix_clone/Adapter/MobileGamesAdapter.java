package com.example.netflix_clone.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix_clone.Model.Game;
import com.example.netflix_clone.R;

import java.util.List;

public class MobileGamesAdapter extends RecyclerView.Adapter<MobileGamesAdapter.GameViewHolder> {

    private List<Game> games;

    public MobileGamesAdapter(List<Game> games) {
        this.games = games;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mobile_game, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = games.get(position);
        holder.bind(game);
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    static class GameViewHolder extends RecyclerView.ViewHolder {
        ImageView gameImage;
        TextView gameTitle;
        TextView gameCategory;

        GameViewHolder(@NonNull View itemView) {
            super(itemView);
            gameImage = itemView.findViewById(R.id.game_image);
            gameTitle = itemView.findViewById(R.id.game_title);
            gameCategory = itemView.findViewById(R.id.game_category);
        }

        void bind(Game game) {
            gameImage.setImageResource(game.getImageResourceId());
            gameTitle.setText(game.getTitle());
            gameCategory.setText(game.getCategory());
        }
    }
}