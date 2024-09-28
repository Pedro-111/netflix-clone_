package com.example.netflix_clone.Model.Request;

public class TokenRequest {
    private String tokenExpirado;
    private String refreshToken;

    public TokenRequest(String tokenExpirado,String refreshToken){
        this.tokenExpirado =tokenExpirado;
        this.refreshToken = refreshToken;
    }
}
