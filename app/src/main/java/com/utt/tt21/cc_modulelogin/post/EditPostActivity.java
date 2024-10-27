package com.utt.tt21.cc_modulelogin.post;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.utt.tt21.cc_modulelogin.R;
import com.utt.tt21.cc_modulelogin.post.ImageUriAdapter;

import java.util.ArrayList;
import java.util.List;

public class EditPostActivity extends AppCompatActivity {
    TextView tvName, tvDes;
    ImageView profileImage;
    ImageButton btnChooseImage;
    Button btnDeleteCache;
    private ArrayList<Uri> imageUris = new ArrayList<>();
    Button btnCancel, btnUpStatus;
    private ImageUriAdapter adapter; // Sử dụng ImageUriAdapter
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_post);

        // Nhận dữ liệu từ Intent
        String uid = getIntent().getStringExtra("uid");
        String username = getIntent().getStringExtra("username");
        String statusId = getIntent().getStringExtra("status_id");
        String content = getIntent().getStringExtra("content");
        String profileImageUrl = getIntent().getStringExtra("profile_image");

        // Lấy userId của người dùng đang đăng nhập
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Ánh xạ các View
        tvName = findViewById(R.id.tvName);
        tvDes = findViewById(R.id.tvDes);
        btnCancel = findViewById(R.id.btnCancel);
        profileImage = findViewById(R.id.profileImage);
        btnChooseImage = findViewById(R.id.chooseImage);
        btnUpStatus = findViewById(R.id.btnUpStatus);
        btnDeleteCache = findViewById(R.id.deleteCacheImage);


        // Thiết lập tên và mô tả
        tvName.setText(username);
        tvDes.setText(content);

        // Thiết lập RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new ImageUriAdapter(this, imageUris); // Khởi tạo adapter mới
        recyclerView.setAdapter(adapter);

        // Tải ảnh đại diện
        Glide.with(this).load(profileImageUrl).into(profileImage);
        loadUserAvatar(userId, profileImage);

        // Lấy danh sách ảnh từ Firebase Storage
        loadImagesFromStorage(userId,statusId);

        // Sự kiện click cho nút hủy
        btnCancel.setOnClickListener(v -> finish()); // Kết thúc Activity hiện tại

        // Mở trình chọn ảnh
        btnChooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnDeleteCache.setOnClickListener(v -> deleteCacheImage());

        // Sự kiện click cho nút cập nhật nội dung
        btnUpStatus.setOnClickListener(v -> {
            String newContent = tvDes.getText().toString();

            // Cập nhật nội dung trước
            updateStatusContent(uid, statusId, newContent);

            // Cập nhật ảnh nếu có
            if (imageUris.isEmpty()) {
                deleteOldImages(statusId);
            } else {
                // Cập nhật ảnh nếu có
                updateImage(uid, statusId);
            }
        });

    }

    private void deleteCacheImage() {
        // Xóa toàn bộ ảnh trong danh sách imageUris
        imageUris.clear();

        // Cập nhật lại adapter để làm mới giao diện
        adapter.notifyDataSetChanged();

        Log.d("EditPostActivity", "All cached images have been deleted.");
    }

    private void deleteOldImages(String statusId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        // Tách statusId để lấy idImgStt
        String[] parts = statusId.split("_");
        String idImgStt = parts[1];

        // Xây dựng đường dẫn đến thư mục ảnh
        String imagesFolderPath = "users/" + userId + "/IdImgStt_" + idImgStt + "/";

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference(imagesFolderPath);

        storageRef.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference fileRef : listResult.getItems()) {
                // Xóa từng tệp
                fileRef.delete()
                        .addOnSuccessListener(aVoid -> Log.d("DeleteImage", "Successfully deleted: " + fileRef.getName()))
                        .addOnFailureListener(exception -> Log.e("DeleteImage", "Failed to delete image", exception));
            }

            // Sau khi xóa tất cả ảnh, tạo lại file dummy
            createEmptyFolder(idImgStt); // Gọi createEmptyFolder với idImgStt
        }).addOnFailureListener(exception -> {
            Log.e("DeleteImage", "Failed to list files", exception);
        });
    }

    private void createEmptyFolder(String idImgStt) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("users").child(userId);

        // Tạo đường dẫn đến thư mục ảnh
        StorageReference imageFolderRef = storageRef.child("IdImgStt_" + idImgStt);

        // Tạo tệp tạm thời để tạo thư mục
        StorageReference dummyFileRef = imageFolderRef.child("dummy.txt");
        byte[] dummyData = new byte[0]; // Dữ liệu trống

        dummyFileRef.putBytes(dummyData)
                .addOnSuccessListener(taskSnapshot -> Log.d("UploadImage", "Created folder: IdImgStt_" + idImgStt))
                .addOnFailureListener(exception -> Log.e("UploadImage", "Failed to create empty folder", exception));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData(); // Nhận URI của ảnh đã chọn

            // Xóa tất cả các ảnh cũ khỏi danh sách
            imageUris.clear(); // Xóa các ảnh cũ
            imageUris.add(imageUri); // Thêm ảnh mới vào danh sách
            adapter.notifyDataSetChanged(); // Cập nhật adapter để hiển thị ảnh mới
        }
    }
    // Phương thức để cập nhật nội dung
    private void updateStatusContent(String uid, String statusId, String newContent) {
        // Khởi tạo Firebase Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("list_status").child(uid).child(statusId);

        // Cập nhật giá trị content
        databaseRef.child("content").setValue(newContent)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseDatabase", "Content updated successfully.");
                    finish(); // Kết thúc Activity sau khi cập nhật thành công
                })
                .addOnFailureListener(exception -> {
                    Log.e("FirebaseDatabase", "Error updating content", exception);
                });
    }

    // Sửa phương thức cập nhật ảnh để chỉ cập nhật ảnh mới
    private void updateImage(String userId, String statusId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String[] parts = statusId.split("_");
        String idImgStt = parts[1];

        // Xây dựng đường dẫn đến thư mục ảnh
        String imagesFolderPath = "users/" + userId + "/IdImgStt_" + idImgStt + "/";
        StorageReference imagesFolderRef = storage.getReference(imagesFolderPath);

        // Xóa tất cả các ảnh cũ trong thư mục
        imagesFolderRef.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference item : listResult.getItems()) {
                item.delete()
                        .addOnSuccessListener(aVoid -> {
                            Log.d("FirebaseStorage", "Old image deleted: " + item.getName());
                        })
                        .addOnFailureListener(exception -> {
                            Log.e("FirebaseStorage", "Error deleting old image", exception);
                        });
            }

            // Tải ảnh mới lên sau khi xóa ảnh cũ thành công
            uploadNewImages(userId, statusId, imageUris);
        }).addOnFailureListener(exception -> {
            Log.e("FirebaseStorage", "Error listing old images", exception);
        });
    }

    private void uploadNewImages(String userId, String statusId, List<Uri> imageUris) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String[] parts = statusId.split("_"); // Tách theo "_"
        String idImgStt = parts[1];
        String storagePath = "users/" + userId + "/IdImgStt_" + idImgStt + "/";

        // Duyệt qua từng Uri trong danh sách imageUris
        for (int index = 0; index < imageUris.size(); index++) {
            Uri imageUri = imageUris.get(index);
            String newImageName = "image_" + (index + 1) + ".jpg"; // Tạo tên cho ảnh
            Log.d("FirebaseStorage", "Storage path: " + storagePath + newImageName);

            // Tạo đường dẫn lưu trữ
            StorageReference storageRef = storage.getReference(storagePath + newImageName);

            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d("FirebaseStorage", "Image " + newImageName + " uploaded successfully.");
                        // Có thể gọi lại hàm để cập nhật danh sách ảnh sau khi tải lên thành công
                        loadImagesFromStorage(userId, statusId);
                    })
                    .addOnFailureListener(exception -> {
                        Log.e("FirebaseStorage", "Error uploading image " + newImageName, exception);
                    });
        }
    }



    // Phương thức để tải ảnh đại diện
    private void loadUserAvatar(String userId, ImageView imageView) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("users/").child(userId).child(userId + ".jpg");

        // Sử dụng Glide để tải ảnh từ Storage và hiển thị vào ImageView
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(imageView.getContext())
                    .load(uri)
                    .into(imageView);
        }).addOnFailureListener(exception -> {
            Log.e("FirebaseStorage", "Error loading image", exception);
        });
    }

    // Phương thức để tải danh sách ảnh từ Firebase Storage
    private void loadImagesFromStorage(String userId, String statusId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Giả sử IdImgStt có dạng "IdImg" và là một phần của statusId
        String[] parts = statusId.split("_"); // Tách theo "_"
        String idImgStt = parts[1]; // Tách IdImgStt từ statusId
        String storagePath = "users/" + userId + "/" + "IdImgStt" + "_" + idImgStt; // Tạo đường dẫn

        // Lấy danh sách ảnh từ thư mục của người dùng
        StorageReference storageRef = storage.getReference(storagePath);
        storageRef.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference item : listResult.getItems()) {
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    imageUris.add(uri); // Thêm URI vào danh sách
                    adapter.notifyDataSetChanged(); // Cập nhật adapter
                }).addOnFailureListener(exception -> {
                    Log.e("FirebaseStorage", "Error getting download URL", exception);
                });
            }
        }).addOnFailureListener(exception -> {
            Log.e("FirebaseStorage", "Failed to list files", exception);
        });
    }



}
