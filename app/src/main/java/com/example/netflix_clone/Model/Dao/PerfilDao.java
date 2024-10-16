package com.example.netflix_clone.Model.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.netflix_clone.Model.Perfiles;

import java.util.List;

@Dao
public interface PerfilDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertarPerfil(Perfiles perfil);

    @Query("SELECT * FROM perfiles")

    List<Perfiles> obtenerPerfiles();
    @Update
    void actualizarPerfil(Perfiles perfil);

    @Query("SELECT * FROM perfiles WHERE idPerfil = :id")
    Perfiles obtenerPerfilPorId(int id);
}
