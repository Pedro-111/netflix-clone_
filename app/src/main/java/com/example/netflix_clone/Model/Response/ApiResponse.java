package com.example.netflix_clone.Model.Response;

import com.example.netflix_clone.Model.Content;

import java.util.List;

public class ApiResponse {
    private List<Content> results;
    public List<Content> getResults(){
        return results;
    }
}
