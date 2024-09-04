package com.example.netflix_clone.Fragmentos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix_clone.Adapter.MobileGamesAdapter;

import com.example.netflix_clone.Model.Game;
import com.example.netflix_clone.R;

import java.util.ArrayList;
import java.util.List;

public class GamesFragment extends Fragment {

    private RecyclerView mobileGamesRecyclerView;

    public GamesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_games, container, false);

        mobileGamesRecyclerView = view.findViewById(R.id.mobile_games_recycler_view);
        setupMobileGamesRecyclerView();

        return view;
    }

    private void setupMobileGamesRecyclerView() {
        List<Game> games = new ArrayList<>();
        games.add(new Game("Snake.io", "Recién agregado", R.drawable.snake_io));
//        games.add(new Game("Bloons TD 6", "Estrategia", R.drawable.bloons_td6));
//        games.add(new Game("Bob Esponja: A cocinar", "Simulación", R.drawable.bob_esponja));
        // Añade más juegos según sea necesario

        MobileGamesAdapter adapter = new MobileGamesAdapter(games);
        mobileGamesRecyclerView.setAdapter(adapter);
        mobileGamesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }
}