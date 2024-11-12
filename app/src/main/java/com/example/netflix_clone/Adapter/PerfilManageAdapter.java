package com.example.netflix_clone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.netflix_clone.Model.Perfil;
import com.example.netflix_clone.Model.Perfiles;
import com.example.netflix_clone.R;

import java.util.ArrayList;
import java.util.List;

public class PerfilManageAdapter extends RecyclerView.Adapter<PerfilManageAdapter.PerfilViewHolder>{
    private List<Perfiles> perfiles;
    private Context context;
    private OnPerfilClickListener listener;
    public interface OnPerfilClickListener{
        void onPerfilClick(Perfiles perfil);
    }
    public PerfilManageAdapter(Context context, OnPerfilClickListener listener){
        this.context = context;
        this.perfiles = new ArrayList<>();
        this.listener = listener;
    }


    @NonNull
    @Override
    public PerfilViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_perfil_manage,parent,false);
        int screenWidth = parent.getWidth();
        int itemWidth = (screenWidth - (2 * parent.getResources().getDimensionPixelSize(R.dimen.grid_spacing))) / 2;
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.width = itemWidth;
        lp.height = itemWidth; // O el alto que prefieras
        view.setLayoutParams(lp);
        return new PerfilViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PerfilViewHolder holder, int position) {
        Perfiles perfil = perfiles.get(position);
        Glide.with(context).load(perfil.getFotoPerfilUrl()).into(holder.profileImage);

        holder.profileName.setText(perfil.getNombre());

        holder.itemView.setOnClickListener(v->{
            if(listener!=null){
                listener.onPerfilClick(perfil);
            }
        });
    }

    @Override
    public int getItemCount() {
        return perfiles.size();
    }
    public void updatePerfiles(List<Perfiles> perfiles){
        this.perfiles = perfiles;
        notifyDataSetChanged();
    }
    static class PerfilViewHolder extends RecyclerView.ViewHolder{
        ImageView profileImage;
        TextView profileName;

        public PerfilViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profile_image_manage);
            profileName = itemView.findViewById(R.id.tv_profile_name_manage);
        }
    }
}
