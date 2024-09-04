package com.example.netflix_clone.Model;

public class Game {
    private String title;
    private String category;
    private int imageResourceId;

    public Game(String title, String category, int imageResourceId) {
        this.title = title;
        this.category = category;
        this.imageResourceId = imageResourceId;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
}