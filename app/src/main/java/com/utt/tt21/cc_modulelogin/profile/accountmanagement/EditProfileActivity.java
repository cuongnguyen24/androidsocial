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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.utt.tt21.cc_modulelogin.R;

import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity { // Implementing GalleryOpener interface
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
//                onClickRequestPermission();
                openGallery();
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Tạo đường dẫn lưu trữ ảnh trên Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("users/" + userId + "/" + userId + ".jpg");

            if (selectedImageUri != null) {
                // Tải ảnh lên Firebase Storage
                UploadTask uploadTask = storageRef.putFile(selectedImageUri);

                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    // Sau khi ảnh được tải lên thành công, lấy URL của ảnh
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Cập nhật DisplayName và PhotoURL trên Firebase Authentication
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(fullName)
                                .setPhotoUri(uri) // Sử dụng URI ảnh từ Firebase Storage
                                .build();

                        user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Cập nhật tên và mô tả trên Realtime Database
                                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

                                databaseRef.child("nameProfile").setValue(fullName);
                                databaseRef.child("desProfile").setValue(desc);
                                databaseRef.child("imgProfile").setValue(uri.toString()).addOnCompleteListener(dbTask -> {
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
                    });
                }).addOnFailureListener(e -> {
                    // Xử lý khi tải ảnh lên không thành công
                    Toast.makeText(EditProfileActivity.this, "Tải ảnh lên không thành công", Toast.LENGTH_SHORT).show();
                });
            } else {
                // Nếu không có ảnh được chọn, chỉ cập nhật tên và mô tả
                updateDatabaseWithoutImage(fullName, desc, userId);
            }
        } else {
            Toast.makeText(this, "Người dùng không tồn tại", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDatabaseWithoutImage(String fullName, String desc, String userId) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        databaseRef.child("nameProfile").setValue(fullName);
        databaseRef.child("desProfile").setValue(desc).addOnCompleteListener(dbTask -> {
            if (dbTask.isSuccessful()) {
                Intent intent = new Intent();
                intent.putExtra("isUpdated", true);
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(this, "Cập nhật mô tả không thành công", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
