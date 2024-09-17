package com.example.netflix_clone.Adapter;

import android.content.Context;
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
import com.example.netflix_clone.Model.Content;
import com.example.netflix_clone.R;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
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
                .load(content.getPoster_path())
                .transform(new RoundedCorners(16)) // Redondea los bordes de la imagen
                .into(holder.contentImage);

        // Añade un log aquí para verificar que tienes datos
        Log.d("SearchAdapter", "Binding title: " + content.getTitle());
        Log.d("SearchAdapter", "Binding image path: " + content.getPoster_path());
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
            contentImage = itemView.findViewById(R.id.imagen_recomendada); // Asegúrate de que coincida con el ID en XML
            contentTitle = itemView.findViewById(R.id.titulo_recomendado); // Asegúrate de que coincida con el ID en XML
        }
    }
}

