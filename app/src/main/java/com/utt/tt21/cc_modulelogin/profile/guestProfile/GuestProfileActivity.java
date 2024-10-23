package com.utt.tt21.cc_modulelogin.profile.guestProfile;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utt.tt21.cc_modulelogin.R;
import com.utt.tt21.cc_modulelogin.profile.threads.SectionsPagerAdapter;

import java.io.IOException;

public class GuestProfileActivity extends AppCompatActivity {
    private ImageView imgAvatar;
    private TextView tvName, tvEmail, accountInfo, tvFollowers;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SectionsPagerAdapter sectionsPagerAdapter;

    private ActivityResultLauncher<Intent> mActivityResultLauncher;
    private String userId; // Biến để lưu UID của người dùng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_profile);

        userId = getIntent().getStringExtra("uid"); // Nhận UID từ Intent

        if (userId == null) {
            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
            finish(); // Hoặc bạn có thể điều hướng trở về activity trước đó
            return;
        }

        initUi();
        initActivityResultLauncher();

        // Khởi tạo adapter
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        // Kết nối TabLayout với ViewPager
        tabLayout.setupWithViewPager(viewPager);

        // Khởi tạo nút theo dõi
        Button btnFollow = findViewById(R.id.btn_follow);
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Thực hiện hành động khi nhấn nút theo dõi
            }
        });

        // Khởi tạo nút nhắc đến
        Button btnTagUser = findViewById(R.id.btn_tag_user);
        btnTagUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Thực hiện hành động khi nhấn nút nhắc đến
            }
        });

        // Sự kiện gọi dữ liệu user
        showUserInformation(userId); // Truyền UID vào hàm này
    }

    private void initUi() {
        imgAvatar = findViewById(R.id.img_avatar);
        tvName = findViewById(R.id.tv_name);
        tvEmail = findViewById(R.id.tv_email);
        accountInfo = findViewById(R.id.account_info);
        tvFollowers = findViewById(R.id.followers);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
    }

    private void initActivityResultLauncher() {
        mActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent intent = result.getData();
                            if (intent != null && intent.getBooleanExtra("isUpdated", false)) {
                                // Gọi lại showUserInformation để cập nhật giao diện ngay lập tức
                                showUserInformation(userId);
                            }
                            Uri uri = intent.getData();
                            if (uri != null) {
                                try {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                    imgAvatar.setImageBitmap(bitmap); // Hiển thị hình ảnh lên ImageView
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                });
    }

    private void showUserInformation(String userId) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Lấy thông tin từ Realtime Database
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String nameProfile = dataSnapshot.child("nameProfile").getValue(String.class);
                    String emailProfile = dataSnapshot.child("emailProfile").getValue(String.class);
                    String desProfile = dataSnapshot.child("desProfile").getValue(String.class);
                    Long followersLong = dataSnapshot.child("followers").getValue(Long.class);
                    String followers = String.valueOf(followersLong);

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

                    if (followers != null) {
                        tvFollowers.setText(followers + " người theo dõi");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });

        // Nếu bạn vẫn muốn lấy ảnh đại diện từ FirebaseUser
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            tvEmail.setText(email);
            Uri photoUrl = user.getPhotoUrl();
            Glide.with(this).load(photoUrl).error(R.drawable.ic_default_user).into(imgAvatar);
        }
    }
}
