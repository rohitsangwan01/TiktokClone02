package com.pvaindia.tiktokclone0;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pvaindia.tiktokclone0.model.SearchModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    ImageView back;
    TextView heading;
    RecyclerView recyclerView;
   List<SearchModel>searchModels=new ArrayList<>();
   LinearLayout progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().hide();

        back=findViewById(R.id.back);
        heading=findViewById(R.id.heading);
        recyclerView=findViewById(R.id.recyclerview);
        progress=findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
        recyclerView.setHasFixedSize(true);

        heading.setText(getIntent().getStringExtra("query"));
        back.setOnClickListener(v->{
            onBackPressed();
        });
        if (CheckConn()){
            Log.d("started","true");
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("PVA")
                    .child("Videos")
                    .orderByChild("tags")
                    .startAt("#"+getIntent().getStringExtra("query"))
                    .endAt(getIntent().getStringExtra("query"))
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        @SuppressWarnings("unchecked")
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            Map<String,String>map=(Map<String, String>) snapshot.getValue();

                           searchModels.add(0,new SearchModel(map.get("link")+"",map.get("views")+""));

                            recyclerView.setAdapter(new SearchAdapter(getApplicationContext(),searchModels));
                            progress.setVisibility(View.GONE);
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
                            Log.e("firebase error",error.getMessage());
                        }
                    });
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("PVA")
                    .child("Videos")
                    .orderByChild("tags")
                    .startAt("#"+getIntent().getStringExtra("query"))
                    .endAt(getIntent().getStringExtra("query"))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()){
                                progress.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(),"No Results Found!",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }else {
            progress.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(),"No Connection",Toast.LENGTH_SHORT).show();
        }


    }
    private boolean CheckConn() {
        try {
            ConnectivityManager connec =
                    (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo=connec.getActiveNetworkInfo();
            if (networkInfo!=null&&networkInfo.isConnected()){
                return true;
            }
        } catch (Exception e) {
           Log.e("error",e.toString());
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}