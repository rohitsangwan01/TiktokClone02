package com.pvaindia.tiktokclone0;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pvaindia.tiktokclone0.ui.CameraActivity;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
//
    private static String HOME = "home";
    private static String DISCOVER = "discover";
    private static String INBOX = "inbox";
    private static String ME = "me";

    private boolean isBackPressedOnce = false;

    private String currentItem;

    ImageView homeIcon, meIcon, discoverIcon, inboxIcon;
    TextView homeTextView, discoverTextView, inboxTextView, meTextView;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.darkestGrey));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.darkestGrey));

        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        fragmentManager = getSupportFragmentManager();

        sp = getApplicationContext().getSharedPreferences("pref", 0);

        initNavBar();

    }

    public void onClickCreateBtn(View view) {
        if (isLogin()) {
            Intent intent = new Intent(this, CameraActivity.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

    }

    public void onClickNavItem(View view) {
        int selectedItemId = view.getId();
        String selectedItemTag = view.getTag().toString();

//        Log.i("item_comment", selectedItemTag);

        if (!selectedItemTag.equals(currentItem)) {

            switch (selectedItemId) {

                case R.id.item_home:
                    VideosAdapter.ReleaseExo();
                    fragmentManager.beginTransaction().replace(R.id.main_layout, new HomeFragment()).commit();
                    setDefaultIcon();
                    homeIcon.setImageResource(R.drawable.icon_home_filled);
//                    homeTextView.setTextColor(getResources().getColor(R.color.colorAccent));
                    currentItem = HOME;
                    break;

                case R.id.item_discover:
                    VideosAdapter.ReleaseExo();
                    fragmentManager.beginTransaction().replace(R.id.main_layout, new SearchFragment()).commit();
                    setDefaultIcon();
                    discoverIcon.setImageResource(R.drawable.icon_search_filled);
//                    discoverTextView.setTextColor(getResources().getColor(R.color.colorAccent));
                    currentItem = DISCOVER;
                    break;

                case R.id.item_inbox:
                    if (isLogin()) {
                        VideosAdapter.ReleaseExo();
                        fragmentManager.beginTransaction().replace(R.id.main_layout, new InboxFragment()).commit();
                        setDefaultIcon();
                        inboxIcon.setImageResource(R.drawable.icon_message_filled);
//                        inboxTextView.setTextColor(getResources().getColor(R.color.colorAccent));
                        currentItem = INBOX;
                    }
                    else {
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }

                    break;

                case R.id.item_me:
                    if (isLogin()) {
                        VideosAdapter.ReleaseExo();
                        fragmentManager.beginTransaction().replace(R.id.main_layout, new MeFragment()).commit();
                        setDefaultIcon();
                        meIcon.setImageResource(R.drawable.icon_person_filled);
//                        meTextView.setTextColor(getResources().getColor(R.color.colorAccent));
                        currentItem = ME;
                    }
                    else {
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }

                    break;


            }
        }
    }

    private void setDefaultIcon() {
        homeIcon.setImageResource(R.drawable.icon_home_outline);
        discoverIcon.setImageResource(R.drawable.icon_search);
        inboxIcon.setImageResource(R.drawable.icon_message_outline);
        meIcon.setImageResource(R.drawable.icon_person_outline);

        homeTextView.setTextColor(getResources().getColor(R.color.white));
        discoverTextView.setTextColor(getResources().getColor(R.color.white));
        inboxTextView.setTextColor(getResources().getColor(R.color.white));
        meTextView.setTextColor(getResources().getColor(R.color.white));

    }

    private void initNavBar() {
        homeIcon = findViewById(R.id.icon_home);
        meIcon = findViewById(R.id.icon_me);
        discoverIcon = findViewById(R.id.icon_discover);
        inboxIcon = findViewById(R.id.icon_inbox);

        homeTextView = findViewById(R.id.home_tv);
        discoverTextView = findViewById(R.id.discover_tv);
        inboxTextView = findViewById(R.id.inbox_tv);
        meTextView = findViewById(R.id.me_tv);

        fragmentManager.beginTransaction().add(R.id.main_layout, new HomeFragment()).commit();
        homeIcon.setImageResource(R.drawable.icon_home_filled);
//        homeTextView.setTextColor(getResources().getColor(R.color.colorAccent));
        currentItem = HOME;
    }

    @Override
    protected void onStop() {
        VideosAdapter.StopExo();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        VideosAdapter.ReleaseExo();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        VideosAdapter.ResumeExo();
        super.onResume();
    }

    private boolean isLogin() {
        if (sp.getString("login", null) == null) {
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if (isBackPressedOnce) {
            finish();
        }
        else {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            isBackPressedOnce = true;
        }
    }
}
