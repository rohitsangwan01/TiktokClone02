package com.pvaindia.tiktokclone0;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.huxq17.download.Pump;
import com.huxq17.download.core.DownloadInfo;
import com.huxq17.download.core.DownloadListener;
import com.pvaindia.tiktokclone0.model.HomeModel;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideosViewHolder> {



    List<HomeModel>homeModels;
    Context context;
    SharedPreferences sp;
    public static  MediaSource mediaSource1=null;
    public static  SimpleExoPlayer simpleExoPlayer=null;
    static String val="null";
    permissionInterface permissionInterface;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;

    public VideosAdapter(Context applicationContext, List<HomeModel> homeModels, permissionInterface permissionInterface) {
        this.context=applicationContext;
        this.homeModels=homeModels;
        this.permissionInterface=permissionInterface;
    }

    static ArrayList<String> currentUserFollowingsList = new ArrayList<>();



    @NonNull
    @Override
    public VideosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VideosViewHolder view = new VideosViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_container_video,
                parent,
                false
        ));

        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VideosViewHolder holder, int position) {

        Pump.newConfigBuilder()
                .setMaxRunningTaskNum(5)
                .setMinUsableStorageSpace(100 * 1024 * 1024 * 1024L);

        sp=context.getSharedPreferences("pref",0);

        if (sp.getString("id", null) != null) {
            getCurrentUserFollowings(holder.redCross,holder.greenTick,holder.pgBarLoadFollow,position);
        }

        //Author's profile pic on Follow button
//        Glide.with(context)
//                .load("https://firebasestorage.googleapis.com/v0/b/tiktokclone-0.appspot.com/o/ProfilePicture%2F" + homeModels.get(position).getUid() + "?alt=media")
//                .placeholder(R.drawable.icon_user_profile)
//                .error(R.drawable.icon_user_profile)
//                .into(holder.profileImage);

        FirebaseDatabase.getInstance()
                .getReference()
                .child("PVA")
                .child("Users")
                .orderByChild("id")
                .equalTo(homeModels.get(position).getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data:snapshot.getChildren()){
                            Glide.with(context)
                                    .load(data.child("image").getValue())
                                    .placeholder(R.drawable.icon_user_profile)
                                    .error(R.drawable.icon_user_profile)
                                    .into(holder.profileImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));

        LoadControl loadControl = new DefaultLoadControl.Builder()
                .setAllocator(new DefaultAllocator(true, 16))
                .setBufferDurationsMs(
                        2000,
                        5000,
                        2000,
                        2000
                )
                .setTargetBufferBytes(-1)
                .setPrioritizeTimeOverSizeThresholds(true)
                .createDefaultLoadControl();



//      holder.simpleExoPlayer=new SimpleExoPlayer.Builder(context).build();
        holder.simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
                context,
                new DefaultRenderersFactory(context),
                trackSelector,
                loadControl,
                null,
                bandwidthMeter
        );

        holder.uname.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra("id", homeModels.get(position).getUid());
            context.startActivity(intent);
            Activity activity = (Activity) context;
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        holder.videoView.getVideoSurfaceView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        float deltaX = x2 - x1;
                        if (Math.abs(deltaX) > MIN_DISTANCE)
                        {
                            Intent intent = new Intent(context, UserProfileActivity.class);
                            intent.putExtra("id", homeModels.get(position).getUid());
                            context.startActivity(intent);
                            Activity activity = (Activity) context;
                            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                        break;
                }
                return false;
            }
        });

        holder.videoView.getVideoSurfaceView().setOnClickListener(v->{
            if (val.equals("null")){
                if (simpleExoPlayer.isPlaying()){
                    holder.imgPause.setVisibility(View.VISIBLE);
                    simpleExoPlayer.setPlayWhenReady(false);
                    val=homeModels.get(position).getId();
                }

            }else {
                val="null";
                holder.imgPause.setVisibility(View.GONE);
                simpleExoPlayer.setPlayWhenReady(true);
            }
        });

        holder.profileImage.setOnClickListener(v -> {

            String uid = homeModels.get(position).getUid();

            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra("id", uid);
            context.startActivity(intent);
            Activity activity = (Activity) context;
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        holder.like.setOnClickListener(v->{
            if (isLogin()) {
                String[] s = homeModels.get(position).getLiked().split("#!split!#");
                ArrayList<String> list = new ArrayList<>(Arrays.asList(s));
                //Like
                if (!list.contains(sp.getString("id", null))) {
                    holder.like.setColorFilter(Color.parseColor("#C20303"), android.graphics.PorterDuff.Mode.SRC_IN);
//                holder.like.setEnabled(false);
                    list.add(sp.getString("id", null));
                    StringBuilder temp = new StringBuilder();
                    for (String id : list) {
                        temp.append("#!split!#").append(id);
                    }
                    homeModels.get(position).setLiked(temp.toString());
                    homeModels.get(position).setLikes(String.valueOf(Integer.parseInt(homeModels.get(position).getLikes()) + 1));
                    holder.LikeVideo(sp.getString("id", null), homeModels.get(position).getId(), homeModels.get(position).getUid());
                    holder.likeCountTextView.setText(Integer.parseInt(holder.likeCountTextView.getText().toString()) + 1 + "");
                }
                //Unlike
                else {
                    holder.like.setColorFilter(Color.parseColor("#e6e6e6"), android.graphics.PorterDuff.Mode.SRC_IN);
//                holder.like.setEnabled(true);
                    list.remove(sp.getString("id", null));
                    StringBuilder temp = new StringBuilder();
                    for (String id : list) {
                        temp.append("#!split!#").append(id);
                    }
                    homeModels.get(position).setLiked(temp.toString());
                    homeModels.get(position).setLikes(String.valueOf(Integer.parseInt(homeModels.get(position).getLikes()) - 1));
                    holder.unlikeVideo(sp.getString("id", null), homeModels.get(position).getId(), homeModels.get(position).getUid());
                    holder.likeCountTextView.setText(Integer.parseInt(holder.likeCountTextView.getText().toString()) - 1 + "");
                }
            }
            else {
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
                Activity activity = (Activity) context;
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        //follow
        holder.redCross.setOnClickListener(v->{

            if (isLogin()) {

                String uid = homeModels.get(position).getUid();

                if (!currentUserFollowingsList.contains(uid)) {

                    //Follow
//                holder.follow.setEnabled(false);
                    holder.redCross.setVisibility(View.INVISIBLE);
                    holder.greenTick.setVisibility(View.VISIBLE);

                    //Update on Video Author's side
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("PVA")
                            .child("Users")
                            .orderByChild("id")
                            .equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Map map = new HashMap<>();
                                    map.put("followersCount", Integer.parseInt(dataSnapshot.child("followersCount")
                                            .getValue(String.class)) + 1 + "");
                                    map.put("followers", dataSnapshot.child("followers")
                                            .getValue(String.class) + "#!split!#" + sp.getString("id", null));
                                    //inbox notification
                                    String inboxItem = sp.getString("id", null) + "&" +
                                            "follow" + "&" +
                                            new Date().getTime();
                                    map.put("inbox", inboxItem + "#!split!#" + dataSnapshot.child("inbox").getValue(String.class));

                                    FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("PVA")
                                            .child("Users")
                                            .child(dataSnapshot.getKey())
                                            .updateChildren(map);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    //Update on Viewer's side
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("PVA")
                            .child("Users")
                            .orderByChild("id")
                            .equalTo(sp.getString("id", null))
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            Map map = new HashMap<>();
                                            map.put("followingsCount", Integer.parseInt(dataSnapshot.child("followingsCount")
                                                    .getValue(String.class)) + 1 + "");
                                            map.put("followings", dataSnapshot.child("followings")
                                                    .getValue(String.class) + "#!split!#" + uid);

                                            FirebaseDatabase.getInstance()
                                                    .getReference()
                                                    .child("PVA")
                                                    .child("Users")
                                                    .child(dataSnapshot.getKey())
                                                    .updateChildren(map);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                } else {
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("id", uid);
                    context.startActivity(intent);
                    Activity activity = (Activity) context;
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
            else {
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
                Activity activity = (Activity) context;
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        //UnFollow
        holder.greenTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogin()) {

                    String uid = homeModels.get(position).getUid();

                    if (currentUserFollowingsList.contains(uid)) {

                        //Unfollow
                        holder.redCross.setVisibility(View.VISIBLE);
                        holder.greenTick.setVisibility(View.INVISIBLE);

                        //Update on Video Author's side
                        FirebaseDatabase.getInstance()
                                .getReference()
                                .child("PVA")
                                .child("Users")
                                .orderByChild("id")
                                .equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        Map map = new HashMap<>();
                                        map.put("followersCount", Integer.parseInt(dataSnapshot.child("followersCount")
                                                .getValue(String.class)) - 1 + "");
                                        map.put("followers", dataSnapshot.child("followers")
                                                .getValue(String.class).replace("#!split!#" + sp.getString("id", null), ""));

                                        FirebaseDatabase.getInstance()
                                                .getReference()
                                                .child("PVA")
                                                .child("Users")
                                                .child(dataSnapshot.getKey())
                                                .updateChildren(map);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        //Update on Viewer's side
                        FirebaseDatabase.getInstance()
                                .getReference()
                                .child("PVA")
                                .child("Users")
                                .orderByChild("id")
                                .equalTo(sp.getString("id", null))
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                Map map = new HashMap<>();
                                                map.put("followingsCount", Integer.parseInt(dataSnapshot.child("followingsCount")
                                                        .getValue(String.class)) - 1 + "");
                                                map.put("followings", dataSnapshot.child("followings")
                                                        .getValue(String.class).replace("#!split!#" + uid, ""));

                                                FirebaseDatabase.getInstance()
                                                        .getReference()
                                                        .child("PVA")
                                                        .child("Users")
                                                        .child(dataSnapshot.getKey())
                                                        .updateChildren(map);
                                            }
                                            // getCurrentUserFollowings();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    } else {
                        Intent intent = new Intent(context, UserProfileActivity.class);
                        intent.putExtra("id", uid);
                        context.startActivity(intent);
                        Activity activity = (Activity) context;
                        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }
                else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                    Activity activity = (Activity) context;
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        holder.share.setOnClickListener(v->{
            //to Make Button UnClickable
            String AppLink = "APP_PLAYSTORE_LINK";
            String url = homeModels.get(position).getLink();
            String Message = "Hey Watch This Video on KALAKAR app"+"\n"+"Get app On Play Stor"+"\n"
                    +AppLink+"\n"+url;


            Intent intent=new Intent( new Intent(Intent.ACTION_SEND));
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT,Message);
            context.startActivity(Intent.createChooser(intent,"Share")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("PVA")
                    .child("Videos")
                    .orderByChild("id")
                    .equalTo(homeModels.get(position).getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        @SuppressWarnings("unchecked")
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                for (DataSnapshot data:snapshot.getChildren()){
                                    Map map=new HashMap();
                                    map.put("shares",Integer.parseInt(data.child("shares").getValue(String.class))+1+"");
                                    FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("PVA")
                                            .child("Videos")
                                            .child(data.getKey())
                                            .updateChildren(map).addOnCompleteListener(task ->
                                            holder.shareCountTextView.setText(Integer.parseInt(holder.shareCountTextView.getText().toString())+1+""));

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

           // String link = homeModels.get(position).getLink();
//            String link = "afac";
//            Task<ShortDynamicLink> dynamicLink= FirebaseDynamicLinks.getInstance().createDynamicLink()
//                    .setLink(Uri.parse(link))
//                    .setDomainUriPrefix("https://kalakar.page.link")
//                    .setAndroidParameters(new DynamicLink.AndroidParameters.Builder("test").setMinimumVersion(1).build())
//                    .buildShortDynamicLink()
//                    .addOnSuccessListener(shortDynamicLink -> {
//
//                        Toast.makeText(context, "OnSuccess Open", Toast.LENGTH_SHORT).show();
//
//                        String mInvitationUrl = shortDynamicLink.getShortLink().toString();
//                        Intent intent=new Intent( new Intent(Intent.ACTION_SEND));
//                        intent.setType("text/plain");
//                        intent.putExtra(Intent.EXTRA_TEXT,homeModels.get(position).getUname()+"\n\n_"+mInvitationUrl+"_");
//                        context.startActivity(Intent.createChooser(intent,"Share")
//                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.e("rohit",e.toString());
//                        //Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
//                        }
//                    });

        });

        holder.comment.setOnClickListener(v->{

//           if (isLogin()) {

            simpleExoPlayer.setPlayWhenReady(false);
            View view = LayoutInflater.from(context).inflate(R.layout.layout_comment, null);


            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("PVA")
                    .child("Videos")
                    .orderByChild("id")
                    .equalTo(homeModels.get(position).getId())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                String[] comment = data.child("comments").getValue(String.class).split("#!comments!#");
                                String[] uname = data.child("commentsUname").getValue(String.class).split("#!uname!#");
                                String[] img = data.child("commentsImg").getValue(String.class).split("#!cmntImg!#");
                                ArrayList<String> list1 = new ArrayList<>(Arrays.asList(comment));
                                ArrayList<String> list2 = new ArrayList<>(Arrays.asList(uname));
                                ArrayList<String> list3 = new ArrayList<>(Arrays.asList(img));
                                Collections.reverse(list1);
                                Collections.reverse(list2);
                                Collections.reverse(list3);
                                RecyclerView recyclerView = view.findViewById(R.id.rv);
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                recyclerView.setAdapter(new CommentAdapter(list1, list2, list3, context));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            EditText edtcomment = view.findViewById(R.id.comment);
            ImageView send = view.findViewById(R.id.send);

            if (!isLogin()) {
//                   edtcomment.setEnabled(false);
                edtcomment.setFocusable(false);
                send.setEnabled(false);
                edtcomment.setOnClickListener(view1 -> {
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                    Activity activity = (Activity) context;
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                });
            }


            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
            bottomSheetDialog.setContentView(view);

            bottomSheetDialog.show();

            bottomSheetDialog.setOnCancelListener(dialog -> simpleExoPlayer.setPlayWhenReady(true));

            send.setOnClickListener(vv -> {
                if (!edtcomment.getText().toString().isEmpty()) {
                    try {
                        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(send.getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    holder.commentCountTextView.setText(Integer.parseInt(holder.commentCountTextView.getText().toString()) + 1 + "");
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
                                            Map map = new HashMap();
                                            map.put("commentsCount",
                                                    Integer.parseInt(data.child("commentsCount").getValue(String.class)) + 1 + "");
                                            map.put("comments",
                                                    data.child("comments").getValue(String.class) + "#!comments!#" +
                                                            edtcomment.getText().toString());
                                            map.put("commentsImg", data.child("commentsImg").getValue(String.class) + "#!cmntImg!#" +
                                                    "https://firebasestorage.googleapis.com/v0/b/tiktokclone-0.appspot.com/o/ProfilePicture%2" +
                                                    sp.getString("id", null) + "?alt=media");
                                            map.put("commentsUname", data.child("commentsUname").getValue(String.class) + "#!uname!#" +
                                                    sp.getString("uname", null));
                                            FirebaseDatabase.getInstance()
                                                    .getReference()
                                                    .child("PVA")
                                                    .child("Videos")
                                                    .child(data.getKey())
                                                    .updateChildren(map);
                                            edtcomment.setText("");

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            });
//           }
//           else {
//
//           }





        });

        //download video
        holder.imgDownload.setOnClickListener(v->{
            AlertDialog.Builder builder=new AlertDialog.Builder(context);
            builder.setMessage("Do You Want To Download This Video?");
            builder.setNegativeButton("No",(dialog, which) -> {
                dialog.cancel();
            });
            builder.setPositiveButton("Yes",(dialog, which) -> {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    Uri uri = Uri.parse(homeModels.get(position).getLink());
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setTitle("Kalakar");

                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "kalakar"+homeModels.get(position).getId()+".mp4");

                    DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    manager.enqueue(request);
                    Toast.makeText(context,"Downloading Started",Toast.LENGTH_SHORT).show();
                }else {
                    permissionInterface.RequestPermi();
                }
            });
            builder.create().show();
        });

        holder.description.setText(homeModels.get(position).getDescription());

    }


    @Override
    public int getItemCount() {
        return homeModels.size();
    }




    static class VideosViewHolder extends RecyclerView.ViewHolder {


        TextView likeCountTextView, commentCountTextView, shareCountTextView,uname,tags,description;

        PlayerView videoView;
        ProgressBar videoProgressBar,pgBarLoadFollow;
        ImageView like,share,comment, profileImage, redCross, greenTick,imgPause,imgDownload;
        SimpleExoPlayer simpleExoPlayer;
        public VideosViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.video_view);
            likeCountTextView = itemView.findViewById(R.id.like_count_tv);
            commentCountTextView = itemView.findViewById(R.id.comment_count_tv);
            shareCountTextView = itemView.findViewById(R.id.share_count_tv);
            videoProgressBar = itemView.findViewById(R.id.video_progress_bar);
            uname=itemView.findViewById(R.id.uname);
            tags=itemView.findViewById(R.id.tags);
            like=itemView.findViewById(R.id.like);
            share=itemView.findViewById(R.id.share);
            comment=itemView.findViewById(R.id.comment);
            profileImage = itemView.findViewById(R.id.profile_img);
            pgBarLoadFollow = itemView.findViewById(R.id.pgBarLoadFollow);

            redCross =itemView.findViewById(R.id.red_cross);
            greenTick = itemView.findViewById(R.id.green_tick);

            imgPause=itemView.findViewById(R.id.imgPause);
            imgDownload=itemView.findViewById(R.id.imgDownload);
            description=itemView.findViewById(R.id.description);

            ImageView musicDiscImageView;
            musicDiscImageView = itemView.findViewById(R.id.iv_music_disk);
            RotateAnimation rotateAnimation = new RotateAnimation(
                    0,360,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
            );
            rotateAnimation.setInterpolator(new LinearInterpolator());
            rotateAnimation.setDuration(4000);
            rotateAnimation.setRepeatCount(Animation.INFINITE);
            musicDiscImageView.setAnimation(rotateAnimation);


//            itemView.setOnTouchListener(new OnSwipeTouchListener(itemView.getContext()) {
//                @Override
//                public void onSwipeLeft() {
////                    Log.i("testritik", "LeftSwipe");
//                    Intent intent = new Intent(itemView.getContext(), UserProfileActivity.class);
//                    intent.putExtra("id", homeModels.get(position).getUid());
//                    context.startActivity(intent);
//
//                }
//            });


        }

        @SuppressLint("SetTextI18n")
        void setMeta(HomeModel videoItem, Context context) {

            likeCountTextView.setText(videoItem.getLikes());
            uname.setText("@"+videoItem.getUname());
            tags.setText(videoItem.getTags());
            shareCountTextView.setText(videoItem.getShares()+"");
            commentCountTextView.setText(videoItem.getComments()+"");

            SharedPreferences sp=context.getSharedPreferences("pref",0);
            String[] s =videoItem.getLiked().split("#!split!#");
            ArrayList<String> list= new ArrayList<>(Arrays.asList(s));
            if (list.contains(sp.getString("id",null))){
                like.setColorFilter(Color.parseColor("#C20303") , android.graphics.PorterDuff.Mode.SRC_IN);
            }


//            if (currentUserFollowingsList.contains(videoItem.getUid())) {
//                redCross.setVisibility(View.INVISIBLE);
//                greenTick.setVisibility(View.VISIBLE);
//            }
//            else {
//                redCross.setVisibility(View.VISIBLE);
//                greenTick.setVisibility(View.INVISIBLE);
//            }


        }

        void LikeVideo(String id, String vidId, String uId){
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("PVA")
                    .child("Videos")
                    .orderByChild("id")
                    .equalTo(vidId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        @SuppressWarnings("unchecked")
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                    Map map=new HashMap<>();

                                    map.put("likes",Integer.parseInt(dataSnapshot.child("likes").getValue(String.class))+1+"");
                                    map.put("liked",dataSnapshot.child("liked").getValue(String.class)+"#!split!#"+ id);

                                    FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("PVA")
                                            .child("Videos")
                                            .child(dataSnapshot.getKey())
                                            .updateChildren(map);
                                    FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("PVA")
                                            .child("Users")
                                            .orderByChild("id")
                                            .equalTo(uId)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot data:snapshot.getChildren()){
                                                        Map map=new HashMap<>();

                                                        map.put("likes",Integer.parseInt(data.child("likes").getValue(String.class))+1+"");


                                                        String inboxItem = id + "&" +
                                                                "like" + "&" +
                                                                new Date().getTime();
                                                        map.put("inbox", inboxItem + "#!split!#" + data.child("inbox").getValue(String.class));


                                                        FirebaseDatabase.getInstance()
                                                                .getReference()
                                                                .child("PVA")
                                                                .child("Users")
                                                                .child(data.getKey())
                                                                .updateChildren(map);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

        void unlikeVideo(String id, String vidId, String uId){
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("PVA")
                    .child("Videos")
                    .orderByChild("id")
                    .equalTo(vidId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        @SuppressWarnings("unchecked")
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                    Map map=new HashMap<>();

                                    map.put("likes",Integer.parseInt(dataSnapshot.child("likes").getValue(String.class))-1+"");
                                    map.put("liked", dataSnapshot.child("liked").getValue(String.class).replace("#!split!#"+ id, ""));

                                    FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("PVA")
                                            .child("Videos")
                                            .child(dataSnapshot.getKey())
                                            .updateChildren(map);
                                    FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("PVA")
                                            .child("Users")
                                            .orderByChild("id")
                                            .equalTo(uId)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot data:snapshot.getChildren()){
                                                        Map map=new HashMap<>();

                                                        map.put("likes",Integer.parseInt(data.child("likes").getValue(String.class))-1+"");
                                                        FirebaseDatabase.getInstance()
                                                                .getReference()
                                                                .child("PVA")
                                                                .child("Users")
                                                                .child(data.getKey())
                                                                .updateChildren(map);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }


    }

    @Override
    public void onViewAttachedToWindow(@NonNull VideosViewHolder holder) {
        if (!holder.simpleExoPlayer.isPlaying()) {
            holder.simpleExoPlayer.setPlayWhenReady(true);
            holder.videoView.setPlayer(holder.simpleExoPlayer);
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, "kalakar"));
            MediaSource videoSource;

            if (holder.getPosition() == 0) {
                PumpRequest(homeModels.get(0).getLink(), 0);
            }

            if (homeModels.get(holder.getPosition()).getLink().endsWith(".mp4")) {
                videoSource =
                        new ProgressiveMediaSource.Factory(dataSourceFactory)
                                .createMediaSource(Uri.fromFile(new File(homeModels.get(holder.getPosition()).getLink())));

                if ((holder.getPosition() + 5) < homeModels.size()) {
                    PumpRequest(homeModels.get(holder.getPosition() + 5).getLink(), holder.getPosition() + 5);
                }

            }
            else {
                videoSource =
                        new ProgressiveMediaSource.Factory(dataSourceFactory)
                                .createMediaSource(Uri.parse(homeModels.get(holder.getPosition()).getLink()));

                for (int i = 1; i <= 5; i++) {
                    if ((holder.getPosition() + i) >= homeModels.size()) {
                        break;
                    }
                    PumpRequest(homeModels.get(holder.getPosition() + i).getLink(), holder.getPosition() + i);
                }

            }


//            PumpRequest(homeModels.get(holder.getPosition() + 1).getLink(), holder.getPosition() + 1);



//            Log.i("ritik", "Got the link");

            holder.simpleExoPlayer.prepare(videoSource);

//            Log.i("ritik", "prepared");

            holder.videoView.setUseController(false);
            simpleExoPlayer = holder.simpleExoPlayer;
            mediaSource1 = videoSource;

            simpleExoPlayer.addListener(new Player.EventListener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playbackState == ExoPlayer.STATE_ENDED) {
                        simpleExoPlayer.seekTo(0);
                        simpleExoPlayer.setPlayWhenReady(true);
                    }
                    if (playWhenReady) {
                        holder.imgPause.setVisibility(View.GONE);
                        holder.videoProgressBar.setVisibility(View.GONE);
                    }
                    if (playbackState == SimpleExoPlayer.STATE_BUFFERING) {
                        holder.videoProgressBar.setVisibility(View.VISIBLE);
                    }


                }

            });
        }
        holder.setMeta(homeModels.get(holder.getLayoutPosition()),context);
        super.onViewAttachedToWindow(holder);
    }
    @Override
    public void onViewDetachedFromWindow(@NonNull VideosViewHolder holder) {
        if (holder.simpleExoPlayer==null){

            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));

            LoadControl loadControl = new DefaultLoadControl.Builder()
                    .setAllocator(new DefaultAllocator(true, 16))
                    .setBufferDurationsMs(
                            2000,
                            5000,
                            2000,
                            2000
                    )
                    .setTargetBufferBytes(-1)
                    .setPrioritizeTimeOverSizeThresholds(true)
                    .createDefaultLoadControl();

            holder.simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
                    context,
                    new DefaultRenderersFactory(context),
                    trackSelector,
                    loadControl,
                    null,
                    bandwidthMeter
            );

//            holder.simpleExoPlayer=new SimpleExoPlayer.Builder(context).build();
        }else {
            holder.simpleExoPlayer.stop(false);
            super.onViewDetachedFromWindow(holder);
        }
    }

    public static void StopExo(){
        if (simpleExoPlayer!=null){
            simpleExoPlayer.setPlayWhenReady(false);
        }
    }

    public static void ResumeExo(){
        if (simpleExoPlayer!=null){
            simpleExoPlayer.setPlayWhenReady(true);
        }
    }

    public static void ReleaseExo(){
        if (simpleExoPlayer!=null){
            simpleExoPlayer.release();
        }
    }

    public void getCurrentUserFollowings(View redCross,View greenTick,View ProgressBar,int position) {
        String id = sp.getString("id", null);

        FirebaseDatabase.getInstance()
                .getReference()
                .child("PVA")
                .child("Users")
                .orderByChild("id")
                .equalTo(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        currentUserFollowingsList.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                String[] s = dataSnapshot.child("followings").getValue(String.class).split("#!split!#");

                                currentUserFollowingsList.addAll(Arrays.asList(s));

                                if (currentUserFollowingsList.contains(homeModels.get(position).getUid())) {
                                    ProgressBar.setVisibility(View.GONE);
                                    redCross.setVisibility(View.INVISIBLE);
                                    greenTick.setVisibility(View.VISIBLE);
                                }
                                else {
                                    ProgressBar.setVisibility(View.GONE);
                                    redCross.setVisibility(View.VISIBLE);
                                    greenTick.setVisibility(View.INVISIBLE);
                                }


                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public boolean isLogin() {

        if (sp.getString("id", null) == null) {
            return false;
        }
        return true;
    }

    private void PumpRequest(String videoUrl, int position) {
//        Log.i("ritik", String.valueOf(!videoUrl.endsWith(".mp4")));
//        Log.i("ritik", String.valueOf(Pump.getDownloadInfoById(videoUrl) == null));
//        Log.i("ritik", String.valueOf(!videoUrl.endsWith(".mp4") && (Pump.getDownloadInfoById(videoUrl) == null)));
//        Log.i("ritik", Pump.getDownloadInfoById(videoUrl).getFilePath());
        if (!videoUrl.endsWith(".mp4")) {
            Pump.newRequest(videoUrl).listener(new DownloadListener() {
                @Override
                public void onSuccess() {
//                    Log.i("ritik", getDownloadInfo().getFilePath());
//                    Log.i("ritik", Pump.getDownloadInfoById(videoUrl).getFilePath());
//                    if (Pump.getDownloadInfoById(videoUrl + "1") == null) {
//                        Log.i("ritik", "is null");
//                    }
//                    else {
//                        Log.i("ritik", "is not null");
//                    }
                    if(!homeModels.isEmpty()){
                        homeModels.get(position).setLink(getDownloadInfo().getFilePath());
                    }
                    super.onSuccess();
                }
            }).submit();
        }
        else if (Pump.getDownloadInfoById(videoUrl) != null) {
            homeModels.get(position).setLink(Pump.getDownloadInfoById(videoUrl).getFilePath());
        }
    }

}
