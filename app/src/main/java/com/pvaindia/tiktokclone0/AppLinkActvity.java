package com.pvaindia.tiktokclone0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

public class AppLinkActvity extends AppCompatActivity {

    SimpleExoPlayer simpleExoPlayer;
    PlayerView playerView;
    SharedPreferences sp;
    SpinKitView spinKitView;
    MediaSource mediaSource;
    static int c=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_link_actvity);
        getSupportActionBar().hide();

        FirebaseApp.initializeApp(this);
        sp=getApplicationContext().getSharedPreferences("pref",0);

        playerView=findViewById(R.id.video_view);
        spinKitView=findViewById(R.id.video_progress_bar);
        simpleExoPlayer=new SimpleExoPlayer.Builder(this).build();

        playerView.setPlayer(simpleExoPlayer);
        playerView.setUseController(false);



        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(pendingDynamicLinkData -> {

                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("PVA")
                            .child("Videos")
                            .orderByChild("link")
                            .equalTo(pendingDynamicLinkData.getLink().toString())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                            DataSource.Factory factory=new DefaultDataSourceFactory(getApplicationContext(),
                                                    Util.getUserAgent(getApplicationContext(),"Kalakar"));

                                             mediaSource=new ProgressiveMediaSource.Factory(factory)
                                                    .createMediaSource(Uri.parse(dataSnapshot.child("link").getValue(String.class)));

                                            simpleExoPlayer.prepare(mediaSource);
                                            simpleExoPlayer.setPlayWhenReady(true);

                                            simpleExoPlayer.addListener(new Player.EventListener() {
                                                @Override
                                                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                                                    if (playWhenReady){
                                                        spinKitView.setVisibility(View.GONE);
                                                    }
                                                    if (playbackState== ExoPlayer.STATE_ENDED){
                                                        simpleExoPlayer.seekTo(0);
                                                    }
                                                }
                                            });
                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getApplicationContext(),"An error occured",Toast.LENGTH_SHORT).show();
                                }
                            });
                });


    }

    @Override
    public void onBackPressed() {
        if (sp.getString("login",null)!=null){
            simpleExoPlayer.release();
            startActivity(new Intent(getApplicationContext(),MainActivity.class)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));

        }else {
            simpleExoPlayer.release();
            startActivity(new Intent(getApplicationContext(),SplashScreen.class));
            finish();
        }
    }



    @Override
    protected void onStop() {
        c=1;
        if (simpleExoPlayer!=null){
            simpleExoPlayer.setPlayWhenReady(false);
        }
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
    protected void onDestroy() {
        simpleExoPlayer.release();
        super.onDestroy();
    }

}