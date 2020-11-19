package com.pvaindia.tiktokclone0;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    public OnBoardingAdapter onboardingAdapter;
    private ViewPager viewPager;
    TextView skipBtn, nextBtn;
    LinearLayout signInAndSkipBtn;

    ChooseCategoryFragment chooseCategoryFragment;

    ImageView indicator1, indicator2, indicator3;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);



        skipBtn = findViewById(R.id.skip_btn);
        signInAndSkipBtn = findViewById(R.id.sign_in_and_skip_btn);
        nextBtn = findViewById(R.id.next_btn);

        getSupportActionBar().hide();

        sp = getApplicationContext().getSharedPreferences("pref", 0);

        List<Fragment> fragments = new ArrayList<>();

        Onboarding1Fragment ob1Fragment = new Onboarding1Fragment();
        fragments.add(ob1Fragment);

        Onboarding2Fragment ob2Fragment = new Onboarding2Fragment();
        fragments.add(ob2Fragment);

        chooseCategoryFragment = new ChooseCategoryFragment();
        fragments.add(chooseCategoryFragment);

        viewPager = findViewById(R.id.ob_view_pager);


        onboardingAdapter = new OnBoardingAdapter(getSupportFragmentManager(), fragments);

        viewPager.setAdapter(onboardingAdapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.i("test", String.valueOf(position));
            }

            @Override
            public void onPageSelected(int position) {
                Log.i("test", String.valueOf(position));

                switch (position) {
                    case 0:
                        signInAndSkipBtn.setVisibility(View.GONE);
                      //  nextBtn.setVisibility(View.VISIBLE);
                       // skipBtn.setVisibility(View.VISIBLE);
                       // indicator1.setImageResource(R.drawable.ob_indicator_active);
                        break;

                    case 1:
                        signInAndSkipBtn.setVisibility(View.GONE);
                      //  nextBtn.setVisibility(View.VISIBLE);
                       // skipBtn.setVisibility(View.VISIBLE);
                      //  indicator2.setImageResource(R.drawable.ob_indicator_active);
                        break;

                    case 2:
                        signInAndSkipBtn.setVisibility(View.VISIBLE);
                       // nextBtn.setVisibility(View.GONE);
                      //  skipBtn.setVisibility(View.GONE);
                       // indicator3.setImageResource(R.drawable.ob_indicator_active);
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                Log.i("test", String.valueOf(state));
            }
        });

    }



    public void onClickNext(View view) {
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
    }

    public void onClickSkip(View view) {
        viewPager.setCurrentItem(2, true);
    }

    public void onClickSignIn(View view) {
        setFirstTimeDone();
        saveSelectedCategories();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickFinishLast(View view) {
        setFirstTimeDone();
        saveSelectedCategories();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setFirstTimeDone() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("is_first_time_done", "true");
        editor.apply();
        editor.commit();
    }

    private void saveSelectedCategories(){
        SharedPreferences.Editor editor = sp.edit();
        StringBuilder selectedCategories = new StringBuilder();
        for (String category: chooseCategoryFragment.selectedCategories) {
            selectedCategories.append(category).append(",");
        }
        editor.putString("categories", selectedCategories.toString());
        editor.apply();
        editor.commit();
    }
}