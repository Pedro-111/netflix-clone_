package com.example.netflix_clone.Model.Request;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("nombre")
    private String nombre;
    private String correo;
    private String clave;

    public RegisterRequest(String nombre, String correo, String clave) {
        this.nombre = nombre;
        this.correo = correo;
        this.clave = clave;
    }
}
