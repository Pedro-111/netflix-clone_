package com.example.netflix_clone.Model.Response;

public class RegisterResponse {
    private boolean isSuccess;
    private boolean duplicate;
    private String message;

    public boolean isSuccess() {
        return isSuccess;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public String getMessage() {
        return message;
    }
}
