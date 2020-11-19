package com.pvaindia.tiktokclone0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        SharedPreferences sp=getApplicationContext().getSharedPreferences("pref",0);
        img=findViewById(R.id.img);

        Handler handler=new Handler();
        handler.postDelayed(() -> {

            if (sp.getString("is_first_time_done", null) == null) {
                startActivity(new Intent(getApplicationContext(), OnboardingActivity.class));
                finish();
            }
            else {

//                if (sp.getString("login", null) == null) {
//                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
//                    finish();
//
//                } else {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    );
//                }
            }
        },1000);
    }

}