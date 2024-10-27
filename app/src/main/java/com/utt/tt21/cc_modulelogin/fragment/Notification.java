//package com.utt.tt21.cc_modulelogin.fragment;
//
//import android.content.Intent;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.viewpager.widget.ViewPager;
//
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//
//import com.google.android.material.tabs.TabLayout;
//import com.google.android.material.tabs.TabLayoutMediator;
//import com.utt.tt21.cc_modulelogin.R;
//
//import com.utt.tt21.cc_modulelogin.profile.threads.SectionsPagerAdapter;
//import com.utt.tt21.cc_modulelogin.search.TabAdapter;
//
//import com.utt.tt21.cc_modulelogin.messenger.messenger;
//
//
//
//public class Notification extends Fragment {
//    private TabLayout tabLayout;
//    private ViewPager viewPager;
//    private TabAdapter tabAdapter;
//    private ImageButton btnBack;
//    public Notification() {
//        // Required empty public constructor
//    }
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view= inflater.inflate(R.layout.fragment_friend, container, false);
//        // Khởi tạo views
//        mapping(view);
//        // Thiết lập adapter cho ViewPager
//        tabAdapter = new TabAdapter(getChildFragmentManager());
//        viewPager.setAdapter(tabAdapter);
//        // Kết nối TabLayout với ViewPager
//        tabLayout.setupWithViewPager(viewPager);
//        // Xử lý sự kiện nút quay lại
//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//       return view;
//    }
//    public void mapping(View view){
//        tabLayout = view.findViewById(R.id.tabLayout);
//        viewPager = view.findViewById(R.id.viewPager);
//        btnBack = view.findViewById(R.id.btnBack);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        initId(view);
//
//    }
//
//    private void initId(View view) {
//
//    }
//}