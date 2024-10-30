package com.example.netflix_clone.Model.Request;

public class MeGustaDTO {
    private String tmdbId;
    private String tipo;
    public MeGustaDTO(String tmdbId,String tipo) {
        this.tmdbId = tmdbId;
        this.tipo = tipo;
    }

    public String getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(String tmdbId) {
        this.tmdbId = tmdbId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
