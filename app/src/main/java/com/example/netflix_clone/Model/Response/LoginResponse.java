package com.example.netflix_clone.Model.Response;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("isSuccess")
    private boolean isSuccess;
    @SerializedName("token")
    private String token;
    @SerializedName("refreshToken")
    private String refreshToken;

    public boolean isSuccess(){
        return this.isSuccess;
    }
    public String getToken(){
        return this.token;
    }
    public String getRefreshToken(){
        return this.refreshToken;
    }
}
