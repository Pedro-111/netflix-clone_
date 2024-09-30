package com.example.netflix_clone.Model.Request;

public class ConfirmarCorreoRequest {
    private String correo;
    private String codigo;

    public ConfirmarCorreoRequest(String correo, String codigo) {
        this.correo = correo;
        this.codigo = codigo;
    }
}
