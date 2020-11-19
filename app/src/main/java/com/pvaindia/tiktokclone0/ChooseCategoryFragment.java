package com.pvaindia.tiktokclone0;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class ChooseCategoryFragment extends Fragment {


    public ArrayList<String> selectedCategories = new ArrayList<>();

    private TextView entertainmentTV;
    private TextView dramaTV;
    private TextView romanticTV;
    private TextView emotionalTV;
    private TextView indiaTV;
    private TextView talentTV;
    private TextView randomTV;
    private TextView allTV;

    public ChooseCategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_choose_category, container, false);

        entertainmentTV = v.findViewById(R.id.entertainment_tv);
        dramaTV = v.findViewById(R.id.drama_tv);
        romanticTV = v.findViewById(R.id.romantic_tv);
        emotionalTV = v.findViewById(R.id.emotional_tv);
        indiaTV = v.findViewById(R.id.india_tv);
        talentTV = v.findViewById(R.id.talent_tv);
        randomTV = v.findViewById(R.id.random_tv);
        allTV = v.findViewById(R.id.all_tv);

        entertainmentTV.setOnClickListener(v1 -> {
            if (!selectedCategories.contains(entertainmentTV.getText().toString())) {
                entertainmentTV.setBackgroundTintList(getContext().getColorStateList(R.color.darkGreen));
                selectedCategories.add(entertainmentTV.getText().toString());
            }
            else {
                entertainmentTV.setBackgroundTintList(getContext().getColorStateList(R.color.darkBrown));
                selectedCategories.remove(entertainmentTV.getText().toString());
            }
        });

        dramaTV.setOnClickListener(v1 -> {
            if (!selectedCategories.contains(dramaTV.getText().toString())) {
                dramaTV.setBackgroundTintList(getContext().getColorStateList(R.color.darkGreen));
                selectedCategories.add(dramaTV.getText().toString());
            }
            else {
                dramaTV.setBackgroundTintList(getContext().getColorStateList(R.color.darkBrown));
                selectedCategories.remove(dramaTV.getText().toString());
            }
        });

        romanticTV.setOnClickListener(v1 -> {
            if (!selectedCategories.contains(romanticTV.getText().toString())) {
                romanticTV.setBackgroundTintList(getContext().getColorStateList(R.color.darkGreen));
                selectedCategories.add(romanticTV.getText().toString());
            }
            else {
                romanticTV.setBackgroundTintList(getContext().getColorStateList(R.color.darkBrown));
                selectedCategories.remove(romanticTV.getText().toString());
            }
        });

        emotionalTV.setOnClickListener(v1 -> {
            if (!selectedCategories.contains(emotionalTV.getText().toString())) {
                emotionalTV.setBackgroundTintList(getContext().getColorStateList(R.color.darkGreen));
                selectedCategories.add(emotionalTV.getText().toString());
            }
            else {
                emotionalTV.setBackgroundTintList(getContext().getColorStateList(R.color.darkBrown));
                selectedCategories.remove(emotionalTV.getText().toString());
            }
        });

        indiaTV.setOnClickListener(v1 -> {
            if (!selectedCategories.contains(indiaTV.getText().toString())) {
                indiaTV.setBackgroundTintList(getContext().getColorStateList(R.color.darkGreen));
                selectedCategories.add(indiaTV.getText().toString());
            }
            else {
                indiaTV.setBackgroundTintList(getContext().getColorStateList(R.color.darkBrown));
                selectedCategories.remove(indiaTV.getText().toString());
            }
        });

        talentTV.setOnClickListener(v1 -> {
            if (!selectedCategories.contains(talentTV.getText().toString())) {
                talentTV.setBackgroundTintList(getContext().getColorStateList(R.color.darkGreen));
                selectedCategories.add(talentTV.getText().toString());
            }
            else {
                talentTV.setBackgroundTintList(getContext().getColorStateList(R.color.darkBrown));
                selectedCategories.remove(talentTV.getText().toString());
            }
        });

        randomTV.setOnClickListener(v1 -> {
            if (!selectedCategories.contains(randomTV.getText().toString())) {
                randomTV.setBackgroundTintList(getContext().getColorStateList(R.color.darkGreen));
                selectedCategories.add(randomTV.getText().toString());
            }
            else {
                randomTV.setBackgroundTintList(getContext().getColorStateList(R.color.darkBrown));
                selectedCategories.remove(randomTV.getText().toString());
            }
        });

        allTV.setOnClickListener(v1 -> {
            if (!selectedCategories.contains(allTV.getText().toString())) {
                allTV.setBackgroundTintList(getContext().getColorStateList(R.color.darkGreen));
                selectedCategories.add(allTV.getText().toString());
            }
            else {
                allTV.setBackgroundTintList(getContext().getColorStateList(R.color.darkBrown));
                selectedCategories.remove(allTV.getText().toString());
            }
        });

        return v;

    }
}