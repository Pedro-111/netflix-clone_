package com.example.netflix_clone.Service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface VideoDownloadServiceApi {
    @Streaming
    @GET("/api/Video/download")
    Call<ResponseBody> downloadBestVideo(@Query("url") String url);
}
