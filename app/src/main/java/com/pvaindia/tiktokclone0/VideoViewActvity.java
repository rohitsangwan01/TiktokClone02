package com.pvaindia.tiktokclone0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.pvaindia.tiktokclone0.model.HomeModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VideoViewActvity extends AppCompatActivity {

    SimpleExoPlayer simpleExoPlayer;
    PlayerView playerView;
    SpinKitView spinKitView;
    ImageView musicDiscImageView;
    ImageView like,share,comment, profileImage, redCross, greenTick,imgPause,imgDownload;
    TextView likeCountTextView, commentCountTextView, shareCountTextView,uname,tags,description;
    HomeModel currentVideo;
    SharedPreferences sp;
    ArrayList<String> currentUserFollowingsList;
    ImageView backBtn;

    static int c=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view_actvity);

        getWindow().setStatusBarColor(getResources().getColor(R.color.darkestGrey));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.darkestGrey));

        getSupportActionBar().hide();

        sp = getSharedPreferences("pref", 0);

        currentUserFollowingsList = new ArrayList<>();
        if (sp.getString("id", null) != null) {
            getCurrentUserFollowings();
        }

        playerView=findViewById(R.id.video_view);
        spinKitView=findViewById(R.id.video_progress_bar);

        likeCountTextView = findViewById(R.id.like_count_tv);
        commentCountTextView = findViewById(R.id.comment_count_tv);
        shareCountTextView = findViewById(R.id.share_count_tv);
        uname=findViewById(R.id.uname);
        tags=findViewById(R.id.tags);
        like=findViewById(R.id.like);
        share=findViewById(R.id.share);
        comment=findViewById(R.id.comment);
        profileImage = findViewById(R.id.profile_img);
        redCross =findViewById(R.id.red_cross);
        greenTick = findViewById(R.id.green_tick);
        imgPause=findViewById(R.id.imgPause);
        imgDownload=findViewById(R.id.imgDownload);
        backBtn = findViewById(R.id.back_btn);
        description=findViewById(R.id.description);

        backBtn.setOnClickListener(v -> {
            finish();
        });

        redCross.setVisibility(View.GONE);
        greenTick.setVisibility(View.GONE);

        musicDiscImageView = findViewById(R.id.iv_music_disk);
        RotateAnimation rotateAnimation = new RotateAnimation(
                0,360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(4000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        musicDiscImageView.setAnimation(rotateAnimation);

        simpleExoPlayer=new SimpleExoPlayer.Builder(getApplicationContext()).build();
        simpleExoPlayer.setPlayWhenReady(true);

        DataSource.Factory factory=new DefaultDataSourceFactory(getApplicationContext(),
                Util.getUserAgent(getApplicationContext(),"Kalakar"));

        MediaSource mediaSource=new ProgressiveMediaSource.Factory(factory)
                .createMediaSource(Uri.parse(getIntent().getStringExtra("link")));

        playerView.setPlayer(simpleExoPlayer);
        playerView.setUseController(false);

        FirebaseDatabase.getInstance()
                .getReference()
                .child("PVA")
                .child("Videos")
                .orderByChild("link")
                .equalTo(getIntent().getStringExtra("link"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            spinKitView.setVisibility(View.GONE);
                            simpleExoPlayer.prepare(mediaSource);
                            for (DataSnapshot data:snapshot.getChildren()){
                                Map<String, String> map = (Map<String, String>) data.getValue();
                                currentVideo = new HomeModel(
                                        "" + map.get("uname"),
                                        "" + map.get("tags"),
                                        "" + map.get("views"),
                                        "" + map.get("likes"),
                                        "" + map.get("liked"),
                                        "" + map.get("shares"),
                                        "" + map.get("link"),
                                        "" + map.get("uid"),
                                        "" + map.get("id"),
                                        "" + map.get("commentsCount"),
                                        map.get("description")
                                );

                                setMeta(currentVideo, getApplicationContext());

                                loadProfilePic();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        simpleExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                if (playWhenReady){
                    spinKitView.setVisibility(View.GONE);
                }
                if (playbackState==SimpleExoPlayer.STATE_BUFFERING){
                    spinKitView.setVisibility(View.VISIBLE);
                }
                if (playbackState==SimpleExoPlayer.STATE_ENDED){
                    simpleExoPlayer.seekTo(0);
                    simpleExoPlayer.setPlayWhenReady(true);
                }
            }
        });

        like.setOnClickListener(v->{
            if (isLogin()) {
                String[] s = currentVideo.getLiked().split("#!split!#");
                ArrayList<String> list = new ArrayList<>(Arrays.asList(s));
                //Like
                if (!list.contains(sp.getString("id", null))) {
                    like.setColorFilter(Color.parseColor("#C20303"), android.graphics.PorterDuff.Mode.SRC_IN);
//                holder.like.setEnabled(false);
                    list.add(sp.getString("id", null));
                    StringBuilder temp = new StringBuilder();
                    for (String id : list) {
                        temp.append("#!split!#").append(id);
                    }
                    currentVideo.setLiked(temp.toString());
                    currentVideo.setLikes(String.valueOf(Integer.parseInt(currentVideo.getLikes()) + 1));
                    LikeVideo(sp.getString("id", null), currentVideo.getId(), currentVideo.getUid());
                    likeCountTextView.setText(Integer.parseInt(likeCountTextView.getText().toString()) + 1 + "");
                }
                //Unlike
                else {
                    like.setColorFilter(Color.parseColor("#FFFFFF"), android.graphics.PorterDuff.Mode.SRC_IN);
//                holder.like.setEnabled(true);
                    list.remove(sp.getString("id", null));
                    StringBuilder temp = new StringBuilder();
                    for (String id : list) {
                        temp.append("#!split!#").append(id);
                    }
                    currentVideo.setLiked(temp.toString());
                    currentVideo.setLikes(String.valueOf(Integer.parseInt(currentVideo.getLikes()) - 1));
                    unlikeVideo(sp.getString("id", null), currentVideo.getId(), currentVideo.getUid());
                    likeCountTextView.setText(Integer.parseInt(likeCountTextView.getText().toString()) - 1 + "");
                }
            }
            else {
                Intent intent = new Intent(VideoViewActvity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        profileImage.setOnClickListener(v -> {

            finish();
        });

        share.setOnClickListener(v->{
            Task<ShortDynamicLink> dynamicLink= FirebaseDynamicLinks.getInstance()
                    .createDynamicLink()
                    .setLink(Uri.parse(currentVideo.getLink()))
                    .setDomainUriPrefix("https://kalakar.page.link")
                    .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                    .buildShortDynamicLink().addOnCompleteListener(task -> {
                        Intent intent=new Intent( new Intent(Intent.ACTION_SEND));
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT,currentVideo.getUname()+"\n\n_"+task.getResult().getShortLink()+"_");
                        startActivity(Intent.createChooser(intent,"Share")
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    });

            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("PVA")
                    .child("Videos")
                    .orderByChild("id")
                    .equalTo(currentVideo.getId())
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
                                            shareCountTextView.setText(Integer.parseInt(shareCountTextView.getText().toString())+1+""));

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        });

        comment.setOnClickListener(v->{

//           if (isLogin()) {

            simpleExoPlayer.setPlayWhenReady(false);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_comment, null);


            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("PVA")
                    .child("Videos")
                    .orderByChild("id")
                    .equalTo(currentVideo.getId())
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
                                recyclerView.setLayoutManager(new LinearLayoutManager(VideoViewActvity.this));
                                recyclerView.setAdapter(new CommentAdapter(list1, list2, list3, VideoViewActvity.this));
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
                    Intent intent = new Intent(VideoViewActvity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                });
            }


            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(VideoViewActvity.this, R.style.BottomSheetDialog);
            bottomSheetDialog.setContentView(view);

            bottomSheetDialog.show();

            bottomSheetDialog.setOnCancelListener(dialog -> simpleExoPlayer.setPlayWhenReady(true));

            send.setOnClickListener(vv -> {
                if (!edtcomment.getText().toString().isEmpty()) {
                    try {
                        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                        InputMethodManager imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(send.getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    commentCountTextView.setText(Integer.parseInt(commentCountTextView.getText().toString()) + 1 + "");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("PVA")
                            .child("Videos")
                            .orderByChild("id")
                            .equalTo(currentVideo.getId())
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

        imgDownload.setOnClickListener(v->{
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setMessage("Do You Want To Download This Video?");
            builder.setNegativeButton("No",(dialog, which) -> {
                dialog.cancel();
            });
            builder.setPositiveButton("Yes",(dialog, which) -> {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    Uri uri = Uri.parse(currentVideo.getLink());
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setTitle("Kalakar");

                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "kalakar"+currentVideo.getId()+".mp4");

                    DownloadManager manager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
                    manager.enqueue(request);
                    Toast.makeText(VideoViewActvity.this,"Downloading Started",Toast.LENGTH_SHORT).show();
                }else {
//                    permissionInterface.RequestPermi();
                    Toast.makeText(this, "Please give storage permission in settings", Toast.LENGTH_SHORT).show();
                }
            });
            builder.create().show();
        });

    }

    @Override
    protected void onDestroy() {
        simpleExoPlayer.release();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        c=1;
        simpleExoPlayer.setPlayWhenReady(false);
        super.onStop();
    }

    @Override
    protected void onResume() {
        if (c==1){
           simpleExoPlayer.setPlayWhenReady(true);
           c=0;
        }

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        simpleExoPlayer.release();
        super.onBackPressed();
    }

    @SuppressLint("SetTextI18n")
    void setMeta(HomeModel videoItem, Context context) {

        likeCountTextView.setText(videoItem.getLikes());
        uname.setText("@" + videoItem.getUname());
        tags.setText(videoItem.getTags());
        shareCountTextView.setText(videoItem.getShares() + "");
        commentCountTextView.setText(videoItem.getComments() + "");
        description.setText(videoItem.getDescription());

        SharedPreferences sp = context.getSharedPreferences("pref", 0);
        String[] s = videoItem.getLiked().split("#!split!#");
        ArrayList<String> list = new ArrayList<>(Arrays.asList(s));
        if (list.contains(sp.getString("id", null))) {
            like.setColorFilter(Color.parseColor("#C20303"), android.graphics.PorterDuff.Mode.SRC_IN);
        }
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

    public void getCurrentUserFollowings() {
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
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                String[] s = dataSnapshot.child("followings").getValue(String.class).split("#!split!#");

                                currentUserFollowingsList.addAll(Arrays.asList(s));

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

    public void loadProfilePic() {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("PVA")
                .child("Users")
                .orderByChild("id")
                .equalTo(currentVideo.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data:snapshot.getChildren()){
                            Glide.with(getApplicationContext())
                                    .load(data.child("image").getValue())
                                    .placeholder(R.drawable.icon_user_profile)
                                    .error(R.drawable.icon_user_profile)
                                    .into(profileImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}