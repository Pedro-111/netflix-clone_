package com.example.netflix_clone.Model.Request;

public class PerfilRequest {
    private String nombre;
    private String fotoPerfilUrl;

    public PerfilRequest(String fotoPerfilUrl){
        this.fotoPerfilUrl = fotoPerfilUrl;
    }
    public PerfilRequest(String nombre,String fotoPerfilUrl){
        this.nombre = nombre;
        this.fotoPerfilUrl = fotoPerfilUrl;
    }
    public String getNombre() {
        return nombre;
    }

    public String getFotoPerfilUrl() {
        return fotoPerfilUrl;
    }
}
