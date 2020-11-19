package com.pvaindia.tiktokclone0;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SocialSignupActvity extends AppCompatActivity {

    TextView next;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_signup);
        getSupportActionBar().hide();

        next=findViewById(R.id.next);

        next.setOnClickListener(view -> Toast.makeText(getApplicationContext(),"ss",Toast.LENGTH_SHORT).show());
    }
}
