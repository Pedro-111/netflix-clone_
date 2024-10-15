package com.example.netflix_clone.Model;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import android.net.Uri;
import androidx.core.content.FileProvider;
import java.io.File;


public class VideoStorageManager {
    private static final String PREF_NAME = "DownloadedVideos";
    private static final String KEY_VIDEOS = "videos";
    private SharedPreferences preferences;
    private Gson gson;
    private Context context;

    public VideoStorageManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveVideo(VideoItem video) {
        List<VideoItem> videos = getVideos();
        videos.add(video);
        saveVideos(videos);
    }

    public List<VideoItem> getVideos() {
        String json = preferences.getString(KEY_VIDEOS, null);
        if (json == null) {
            return new ArrayList<>();
        } else {
            Type type = new TypeToken<List<VideoItem>>(){}.getType();
            return gson.fromJson(json, type);
        }
    }

    private void saveVideos(List<VideoItem> videos) {
        String json = gson.toJson(videos);
        preferences.edit().putString(KEY_VIDEOS, json).apply();
    }

    public void deleteVideo(VideoItem video) {
        List<VideoItem> videos = getVideos();
        videos.remove(video);
        saveVideos(videos);
        // Eliminar el archivo físico
        File file = new File(video.getVideoPath());
        if (file.exists()) {
            file.delete();
        }
    }

    public Uri getVideoUri(String videoPath) {
        File videoFile = new File(videoPath);
        return FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", videoFile);
    }
}