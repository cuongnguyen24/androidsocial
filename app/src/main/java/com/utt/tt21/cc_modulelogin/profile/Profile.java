package com.utt.tt21.cc_modulelogin.profile;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.utt.tt21.cc_modulelogin.R;
import com.utt.tt21.cc_modulelogin.authentication.SignInActivity;
import com.utt.tt21.cc_modulelogin.profile.accountmanagement.EditProfileActivity;
import com.utt.tt21.cc_modulelogin.profile.threads.SectionsPagerAdapter;

import java.io.IOException;

public class Profile extends Fragment {

    public static final int MY_REQUEST_CODE = 10;
    private ImageView imgAvartar;
    private TextView tvName, tvEmail, accountInfo, tvFollowers;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SectionsPagerAdapter sectionsPagerAdapter;


    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent == null) {
                            return;
                        }
                        if (intent != null && intent.getBooleanExtra("isUpdated", false)) {
                            // Gọi lại showUserInformation để cập nhật giao diện ngay lập tức
                            showUserInformation();
                        }
                        Uri uri = intent.getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            imgAvartar.setImageBitmap(bitmap); // Hiển thị hình ảnh lên ImageView
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });

    private ContentResolver getContentResolver() {
        return getActivity().getContentResolver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initUi(view);

        // Khởi tạo adapter
        sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        // Kết nối TabLayout với ViewPager
        tabLayout.setupWithViewPager(viewPager);

        // Khởi tạo nút đăng xuất
        Button btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut(); // Đăng xuất khỏi Firebase
                Intent intent = new Intent(getActivity(), SignInActivity.class); // Chuyển về màn hình đăng nhập
                startActivity(intent);
                getActivity().finishAffinity(); // Đóng tất cả các activity trước đó
            }
        });

        // Khởi tạo nút chỉnh sửa trang cá nhân
        Button btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Chuyển đến trang chỉnh sửa profile
                Intent intent = new Intent(getActivity(), EditProfileActivity.class); // Khởi tạo intent để chuyển đến EditProfileActivity
                startActivity(intent);
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
        accountInfo = view.findViewById(R.id.account_info);
        tvFollowers = view.findViewById(R.id.followers);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);

    }

    private void showUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        String userId = user.getUid(); // Lấy UID của người dùng
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Lấy thông tin từ Realtime Database
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Lấy nameProfile từ database
                    String nameProfile = dataSnapshot.child("nameProfile").getValue(String.class);
                    String emailProfile = dataSnapshot.child("emailProfile").getValue(String.class);
                    String desProfile = dataSnapshot.child("desProfile").getValue(String.class);
//                    Long followersLong = dataSnapshot.child("followers").getValue(Long.class);
//                    String followers = String.valueOf(followersLong);

                    // Thiết lập nameProfile vào tvName
                    if (nameProfile != null) {
                        tvName.setVisibility(View.VISIBLE);
                        tvName.setText(nameProfile);
                    } else {
                        tvName.setVisibility(View.GONE);
                    }

                    // Thiết lập emailProfile vào tvEmail
                    if (emailProfile != null) {
                        tvEmail.setText(emailProfile);
                    }

                    // Thiết lập độ hiển thị cho account_info
                    if (desProfile == null || desProfile.trim().isEmpty()) {
                        accountInfo.setVisibility(View.GONE); // Ẩn nếu không có tên
                    } else {
                        accountInfo.setVisibility(View.VISIBLE); // Hiện nếu có tên
                        accountInfo.setText(desProfile); // Thiết lập văn bản cho account_info
                    }

                    //Lấy và đếm số lượng người theo dõi từ nhánh "followers"
                    if (dataSnapshot.hasChild("followers")) {
                        long followersCount = dataSnapshot.child("followers").getChildrenCount();
                        tvFollowers.setText(followersCount + " người theo dõi");
                    } else {
                        tvFollowers.setText("0 người theo dõi");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });

        // Nếu muốn lấy email từ FirebaseUser
        String email = user.getEmail();
        tvEmail.setText(email);

        // Nếu bạn vẫn muốn lấy ảnh đại diện từ FirebaseUser
        Uri photoUrl = user.getPhotoUrl();
        Glide.with(this).load(photoUrl).error(R.drawable.ic_default_user).into(imgAvartar);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tải lại dữ liệu người dùng khi Profile trở lại
        showUserInformation();
    }
}
