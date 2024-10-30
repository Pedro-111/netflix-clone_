package com.example.netflix_clone.Model.Request;

public class MeGustaDTO {
    private String tmdbId;

    public MeGustaDTO(String tmdbId) {
        this.tmdbId = tmdbId;
    }

    public String getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(String tmdbId) {
        this.tmdbId = tmdbId;
    }
}
