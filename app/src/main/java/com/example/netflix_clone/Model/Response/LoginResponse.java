package com.example.netflix_clone.Model.Response;

import com.example.netflix_clone.Model.Perfil;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoginResponse {
    @SerializedName("isSuccess")
    private boolean isSuccess;
    @SerializedName("token")
    private String token;
    @SerializedName("refreshToken")
    private String refreshToken;

    private List<Perfil> perfiles;

    public boolean isSuccess(){
        return this.isSuccess;
    }
    public String getToken(){
        return this.token;
    }
    public String getRefreshToken(){
        return this.refreshToken;
    }

    public List<Perfil> getPerfiles() {
        return perfiles;
    }
}
