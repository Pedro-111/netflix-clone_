package com.example.netflix_clone.Model.Request;

public class MiListaRequest {
    private int tmdbId;
    private String tipo;

    public MiListaRequest(int tmdbId, String tipo) {
        this.tmdbId = tmdbId;
        this.tipo = tipo;
    }
}
