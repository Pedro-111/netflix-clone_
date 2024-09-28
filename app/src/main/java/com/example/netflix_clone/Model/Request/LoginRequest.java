package com.example.netflix_clone.Model.Request;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("correo")
    private String correo;
    @SerializedName("clave")
    private String clave;
    public LoginRequest(String correo,String clave){
        this.correo = correo;
        this.clave = clave;
    }
}
