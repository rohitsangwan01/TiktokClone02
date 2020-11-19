package com.pvaindia.tiktokclone0;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Intent;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pvaindia.tiktokclone0.model.HomeModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class HomeFragment extends Fragment {


    List<HomeModel>homeModels=new ArrayList<>();
    VideosAdapter videosAdapter;
    TextView followingTextView, loginTextView, trendingTextView;
    SharedPreferences sp;
    ViewPager2 videoViewPager;
    LinearLayout layoutNoFollowing;
    LinearLayout layoutGettingYourVideos;
    ShimmerFrameLayout shimmerEffect;
    ShimmerFrameLayout shimmerEffect2;

    int a=0;
    int b = 0;



    static ArrayList<String> currentUserFollowingsList = new ArrayList<>();

    public HomeFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.activity_home, container, false);

        layoutGettingYourVideos = v.findViewById(R.id.getting_your_videos_layout);
        layoutNoFollowing = v.findViewById(R.id.no_following_layout);

        layoutNoFollowing.setVisibility(View.GONE);

        videoViewPager = v.findViewById(R.id.video_view_pager);

        shimmerEffect = v.findViewById(R.id.shimmerEffect);
        shimmerEffect.startShimmer();
        shimmerEffect2 = v.findViewById(R.id.shimmerEffect2);
        shimmerEffect2.startShimmer();

        FirebaseApp.initializeApp(getContext());

        permissionInterface permissionInterface= () -> {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},11);
        };
        videosAdapter=new VideosAdapter(getContext(),homeModels,permissionInterface);

        sp = getContext().getSharedPreferences("pref", 0);

        followingTextView = v.findViewById(R.id.tv_following);
        loginTextView = v.findViewById(R.id.tv_Login);
        trendingTextView = v.findViewById(R.id.tv_trending);

        if (isLogin()) {
            loginTextView.setVisibility(View.GONE);
            followingTextView.setVisibility(View.VISIBLE);
        }
        else {
            loginTextView.setVisibility(View.VISIBLE);
            followingTextView.setVisibility(View.GONE);
        }

        loginTextView.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        followingTextView.setOnClickListener(view -> {
            trendingTextView.setAlpha(0.5f);
            followingTextView.setAlpha(1f);
            showFollowing();
        });

        trendingTextView.setOnClickListener(view -> {
            trendingTextView.setAlpha(1f);
            followingTextView.setAlpha(0.5f);
            showTrending();
        });

        if (isLogin()) {
            getCurrentUserFollowings();
        }


        showTrending();
        layoutGettingYourVideos.setVisibility(View.VISIBLE);

        return v;

    }

    private boolean CheckConn() {
        try {
            ConnectivityManager connec =
                    (ConnectivityManager) getContext().getSystemService(getContext().CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo=connec.getActiveNetworkInfo();
            if (networkInfo!=null&&networkInfo.isConnected()){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isLogin() {
        if (sp.getString("id", null) == null) {
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==11){
            if (grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getContext(),"Permission granted",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getContext(),"Permission denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showTrending() {

        if (isLogin()) {
            getCurrentUserFollowings();
        }

      //  layoutGettingYourVideos.setVisibility(View.VISIBLE);
        layoutNoFollowing.setVisibility(View.GONE);

        homeModels.clear();

        if (CheckConn()) {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("PVA")
                    .child("Videos")
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        @SuppressWarnings("unchecked")
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            Map<String, String> map = (Map<String, String>) snapshot.getValue();

                            homeModels.add(0, new HomeModel(
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

                            ));
                            layoutGettingYourVideos.setVisibility(View.GONE);
                            videoViewPager.setAdapter(videosAdapter);
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
        }else {
            Toast.makeText(getContext(),"No Connection",Toast.LENGTH_LONG).show();
        }
    }

    public void showFollowing() {


        getCurrentUserFollowings();

        //layoutGettingYourVideos.setVisibility(View.VISIBLE);
        layoutNoFollowing.setVisibility(View.GONE);

//        new Handler().postDelayed(() -> {
//            if (currentUserFollowingsList.isEmpty()) {
//                Toast.makeText(getContext(), "You don't follow anyone" , Toast.LENGTH_SHORT).show();
//            }
//        }, 5000);

        if (CheckConn()) {

            homeModels.clear();

            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("PVA")
                    .child("Videos")
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        @SuppressWarnings("unchecked")
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            Map<String, String> map = (Map<String, String>) snapshot.getValue();

                            if (currentUserFollowingsList.contains(map.get("uid"))) {

                                homeModels.add(0, new HomeModel(
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
                                ));
                            }

                            layoutGettingYourVideos.setVisibility(View.GONE);
                            videoViewPager.setAdapter(videosAdapter);

                            new Handler().postDelayed(() -> {
                                    if (homeModels.isEmpty()) {
                                        layoutNoFollowing.setVisibility(View.VISIBLE);
                                    }
                            }, 1000);
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
        } else {
            Toast.makeText(getContext(), "No Connection", Toast.LENGTH_LONG).show();
        }
    }

    public void getCurrentUserFollowings() {
        String id = sp.getString("id", null);

        currentUserFollowingsList.clear();

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

}
