package com.utt.tt21.cc_modulelogin.search;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utt.tt21.cc_modulelogin.R;



public class Search extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TabAdapter tabAdapter;
    private FollowingFragment followingFragment;
    private FollowersFragment followersFragment;
    private GoiiFragment goiiFragment;
    private TextView  toolbar_t;
    private FirebaseAuth mAuth;
    private ImageButton btnTimKiem;
    public Search() {
        // yeu cau ham tao
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_friend, container, false);
        mapping(view);
        // hien ten
        toolbar_t = view.findViewById(R.id.toolbar_t);
        mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId).child("nameProfile");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                toolbar_t.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        followingFragment = new FollowingFragment();
        followersFragment = new FollowersFragment();
        goiiFragment = new GoiiFragment();
        // Thiết lập adapter cho ViewPager
        tabAdapter = new TabAdapter(getActivity(), followingFragment, followersFragment, goiiFragment);
        viewPager.setAdapter(tabAdapter);
        // Kết nối TabLayout với ViewPager
        viewPager.setOffscreenPageLimit(3);
        // Xử lý sự kiện nút quay lại
        btnTimKiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),TimKiem.class));
            }
        });
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Đang theo dõi");
                            break;
                        case 1:
                            tab.setText("Người theo dõi");
                            break;
                        case 2:
                            tab.setText("Bạn bè");
                            break;
                    }
                }).attach();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Xử lý khi một tab được chọn

                switch (tab.getPosition()){
                    case 0:
                        followingFragment.refreshData();
                        break;
                    case 1:
                        followersFragment.refreshData();
                        break;
                    case 2:
                        goiiFragment.loadUsers();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Xử lý khi một tab không còn được chọn
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Xử lý khi một tab đã được chọn lại
                Toast.makeText(getActivity(), "Reselected: " + tab.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
    public void mapping(View view){
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        btnTimKiem = view.findViewById(R.id.btnTimKiem);
    }

}