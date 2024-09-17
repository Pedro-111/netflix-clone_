package com.example.netflix_clone.Model;

import java.io.Serializable;

public class Content implements Serializable {
    private String name;
    private String title;
    private String overview;
    private String poster_path;
    private String mediaType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPoster_path() {
        return "https://image.tmdb.org/t/p/w500" + poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    // Helper method to get the display title (either name or title)
    public String getDisplayTitle() {
        return title != null ? title : name;
    }
}