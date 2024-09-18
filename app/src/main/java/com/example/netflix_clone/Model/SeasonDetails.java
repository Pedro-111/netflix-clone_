package com.example.netflix_clone.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SeasonDetails {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("season_number")
    private int seasonNumber;

    @SerializedName("episodes")
    private List<Episode> episodes;

    public SeasonDetails() {
    }

    public SeasonDetails(int id, String name, int seasonNumber, List<Episode> episodes) {
        this.id = id;
        this.name = name;
        this.seasonNumber = seasonNumber;
        this.episodes = episodes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }
}
