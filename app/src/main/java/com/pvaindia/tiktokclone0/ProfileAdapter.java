package com.pvaindia.tiktokclone0;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pvaindia.tiktokclone0.model.HomeModel;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    Context context;
    List<HomeModel>homeModels;
    public ProfileAdapter(Context context, List<HomeModel> homeModels) {
        this.context=context;
        this.homeModels=homeModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.profile,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(context)
                .load(homeModels.get(position).getLink())
                .centerCrop()
                .into(holder.img);

        holder.img.setOnClickListener(v->{
            context.startActivity(new Intent(context, VideoViewActvity.class)
            .putExtra("link",homeModels.get(position).getLink())
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

            Activity activity = (Activity) context;
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        });

        holder.delete.setOnClickListener(v -> {


            AlertDialog.Builder deleteDialogBuilder = new AlertDialog.Builder(context);
            deleteDialogBuilder.setTitle("Confirm");
            deleteDialogBuilder.setMessage("Are you sure to delete this video?");
            deleteDialogBuilder.setCancelable(false);

            deleteDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("PVA")
                            .child("Videos")
                            .orderByChild("id")
                            .equalTo(homeModels.get(position).getId())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot data : snapshot.getChildren()) {
                                            data.getRef().removeValue();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                    homeModels.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, homeModels.size());
                }
            });

            deleteDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog deleteDialog = deleteDialogBuilder.create();
            deleteDialog.show();

        });

        Activity activity = (Activity) context;

        if (activity instanceof MainActivity) {
            Log.i("testritik", "MainActivity");
            holder.delete.setVisibility(View.VISIBLE);
        }
//        else if (activity instanceof UserProfileActivity) {
//            Log.i("testritik", "UserProfileActivity");
//            holder.delete.setVisibility(View.GONE);
//        }

//        Log.i("testritik", context.toString());


    }



    @Override
    public int getItemCount() {
        return homeModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        ImageView delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img=itemView.findViewById(R.id.img);
            delete=itemView.findViewById(R.id.delete_video);
        }
    }
}
