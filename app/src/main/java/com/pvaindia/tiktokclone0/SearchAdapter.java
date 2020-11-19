package com.pvaindia.tiktokclone0;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pvaindia.tiktokclone0.model.SearchModel;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    List<SearchModel>searchModels;
    Context context;
    public SearchAdapter(Context applicationContext, List<SearchModel> searchModel) {
        this.context=applicationContext;
      this.searchModels=searchModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.search_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(context).load(searchModels.get(position).getLink()).into(holder.img);

        holder.img.setOnClickListener(v->{
            context.startActivity(new Intent(context,VideoViewActvity.class)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra("link",searchModels.get(position).getLink()));
        });
    }

    @Override
    public int getItemCount() {
        return searchModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img=itemView.findViewById(R.id.img);
        }
    }
}
