package com.example.netflix_clone.Model;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.example.netflix_clone.Service.VideoDownloadServiceApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideoDownloader {
    private final Context context;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final OkHttpClient.Builder httpClient;
    private final VideoDownloadServiceApi videoDownloadService;

    public interface DownloadCallback {
        void onDownloadStarted(String fileName);
        void onProgressUpdate(int progress, String downloadedSize, String totalSize);
        void onDownloadComplete(boolean success, String fileName);
        void onError(String errorMessage);
    }

    public VideoDownloader(Context context) {
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());

        httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://juanchanque-001-site1.ctempurl.com/")
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        videoDownloadService = retrofit.create(VideoDownloadServiceApi.class);
    }

    public void downloadVideo(String urlYoutube, DownloadCallback callback) {
        Call<ResponseBody> call = videoDownloadService.downloadBestVideo(urlYoutube);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String fileName = getFileNameFromResponse(response);
                    long contentLength = getContentLength(response);
                    mainHandler.post(() -> callback.onDownloadStarted(fileName));
                    executorService.execute(() -> {
                        boolean success = guardarVideo(response.body(), fileName, contentLength, callback);
                        mainHandler.post(() -> callback.onDownloadComplete(success, fileName));
                    });
                } else {
                    mainHandler.post(() -> callback.onError("Error en la respuesta: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mainHandler.post(() -> callback.onError("Error al llamar al API: " + t.getMessage()));
            }
        });
    }

    private boolean guardarVideo(ResponseBody responseBody, String fileName, long contentLength, DownloadCallback callback) {
        try {
            File video = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];
                long fileSizeDownloaded = 0;
                long startTime = System.currentTimeMillis();
                long lastUpdateTime = startTime;

                inputStream = responseBody.byteStream();
                outputStream = new FileOutputStream(video);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;

                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastUpdateTime > 500) {  // Actualizar cada 500ms
                        final long downloadedSize = fileSizeDownloaded;
                        mainHandler.post(() -> updateProgress(downloadedSize, contentLength, callback));
                        lastUpdateTime = currentTime;

                        // Actualizar el timeout basado en la velocidad de descarga
                        long elapsedSeconds = (currentTime - startTime) / 1000;
                        if (elapsedSeconds > 0) {
                            long bytesPerSecond = fileSizeDownloaded / elapsedSeconds;
                            long remainingBytes = contentLength - fileSizeDownloaded;
                            long estimatedRemainingSeconds = remainingBytes / bytesPerSecond;
                            updateTimeout(estimatedRemainingSeconds);
                        }
                    }
                }

                outputStream.flush();
                return true;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            mainHandler.post(() -> callback.onError("Error al guardar el archivo: " + e.getMessage()));
            return false;
        }
    }

    private void updateProgress(long downloaded, long total, DownloadCallback callback) {
        int progress = (int) ((downloaded * 100) / total);
        String downloadedMB = String.format("%.2f", downloaded / (1024.0 * 1024.0));
        String totalMB = String.format("%.2f", total / (1024.0 * 1024.0));

        callback.onProgressUpdate(progress, downloadedMB, totalMB);
    }

    private void updateTimeout(long estimatedRemainingSeconds) {
        long newTimeout = Math.max(60, estimatedRemainingSeconds + 30);  // Mínimo 60 segundos, más 30 segundos de margen
        httpClient.readTimeout(newTimeout, TimeUnit.SECONDS)
                .writeTimeout(newTimeout, TimeUnit.SECONDS);
    }

    private String getFileNameFromResponse(Response<ResponseBody> response) {
        String contentDisposition = response.headers().get("content-disposition");
        String fileName = "video_descargado.mp4"; // Nombre por defecto

        if (contentDisposition != null && contentDisposition.contains("filename=")) {
            Pattern pattern = Pattern.compile("filename\\*?=(UTF-8'')?([^;]+)");
            Matcher matcher = pattern.matcher(contentDisposition);
            if (matcher.find()) {
                fileName = matcher.group(2);
                try {
                    fileName = URLDecoder.decode(fileName, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                fileName = fileName.replaceAll("\"", "");
            }
        }

        return sanitizeFileName(fileName);
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private long getContentLength(Response<ResponseBody> response) {
        String contentLength = response.headers().get("content-length");
        return contentLength != null ? Long.parseLong(contentLength) : -1L;
    }

    public void shutdown() {
        executorService.shutdown();
    }
}