package com.utt.tt21.cc_modulelogin.accountmanagement;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.utt.tt21.cc_modulelogin.R;
import com.utt.tt21.cc_modulelogin.authentication.SignInActivity;

import java.io.IOException;

public class Profile extends Fragment implements GalleryOpener {

    public static final int MY_REQUEST_CODE = 10;
    private ImageView imgAvartar;
    private TextView tvName, tvEmail, accountInfo;

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

        // Bắt sự kiện click vào avatar để mở gallery
        imgAvartar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(); // Gọi phương thức mở thư viện ảnh
            }
        });
    }

    private void showUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();

        if (name == null) {
            tvName.setVisibility(View.GONE);
        } else {
            tvName.setVisibility(View.VISIBLE);
            tvName.setText(name);
        }

        tvEmail.setText(email);
        Glide.with(this).load(photoUrl).error(R.drawable.ic_avatar_default).into(imgAvartar);

        // Kiểm tra và thiết lập độ hiển thị cho account_info
        if (name == null || name.trim().isEmpty()) {
            accountInfo.setVisibility(View.GONE); // Ẩn nếu không có tên
        } else {
            accountInfo.setVisibility(View.VISIBLE); // Hiện nếu có tên
            accountInfo.setText("Chào mình là " + name + "."); // Thiết lập văn bản cho account_info
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tải lại dữ liệu người dùng khi Profile trở lại
        showUserInformation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(getContext(), "Permission denied. Please allow gallery access in settings.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }
}
