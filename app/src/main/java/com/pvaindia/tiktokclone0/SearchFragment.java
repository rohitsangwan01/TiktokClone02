package com.pvaindia.tiktokclone0;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;


public class SearchFragment extends Fragment implements View.OnClickListener {


    EditText search;
    TextView txt1,txt2,txt3,txt4,txt5,txt6,txt7;
    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_search, container, false);

        search=view.findViewById(R.id.edt_search_box);

        search.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId== EditorInfo.IME_ACTION_SEARCH){
                if (!search.getText().toString().isEmpty()){
                    startActivity(new Intent(getContext(),SearchActivity.class)
                            .putExtra("query",search.getText().toString()));
                    search.setText("");
                }
            }
            return false;
        });

        txt1=view.findViewById(R.id.txt1);
        txt2=view.findViewById(R.id.txt2);
        txt3=view.findViewById(R.id.txt3);
        txt4=view.findViewById(R.id.txt4);
        txt5=view.findViewById(R.id.txt5);
        txt6=view.findViewById(R.id.txt6);
        txt7=view.findViewById(R.id.txt7);

        txt1.setOnClickListener(this);
        txt2.setOnClickListener(this);
        txt3.setOnClickListener(this);
        txt4.setOnClickListener(this);
        txt5.setOnClickListener(this);
        txt6.setOnClickListener(this);
        txt7.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        TextView txt=v.findViewById(v.getId());
        startActivity(new Intent(getContext(),SearchActivity.class)
        .putExtra("query",txt.getText().toString()));
    }
}