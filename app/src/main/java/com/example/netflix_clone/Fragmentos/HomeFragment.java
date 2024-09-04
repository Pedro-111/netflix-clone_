package com.example.netflix_clone.Fragmentos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix_clone.Adapter.ContentAdapter;
import com.example.netflix_clone.DetailActivity;
import com.example.netflix_clone.Model.Content;
import com.example.netflix_clone.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements ContentAdapter.OnItemClickListener {

    private RecyclerView acclaimedSeriesRecyclerView;
    private RecyclerView dramaticSeriesRecyclerView;
    private ContentAdapter acclaimedSeriesAdapter;
    private ContentAdapter dramaticSeriesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        acclaimedSeriesRecyclerView = view.findViewById(R.id.acclaimed_series_recycler_view);
        dramaticSeriesRecyclerView = view.findViewById(R.id.dramatic_series_recycler_view);

        setupRecyclerViews();

        return view;
    }

    private void setupRecyclerViews() {
        List<Content> acclaimedSeries = getAcclaimedSeries();
        List<Content> dramaticSeries = getDramaticSeries();

        acclaimedSeriesAdapter = new ContentAdapter(acclaimedSeries, getContext(), this);
        dramaticSeriesAdapter = new ContentAdapter(dramaticSeries, getContext(), this);

        acclaimedSeriesRecyclerView.setAdapter(acclaimedSeriesAdapter);
        dramaticSeriesRecyclerView.setAdapter(dramaticSeriesAdapter);
    }

    private List<Content> getAcclaimedSeries() {
        // Populate this with your actual data
        List<Content> series = new ArrayList<>();
        series.add(new Content("Peaky Blinders","https://wallpaperaccess.com/full/1087735.jpg","Descripcion"));
        series.add(new Content("Better Call Saul", "https://wallpaperaccess.com/full/1091801.jpg"));
        series.add(new Content("Breaking Bad", "https://wallpaperaccess.com/full/5840994.jpg"));
        series.add(new Content("House", "https://wallpaperaccess.com/full/1827383.jpg"));
        return series;
    }

    private List<Content> getDramaticSeries() {
        // Populate this with your actual data
        List<Content> series = new ArrayList<>();
        series.add(new Content("Elite", "https://wallpaperaccess.com/full/2281944.jpg"));
        series.add(new Content("Dexter", "https://wallpaperaccess.com/full/1088998.jpg"));
        series.add(new Content("Sex Education", "https://wallpaperaccess.com/full/1557429.png"));
        return series;
    }

    @Override
    public void onItemClick(Content content) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("content", content);
        startActivity(intent);
    }

}