package com.utt.tt21.cc_modulelogin.search;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;


import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.utt.tt21.cc_modulelogin.R;


public class Search extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabAdapter tabAdapter;
    private ImageButton btnBack;

    private Button btnGet, btnPush;
    private TextView tvShow;


    public Search() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_friend, container, false);
        // Khởi tạo views
        mapping(view);
        // Thiết lập adapter cho ViewPager
        tabAdapter = new TabAdapter(getChildFragmentManager());
        viewPager.setAdapter(tabAdapter);
        // Kết nối TabLayout với ViewPager
        tabLayout.setupWithViewPager(viewPager);
        // Xử lý sự kiện nút quay lại
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }
    public void mapping(View view){
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        btnBack = view.findViewById(R.id.btnBack);


    }


    //push data
    private void onClickPushData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("list_user");

        reference.setValue("");
    }

    private void init(View view) {
        btnGet = view.findViewById(R.id.getString);
        btnPush = view.findViewById(R.id.pushString);
        tvShow = view.findViewById(R.id.tvShow);


    }
}