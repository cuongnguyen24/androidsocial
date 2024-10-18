package com.utt.tt21.cc_modulelogin.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.utt.tt21.cc_modulelogin.R;
import com.utt.tt21.cc_modulelogin.authentication.SignInActivity;


public class Profile extends Fragment {

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Khởi tạo nút đăng xuất
        Button btnLogout = view.findViewById(R.id.btn_logout);

        // Sự kiện OnClickListener cho nút đăng xuất
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut(); // Đăng xuất khỏi Firebase
                Intent intent = new Intent(getActivity(), SignInActivity.class); // Chuyển về màn hình đăng nhập
                startActivity(intent);
                getActivity().finishAffinity(); // Đóng tất cả các activity trước đó
            }
        });

        return view;
    }
}