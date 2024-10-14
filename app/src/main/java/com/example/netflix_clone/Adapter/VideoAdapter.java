package com.example.netflix_clone.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.netflix_clone.Model.VideoItem;
import com.example.netflix_clone.R;

import java.util.List;


public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<VideoItem> videoItems;
    private OnItemClickListener listener;

    public VideoAdapter(List<VideoItem> videoItems) {
        this.videoItems = videoItems;
    }

    public interface OnItemClickListener {
        void onItemClick(VideoItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoItem item = videoItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return videoItems.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        private TextView seriesTitle;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            seriesTitle = itemView.findViewById(R.id.series_title);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(videoItems.get(position));
                }
            });
        }

        public void bind(VideoItem item) {
            seriesTitle.setText(item.getSeriesTitle());
        }
    }

    public void updateItems(List<VideoItem> newItems) {
        videoItems.clear();
        videoItems.addAll(newItems);
        notifyDataSetChanged();
    }
}