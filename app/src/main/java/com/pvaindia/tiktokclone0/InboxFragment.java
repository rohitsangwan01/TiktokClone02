package com.pvaindia.tiktokclone0;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class InboxFragment extends Fragment {

    SharedPreferences sp;
//    ProgressBar progressBar;
    TextView noNotificationsTextView;
    SwipeRefreshLayout swipeRefreshLayout;
    List<InboxItem> inboxItems;
    ListView notificationListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

//        progressBar = view.findViewById(R.id.progressBar);
//        progressBar.setVisibility(View.VISIBLE);

        noNotificationsTextView = view.findViewById(R.id.no_notifications_tv);
        noNotificationsTextView.setVisibility(View.GONE);

        notificationListView = view.findViewById(R.id.notification_list_view);

        inboxItems = new ArrayList<>();

        sp = getContext().getSharedPreferences("pref", 0);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadNotifications();
            }
        });

        loadNotifications();
        swipeRefreshLayout.setRefreshing(true);










//        inboxItems.add(new InboxItem("https://firebasestorage.googleapis.com/v0/b/tiktokclone-0.appspot.com/o/ProfilePicture%2F2e42aaa9d43e4150960b5864afcd0a4b?alt=media",
//                "@pvaindia started following you.",
//                "13m",
//                "http://www.pvaindia.com"));
//
//        inboxItems.add(new InboxItem("https://firebasestorage.googleapis.com/v0/b/tiktokclone-0.appspot.com/o/ProfilePicture%2F2e42aaa9d43e4150960b5864afcd0a4b?alt=media",
//                "@pvaindia started following you.",
//                "13m",
//                "http://www.pvaindia.com"));
//
//        inboxItems.add(new InboxItem("https://firebasestorage.googleapis.com/v0/b/tiktokclone-0.appspot.com/o/ProfilePicture%2F2e42aaa9d43e4150960b5864afcd0a4b?alt=media",
//                "@pvaindia started following you.",
//                "13m",
//                "http://www.pvaindia.com"));



//        InboxAdapter inboxAdapter = new InboxAdapter(getContext(), inboxItems);
//        ListView notificationListView = view.findViewById(R.id.notification_list_view);
//
//        notificationListView.setAdapter(inboxAdapter);
//
//        notificationListView.setOnItemClickListener((parent, view1, position, id) -> {
//            String link = inboxItems.get(position).getAssociatedLink();
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setData(Uri.parse(link));
//            startActivity(intent);
//        });

        return view;

    }

    void loadNotifications() {
        inboxItems.clear();
        FirebaseDatabase.getInstance()
                .getReference()
                .child("PVA")
                .child("Users")
                .orderByChild("id")
                .equalTo(sp.getString("id", null))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            ArrayList<String> inboxItemString = new ArrayList<>();
                            inboxItemString.addAll(Arrays.asList(dataSnapshot.child("inbox").getValue(String.class).split("#!split!#")));
                            for (String item: inboxItemString) {
                                //each item in firebase is stored in format -> id&type&time
                                String[] actualItem = item.split("&");

                                FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child("PVA")
                                        .child("Users")
                                        .orderByChild("id")
                                        .equalTo(actualItem[0])
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot data: snapshot.getChildren()) {
                                                    String typeString;
                                                    switch(actualItem[1]) {
                                                        case "like":
                                                            typeString = "liked your post.";
                                                            break;
                                                        case "follow":
                                                            typeString = "started following you.";
                                                            break;
                                                        case "upload":
                                                            typeString = "uploaded a new video.";
                                                            break;
                                                        default:
                                                            typeString = "";
                                                    }
                                                    long temp = (new Date().getTime() - Long.parseLong(actualItem[2])) / (1000 * 60);
                                                    String timeDiff;
                                                    if (temp < 60) {
                                                        timeDiff = temp + "m";
                                                    }
                                                    else if (temp < 1440) {
                                                        timeDiff = ((long) temp / 60) + "h";
                                                    }
                                                    else {
                                                        timeDiff = ((long) temp / (60 * 24)) + "d";
                                                    }
                                                    inboxItems.add(new InboxItem(
                                                                    data.child("image").getValue(String.class),
                                                                    "@" + data.child("uname").getValue(String.class) + " " + typeString,
                                                                    timeDiff,
                                                                    "http://www.pvaindia.com"
                                                            )
                                                    );
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


        new Handler().postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
//            progressBar.setVisibility(View.GONE);
            Context context = getContext();
            InboxAdapter inboxAdapter = null;
            if (context != null) {
                inboxAdapter = new InboxAdapter(getContext(), inboxItems);
            }

            notificationListView.setAdapter(inboxAdapter);

            //TODO
//            notificationListView.setOnItemClickListener((parent, view1, position, id) -> {
//                String link = inboxItems.get(position).getAssociatedLink();
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse(link));
//                startActivity(intent);
//            });

            if (inboxItems.size() == 0) {
                noNotificationsTextView.setVisibility(View.VISIBLE);
            }
            else {
                noNotificationsTextView.setVisibility(View.GONE);
            }

        }, 1500);

    }
}