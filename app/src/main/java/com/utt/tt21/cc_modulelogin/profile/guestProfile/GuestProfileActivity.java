package com.utt.tt21.cc_modulelogin.profile.guestProfile;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.utt.tt21.cc_modulelogin.profile.accountmanagement.ImageDisplayActivity;
import com.utt.tt21.cc_modulelogin.profile.threads.SectionsPagerAdapter;

import java.io.IOException;

public class GuestProfileActivity extends AppCompatActivity {
    private ImageView imgAvatar;
    private TextView tvName, tvEmail, accountInfo, tvFollowers;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private SectionsGuestPagerAdapter sectionsGuestPagerAdapter;

    private ActivityResultLauncher<Intent> mActivityResultLauncher;
    private String userId; // Biến để lưu UID của người dùng

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private Button btnFollow;
    private ImageButton btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_profile);

        userId = getIntent().getStringExtra("uid"); // Nhận UID từ Intent

        if (userId == null) {
            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");

        initUi();
        initActivityResultLauncher();

        // Khởi tạo adapter
        sectionsGuestPagerAdapter = new SectionsGuestPagerAdapter(getSupportFragmentManager(),userId);
        viewPager.setAdapter(sectionsGuestPagerAdapter);
        // Kết nối TabLayout với ViewPager
        tabLayout.setupWithViewPager(viewPager);

        // Khởi tạo nút theo dõi và kiểm tra trạng thái Follow
        checkFollowStatus(mAuth.getCurrentUser().getUid(), userId);

        // Xử lý sự kiện khi nhấn nút theo dõi
        btnFollow.setOnClickListener(v -> {
            if (btnFollow.getText().toString().equals("Follow")) {
                followUser(mAuth.getCurrentUser().getUid(), userId);
            } else {
                unfollowUser(mAuth.getCurrentUser().getUid(), userId);
            }
        });

        // Khởi tạo nút nhắn tin
        Button btnTagUser = findViewById(R.id.btn_message);
        btnTagUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Thực hiện hành động khi nhấn nút nhắn tin
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lấy URL của ảnh đại diện từ FirebaseUser
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Uri photoUrl = user.getPhotoUrl();
                    Intent intent = new Intent(GuestProfileActivity.this, ImageDisplayActivity.class);
                    if (photoUrl != null) {
                        intent.putExtra("imageUrl", photoUrl.toString());
                    }
                    startActivity(intent);
                } else {
                    Toast.makeText(GuestProfileActivity.this, "Không tìm thấy ảnh đại diện", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Sự kiện gọi dữ liệu user
        showUserInformation(userId); // Truyền UID vào hàm này
    }

    private void initUi() {
        imgAvatar = findViewById(R.id.img_avatar_guest);
        tvName = findViewById(R.id.tv_name);
        tvEmail = findViewById(R.id.tv_email);
        accountInfo = findViewById(R.id.account_info);
        tvFollowers = findViewById(R.id.followers);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        btnFollow = findViewById(R.id.btn_follow);
        btnClose = findViewById(R.id.btn_close_guest);
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

        // Nếu bạn vẫn muốn lấy ảnh đại diện từ FirebaseUser
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            tvEmail.setText(email);
            Uri photoUrl = user.getPhotoUrl();
            Glide.with(this).load(photoUrl).error(R.drawable.ic_default_user).into(imgAvatar);
        }
    }

    // Kiểm tra trạng thái follow
    private void checkFollowStatus(String currentUserId, String targetUserId) {
        userRef.child(currentUserId).child("followings").child(targetUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    btnFollow.setText("Unfollow");
                } else {
                    btnFollow.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    // Hàm follow người dùng
    private void followUser(String currentUserId, String targetUserId) {
        userRef.child(currentUserId).child("followings").child(targetUserId).setValue(true);
        userRef.child(targetUserId).child("followers").child(currentUserId).setValue(true);
        btnFollow.setText("Unfollow");
    }

    // Hàm unfollow người dùng
    private void unfollowUser(String currentUserId, String targetUserId) {
        userRef.child(currentUserId).child("followings").child(targetUserId).removeValue();
        userRef.child(targetUserId).child("followers").child(currentUserId).removeValue();
        btnFollow.setText("Follow");
    }
}
