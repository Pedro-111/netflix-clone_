package com.example.netflix_clone.Model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "descargas")
public class Descarga {
    @PrimaryKey(autoGenerate = true)
    public int idDescarga;
    public int idPerfil;
    public int tmdbId;
    public String tipo;
    public String estado;
    public long fechaDescarga;
    public String rutaArchivo;
    public long tamanoArchivo;
    public String nombreArchivo;
    public String posterPath;
//    public Integer numeroTemporada;
//    public Integer numeroEpisodio;
}
