package com.utt.tt21.cc_modulelogin.accountmanagement;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.utt.tt21.cc_modulelogin.R;
import com.utt.tt21.cc_modulelogin.authentication.SignInActivity;


public class Profile extends Fragment {

    private ImageView imgAvartar;
    private TextView tvName, tvEmail;

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initUi(view);
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
        // Sự kiện gọi dữ liệu user
        showUserInformation();
        return view;
    }

    private void initUi(View view) {
        imgAvartar = view.findViewById(R.id.img_avatar);
        tvName = view.findViewById(R.id.tv_name);
        tvEmail = view.findViewById(R.id.tv_email);
    }

    private void showUserInformation(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }else {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            if(name == null){
                // nếu chưa có name thì ẩn textview đi
                tvName.setVisibility(View.GONE);
            }else{
                // ngược lại thì hiện ra
                tvName.setVisibility(View.VISIBLE);
                tvName.setText(name);
            }

            tvEmail.setText(email);
            Glide.with(this).load(photoUrl).error(R.drawable.ic_avatar_default).into(imgAvartar);

        }
    }
}