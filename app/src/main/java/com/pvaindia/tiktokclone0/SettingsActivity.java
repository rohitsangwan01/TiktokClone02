package com.pvaindia.tiktokclone0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    ImageView backBtn;
    LinearLayout logout;
    TextView PVALabel, appVersionTextView;

    private FirebaseAuth firebaseAuth;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().hide();

        getWindow().setNavigationBarColor(getResources().getColor(R.color.darkestGrey));

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);


        sp = getSharedPreferences("pref", 0);

        backBtn = findViewById(R.id.back_btn);
        logout = findViewById(R.id.logout);
        PVALabel = findViewById(R.id.pva_label);
        appVersionTextView = findViewById(R.id.app_version_tv);

        String versionName = "";

        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        appVersionTextView.setText("v" + versionName);

        backBtn.setOnClickListener(v -> {
            finish();
        });

        PVALabel.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, WebViewActivity.class);
            intent.putExtra("url", "https://pvaindia.com");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        logout.setOnClickListener(v -> {
            firebaseAuth.signOut();
        });

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            if (firebaseAuth.getCurrentUser() == null) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("login", null);
                editor.putString("id", null);
                editor.putString("uname", null);
                editor.apply();
                editor.commit();

                Toast.makeText(SettingsActivity.this, "Logout success", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                finish();

            }
        }
    };
}