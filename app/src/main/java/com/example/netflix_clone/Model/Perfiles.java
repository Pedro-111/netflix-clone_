package com.example.netflix_clone.Model;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "perfiles")
public class Perfiles {
    @PrimaryKey(autoGenerate = true)
    private int idPerfil; // ID del perfil
    private String nombre; // Nombre del perfil
    private String fotoPerfilUrl;

    public void setFotoPerfilUrl(String fotoPerfilUrl){
        this.fotoPerfilUrl = fotoPerfilUrl;
    }
    public String getFotoPerfilUrl() {
        return fotoPerfilUrl;
    }
    public Perfiles(String nombre) {
        this.nombre = nombre;
    }

    // Getters y Setters
    public int getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(int idPerfil) {
        this.idPerfil = idPerfil;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
