package com.utt.tt21.cc_modulelogin.profile.accountmanagement;

import static com.utt.tt21.cc_modulelogin.profile.Profile.MY_REQUEST_CODE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utt.tt21.cc_modulelogin.R;

import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity implements GalleryOpener { // Implementing GalleryOpener interface
    private ImageView imgAvatar;
    private EditText edtFullName, edtEmail, edtDesc;
    private Button btnUpdateProfile;
    private ImageButton btnClose;
    private Uri selectedImageUri; // Biến để lưu trữ URI của ảnh đã chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initUi();
        setUserInformation();
        initListener();
    }

    private void initUi() {
        imgAvatar = findViewById(R.id.img_avatar_edit);
        edtFullName = findViewById(R.id.edt_full_name_edit);
        edtEmail = findViewById(R.id.edt_email_edit);
        btnUpdateProfile = findViewById(R.id.btn_update_profile);
        btnClose = findViewById(R.id.btn_close);
        edtDesc = findViewById(R.id.edt_desc_edit);
    }

    private void setUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        String userId = user.getUid(); // Lấy UID của người dùng
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fullName = snapshot.child("nameProfile").getValue(String.class);
                    String desc = snapshot.child("desProfile").getValue(String.class);
                    edtEmail.setText(user.getEmail());
                    edtFullName.setText(fullName);
                    edtDesc.setText(desc);

                    // Kiểm tra kiểu dữ liệu của imgProfile trước khi chuyển đổi thành String và tải ảnh qua Glide
                    Object photoUrlObj = snapshot.child("imgProfile").getValue();
                    if (photoUrlObj instanceof String) {
                        String photoUrl = (String) photoUrlObj;
                        Glide.with(EditProfileActivity.this)
                                .load(photoUrl)
                                .error(R.drawable.ic_default_user)
                                .into(imgAvatar);
                    }
                }else{
                    Toast.makeText(EditProfileActivity.this, "Không tìm thấy dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditProfileActivity.this, "Không thể lấy dữ liệu: ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initListener() {
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRequestPermission();
            }
        });

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInformation();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void onClickRequestPermission() {
        // Từ Android 6 trở lên cần yêu cầu quyền
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openGallery();
            return;
        }

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permissions, MY_REQUEST_CODE);
        }
    }

    // Xử lý kết quả yêu cầu quyền truy cập
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission denied. Please allow gallery access in settings.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), MY_REQUEST_CODE);
    }

    // Nhận kết quả từ gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData(); // Lưu URI của ảnh đã chọn
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                imgAvatar.setImageBitmap(bitmap); // Hiển thị hình ảnh lên ImageView
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateUserInformation() {
        String fullName = edtFullName.getText().toString();
        String desc = edtDesc.getText().toString();
        // email không cho cập nhật
        // String email = edtEmail.getText().toString();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Cập nhật DisplayName và PhotoURL trên Firebase Authentication
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(fullName)
                    .setPhotoUri(selectedImageUri) // Sử dụng URI đã lưu
                    .build();

            user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Cập nhật tên và mô tả trên Realtime Database
                    String userId = user.getUid();
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

                    databaseRef.child("nameProfile").setValue(fullName);
                    databaseRef.child("desProfile").setValue(desc).addOnCompleteListener(dbTask -> {
                        if (dbTask.isSuccessful()) {
                            Intent intent = new Intent();
                            intent.putExtra("isUpdated", true);  // Đánh dấu cập nhật thành công
                            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Cập nhật mô tả không thành công", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "Cập nhật thông tin không thành công", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Người dùng không tồn tại", Toast.LENGTH_SHORT).show();
        }
    }

}
