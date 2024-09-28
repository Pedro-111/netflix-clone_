package com.example.netflix_clone.Model.Response;

public class TokenResponse {
    private boolean isSuccess;
    private String token;
    private String refreshToken;

    public boolean isSuccess(){
        return  this.isSuccess;
    }
    public String getToken(){
        return this.token;
    }
    public String getRefreshToken(){
        return this.refreshToken;
    }
}
