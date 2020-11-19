package com.pvaindia.tiktokclone0;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    ArrayList list,list1,list2;
    Context context;

    public CommentAdapter(ArrayList list,ArrayList list1,ArrayList list2, Context context) {
        this.list = list;
        this.list1=list1;
        this.list2=list2;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (position==list.size()-1){
            holder.layout.setVisibility(View.GONE);
        }
        holder.comment.setText(list.get(position)+"");
        holder.uname.setText(list1.get(position)+"");
        Glide.with(context)
                .load(list2.get(position))
                .placeholder(R.drawable.ic_baseline_person_24)
                .error(R.drawable.ic_baseline_person_24)
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView comment,uname;
        LinearLayout layout;
        ImageView img;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            comment=itemView.findViewById(R.id.comment);
            uname=itemView.findViewById(R.id.uname);
            img=itemView.findViewById(R.id.img);
            layout=itemView.findViewById(R.id.layout);
        }
    }
}
