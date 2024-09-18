package com.example.netflix_clone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.netflix_clone.DetailActivity;
import com.example.netflix_clone.Model.Content;
import com.example.netflix_clone.R;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private static final String TAG = "SearchAdapter";
    private List<Content> contentList;
    private Context context;

    public SearchAdapter(List<Content> contentList, Context context) {
        this.contentList = contentList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Content content = contentList.get(position);
        holder.contentTitle.setText(content.getTitle());
        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500" + content.getPoster_path())
                .transform(new RoundedCorners(16))
                .into(holder.contentImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("content", content);
            Log.d(TAG, "Sending content: " + content.getTitle() + ", ID: " + content.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView contentImage;
        TextView contentTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contentImage = itemView.findViewById(R.id.imagen_recomendada);
            contentTitle = itemView.findViewById(R.id.titulo_recomendado);
        }
    }
}