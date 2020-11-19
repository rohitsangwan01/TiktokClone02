package com.pvaindia.tiktokclone0;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pvaindia.tiktokclone0.model.HomeModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    TextView followers,likes,followings,uname, followBtn;
    RecyclerView recyclerView;
    SharedPreferences sp;
    SpinKitView spinKitView;
    List<HomeModel> homeModels=new ArrayList<>();
    ImageView img, backBtn;
    LinearLayout load,mainLayout;
    NestedScrollView nestedScrollView;
    String queryId;
    ProgressBar PgBArLoadBtn;

    static ArrayList<String> currentUserFollowingsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getSupportActionBar().hide();

        recyclerView=findViewById(R.id.recyclerview);
        followers=findViewById(R.id.txt_followers);
        likes=findViewById(R.id.txt_likes);
        followings=findViewById(R.id.txt_following);
        spinKitView=findViewById(R.id.spinkit);
        uname=findViewById(R.id.uname);
        img=findViewById(R.id.img);
        load=findViewById(R.id.load);
        followBtn = findViewById(R.id.follow_btn);
        backBtn = findViewById(R.id.back_btn);
        mainLayout=findViewById(R.id.main_layout);
        nestedScrollView=findViewById(R.id.nested);
        PgBArLoadBtn = findViewById(R.id.PgBArLoadBtn);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));

        queryId = getIntent().getStringExtra("id");

        sp = getSharedPreferences("pref",0);

        if (isLogin()) {
            getCurrentUserFollowings();
        }

        LoadProfile();

        nestedScrollView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()){
            @Override
            public void onSwipeRight() {
                finish();
                super.onSwipeRight();
            }
        });

        backBtn.setOnClickListener(v -> {
            finish();
        });

        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isLogin()) {

                    if (followBtn.getText().equals("Follow")) {

                        //Follow
                        //Update on Video Author's side
                        FirebaseDatabase.getInstance()
                                .getReference()
                                .child("PVA")
                                .child("Users")
                                .orderByChild("id")
                                .equalTo(queryId).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                                        .getValue(String.class) + "#!split!#" + queryId);

                                                FirebaseDatabase.getInstance()
                                                        .getReference()
                                                        .child("PVA")
                                                        .child("Users")
                                                        .child(dataSnapshot.getKey())
                                                        .updateChildren(map);
                                            }
                                            getCurrentUserFollowings();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                        followBtn.setText("Following");
                        followBtn.setTextColor(getColor(R.color.black));
                        followBtn.setBackground(getResources().getDrawable(R.drawable.follow_bg));

                    } else {
                        //Unfollow

                        //Update on Video Author's side
                        FirebaseDatabase.getInstance()
                                .getReference()
                                .child("PVA")
                                .child("Users")
                                .orderByChild("id")
                                .equalTo(queryId).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                                        .getValue(String.class).replace("#!split!#" + queryId, ""));

                                                FirebaseDatabase.getInstance()
                                                        .getReference()
                                                        .child("PVA")
                                                        .child("Users")
                                                        .child(dataSnapshot.getKey())
                                                        .updateChildren(map);
                                            }
                                            getCurrentUserFollowings();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                        followBtn.setText("Follow");
                        followBtn.setTextColor(getColor(R.color.white));
                        followBtn.setBackgroundColor(Color.parseColor("#E9445A"));
                    }
                }
                else {
                    Toast.makeText(UserProfileActivity.this, "You must login first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FirebaseDatabase.getInstance()
                .getReference()
                .child("PVA")
                .child("Users")
                .orderByChild("id")
                .equalTo(queryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot data:snapshot.getChildren()){
                                followings.setText(data.child("followingsCount").getValue(String.class));
                                followers.setText(data.child("followersCount").getValue(String.class));
                                likes.setText(data.child("likes").getValue(String.class));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void LoadProfile() {

        Log.i("testritik", queryId);


        FirebaseDatabase.getInstance()
                .getReference()
                .child("PVA")
                .child("Users")
                .orderByChild("id")
                .equalTo(queryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot data:snapshot.getChildren()){
                                followings.setText(data.child("followingsCount").getValue(String.class));
                                followers.setText(data.child("followersCount").getValue(String.class));
                                likes.setText(data.child("likes").getValue(String.class));
                                uname.setText(data.child("uname").getValue(String.class));

                                if (getApplicationContext()!=null){
                                    Glide.with(getApplicationContext())
                                            .load(data.child("image").getValue(String.class))
                                            .placeholder(R.drawable.icon_user_profile)
                                            .error(R.drawable.icon_user_profile)
                                            .apply(RequestOptions.circleCropTransform())
                                            .into(img);
                                }

                                load.setVisibility(View.GONE);

                                LoadMyVid();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void LoadMyVid() {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("PVA")
                .child("Videos")
                .orderByChild("uid")
                .equalTo(getIntent().getStringExtra("id"))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Map<String,String> map=(Map<String, String>) snapshot.getValue();


                        homeModels.add(new HomeModel(
                                ""+map.get("uname"),
                                ""+map.get("tags"),
                                ""+map.get("views"),
                                ""+map.get("likes"),
                                ""+map.get("liked"),
                                ""+map.get("shares"),
                                ""+map.get("link"),
                                ""+map.get("uid"),
                                ""+map.get("id"),
                                ""+map.get("commentsCount"),
                                ""+map.get("description")
                        ));



                        recyclerView.setAdapter(new ProfileAdapter(UserProfileActivity.this,homeModels));
                        spinKitView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance()
                .getReference()
                .child("PVA")
                .child("Videos")
                .orderByChild("uid")
                .equalTo(sp.getString("id",null))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
//                            Toast.makeText(UserProfileActivity.this,"User don't have any videos yet!",Toast.LENGTH_SHORT).show();
                            spinKitView.setVisibility(View.GONE);
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
                        currentUserFollowingsList.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                String[] s = dataSnapshot.child("followings").getValue(String.class).split("#!split!#");

                                currentUserFollowingsList.addAll(Arrays.asList(s));

                                if (currentUserFollowingsList.contains(queryId)) {
                                    PgBArLoadBtn.setVisibility(View.INVISIBLE);
                                    followBtn.setVisibility(View.VISIBLE);
                                    followBtn.setText("Following");
                                    followBtn.setTextColor(getColor(R.color.black));
                                    followBtn.setBackground(getResources().getDrawable(R.drawable.follow_bg));
                                }
                                else {
                                    PgBArLoadBtn.setVisibility(View.INVISIBLE);
                                    followBtn.setVisibility(View.VISIBLE);
                                    followBtn.setText("Follow");
                                    followBtn.setTextColor(getColor(R.color.white));
                                    followBtn.setBackgroundColor(Color.parseColor("#E9445A"));
                                }




                            }
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

    public boolean isLogin() {
        if (sp.getString("id", null) == null) {
            return false;
        }

        return true;
    }

}