package com.example.netflix_clone.Model;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.netflix_clone.Model.Dao.DescargaDao;
import com.example.netflix_clone.Model.Dao.PerfilDao;

@Database(entities = {Descarga.class, Perfiles.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DescargaDao descargaDao();
    public abstract PerfilDao perfilDao(); // Agregar el DAO de perfil

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database").fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
