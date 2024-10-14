package com.example.netflix_clone.Model;
import android.os.Parcel;
import android.os.Parcelable;

public class VideoItem implements Parcelable {
    private String seriesTitle;
    private String videoPath;

    public VideoItem(String seriesTitle, String videoPath) {
        this.seriesTitle = seriesTitle;
        this.videoPath = videoPath;
    }

    protected VideoItem(Parcel in) {
        seriesTitle = in.readString();
        videoPath = in.readString();
    }

    public static final Creator<VideoItem> CREATOR = new Creator<VideoItem>() {
        @Override
        public VideoItem createFromParcel(Parcel in) {
            return new VideoItem(in);
        }

        @Override
        public VideoItem[] newArray(int size) {
            return new VideoItem[size];
        }
    };

    public String getSeriesTitle() {
        return seriesTitle;
    }

    public String getVideoPath() {
        return videoPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(seriesTitle);
        dest.writeString(videoPath);
    }
}
