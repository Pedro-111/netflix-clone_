package com.example.netflix_clone.Model.Response;

import com.example.netflix_clone.Model.VideoData;

import java.util.List;

public class TrailerResponse {
    private boolean isSuccess;
    private List<VideoData> videos;
    private String message;

    public boolean isSuccess() {
        return isSuccess;
    }

    public List<VideoData> getVideos() {
        return videos;
    }

    public String getMessage() {
        return message;
    }
}
