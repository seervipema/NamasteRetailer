package com.pemaseervi.myretailer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;


public class SelectLangaugeFragment extends Fragment {


    public SelectLangaugeFragment() {

    }
    private TextView tvEnglish,tvHindi;
    private RadioButton selectEnglish,selectHindi;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_select_langauge, container, false);
        tvEnglish= view.findViewById(R.id.tv_english);
        tvHindi= view.findViewById(R.id.tv_hindi);
        selectEnglish= view.findViewById(R.id.english_radio_selector);
        selectHindi= view.findViewById(R.id.hindi_radio_selector);
        selectEnglish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
        selectHindi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
        return view;
    }








}
