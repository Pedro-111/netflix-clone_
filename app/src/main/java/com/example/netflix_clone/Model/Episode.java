package com.example.netflix_clone.Model;

import com.google.gson.annotations.SerializedName;

public class Episode {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("episode_number")
    private int episodeNumber;

    @SerializedName("overview")
    private String overview;

    @SerializedName("still_path")
    private String stillPath;

    @SerializedName("air_date")
    private String airDate;

    public Episode() {
    }

    public Episode(int id, String name, int episodeNumber, String overview, String stillPath, String airDate) {
        this.id = id;
        this.name = name;
        this.episodeNumber = episodeNumber;
        this.overview = overview;
        this.stillPath = stillPath;
        this.airDate = airDate;
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

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(int episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getStillPath() {
        return stillPath;
    }

    public void setStillPath(String stillPath) {
        this.stillPath = stillPath;
    }

    public String getAirDate() {
        return airDate;
    }

    public void setAirDate(String airDate) {
        this.airDate = airDate;
    }
}
