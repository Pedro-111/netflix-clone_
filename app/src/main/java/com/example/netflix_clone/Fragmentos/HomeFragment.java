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
        series.add(new Content("Better Call Saul", "https://example.com/better_call_saul.jpg"));
        series.add(new Content("Breaking Bad", "https://example.com/breaking_bad.jpg"));
        series.add(new Content("House", "https://example.com/house.jpg"));
        return series;
    }

    private List<Content> getDramaticSeries() {
        // Populate this with your actual data
        List<Content> series = new ArrayList<>();
        series.add(new Content("Elite", "https://example.com/elite.jpg"));
        series.add(new Content("Dexter", "https://example.com/dexter.jpg"));
        series.add(new Content("Sex Education", "https://example.com/sex_education.jpg"));
        return series;
    }

    @Override
    public void onItemClick(Content content) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("content", content);
        startActivity(intent);
    }

}