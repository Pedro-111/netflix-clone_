package com.example.netflix_clone.Model.Request;

import com.google.gson.annotations.SerializedName;

public class TokenRequest {
    @SerializedName("tokenExpirado")
    private String tokenExpirado;
    @SerializedName("refreshToken")
    private String refreshToken;

    public TokenRequest(String tokenExpirado,String refreshToken){
        this.tokenExpirado =tokenExpirado;
        this.refreshToken = refreshToken;
    }
}
