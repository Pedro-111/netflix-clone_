package com.example.netflix_clone.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.netflix_clone.Model.Descarga;

import java.util.List;

@Dao
public interface DescargaDao {
    @Insert
    long insert(Descarga descarga);
    @Query("SELECT * FROM descargas WHERE idPerfil = :idPerfil")
    List<Descarga> getDescargasByPerfil(int idPerfil);
    @Query("SELECT * FROM descargas")
    List<Descarga> getAllDescargas();
}
