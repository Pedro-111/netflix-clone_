package com.example.netflix_clone.Model;

public class Icono {
    private String url;
    private boolean isLoaded;
    public Icono(String url) {
        this.url = url;
        this.isLoaded = false;
    }
    public Icono() {
    }

    public String getUrl() {
        return url;
    }
    public boolean isLoaded() { return isLoaded; }
    public void setLoaded(boolean loaded) { isLoaded = loaded; }
}
