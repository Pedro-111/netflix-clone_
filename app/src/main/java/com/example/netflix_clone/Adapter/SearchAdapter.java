package com.example.netflix_clone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.netflix_clone.Activity.DetailActivity;
import com.example.netflix_clone.Model.Content;
import com.example.netflix_clone.R;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "SearchAdapter";
    private List<Content> contentList;
    private Context context;
    private boolean isLoadingMore = false;
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    public SearchAdapter(List<Content> contentList, Context context) {
        this.contentList = contentList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentViewHolder) {
            Content content = contentList.get(position);
            ((ContentViewHolder) holder).bind(content);
        }
    }

    @Override
    public int getItemCount() {
        return contentList.size() + (isLoadingMore ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == contentList.size() && isLoadingMore) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setLoadingMore(boolean loadingMore) {
        if (this.isLoadingMore != loadingMore) {
            this.isLoadingMore = loadingMore;
            if (loadingMore) {
                notifyItemInserted(contentList.size());
            } else {
                notifyItemRemoved(contentList.size());
            }
        }
    }

    public void addContent(List<Content> newContent) {
        if (newContent != null && !newContent.isEmpty()) {
            int startPosition = contentList.size();
            contentList.addAll(newContent);
            notifyItemRangeInserted(startPosition, newContent.size());
        }
    }

    public void clear() {
        int size = contentList.size();
        contentList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder {
        ImageView contentImage;
        TextView contentTitle;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            contentImage = itemView.findViewById(R.id.imagen_recomendada);
            contentTitle = itemView.findViewById(R.id.titulo_recomendado);
        }

        void bind(Content content) {
            if(content.getTitle()!=null){
                contentTitle.setText(content.getTitle());
            }else{
                contentTitle.setText(content.getName());
            }
            Glide.with(context)
                    .load(content.getPoster_path())
                    .transform(new RoundedCorners(16))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_download_for_offline)
                    .error(R.drawable.ic_launcher_background)
                    .into(contentImage);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("content", content);
                Log.d(TAG, "Sending content: " + content.getTitle() + ", ID: " + content.getId());
                context.startActivity(intent);
            });
        }
    }

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}