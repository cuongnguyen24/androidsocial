package com.utt.tt21.cc_modulelogin.detailstatus;

import static com.utt.tt21.cc_modulelogin.MainActivity.CAMERA_REQUEST;
import static com.utt.tt21.cc_modulelogin.MainActivity.PICK_IMAGE_REQUEST;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.utt.tt21.cc_modulelogin.MainActivity;
import com.utt.tt21.cc_modulelogin.R;
import com.utt.tt21.cc_modulelogin.home.homeAdapter.ImageAdapter;
import com.utt.tt21.cc_modulelogin.home.homeAdapter.ImageStringAdapter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CommentLayoutActivity extends AppCompatActivity {
    ImageView myAccountImage,accountImage;
    private TextView tvUserName, tvStatusContent;
    private LinearLayout commentContainer;
    private String replyContent;
    private Button btnUpComment;
    public static final int PICK_IMAGE_REQUEST = 1;
    public static final int CAMERA_REQUEST = 2;
    private List<Uri> imageUris = new ArrayList<>();
    private ImageView chooseImage,takeAPicture;
    private ImageAdapter imageAdapter;
    private ImageStringAdapter imageStringAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_layout);

        // Nhận dữ liệu từ Intent
        String uid = getIntent().getStringExtra("uid");
        String username = getIntent().getStringExtra("username");
        String statusId = getIntent().getStringExtra("status_id");
        String content = getIntent().getStringExtra("content");
        String profileImageUrl = getIntent().getStringExtra("profile_image");

        // Lấy userId của người dùng đang đăng nhập
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Khởi tạo các view
        accountImage = findViewById(R.id.accountImage);
        myAccountImage = findViewById(R.id.MyAccountImage); // Khởi tạo ImageView cho avatar của mình
        // Thêm sự kiện click cho back_button
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            // Quay lại màn hình trước
            finish(); // Kết thúc Activity hiện tại
        });
        //load avatar post
        loadUserAvatar(uid,accountImage);


        tvUserName = findViewById(R.id.tvUserName);
        tvStatusContent = findViewById(R.id.tvStatusContent);
        tvUserName.setText(username);
        tvStatusContent.setText(content);
        ImageView accountImage = findViewById(R.id.accountImage);
        Glide.with(this).load(profileImageUrl).into(accountImage);

        // Tải ảnh đại diện vào myAccountImage
        loadUserAvatar(currentUserId, myAccountImage);

        // Khởi tạo RecyclerView cho ảnh từ Intent
        ArrayList<String> imageList = getIntent().getStringArrayListExtra("image_list");
        RecyclerView recyclerView = findViewById(R.id.rvImage);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageStringAdapter = new ImageStringAdapter(this, imageList); // Dùng adapter này để hiển thị ảnh từ Intent
        recyclerView.setAdapter(imageStringAdapter);
        imageStringAdapter.notifyDataSetChanged();
        fetchUserName(currentUserId);
        EditText replyEditText = findViewById(R.id.replyContent); // Assuming replyEditText is the ID

        btnUpComment = findViewById(R.id.btnUpComment);
        btnUpComment.setOnClickListener(v -> {
            replyContent = replyEditText.getText().toString().trim();
            if (!replyContent.isEmpty()) {
                pushDataToFirebase(replyContent, uid, statusId);
                setResult(RESULT_OK);
                finish();
            } else {
                Log.e("Comment", "Reply content is empty!");
            }
        });

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
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Cho phép chọn nhiều hình ảnh
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn hình ảnh"), PICK_IMAGE_REQUEST);
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == MainActivity.RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) { // Nếu chọn nhiều ảnh
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imageUris.add(imageUri); // Thêm URI vào danh sách
                    }
                } else if (data.getData() != null) { // Nếu chỉ chọn 1 ảnh
                    Uri imageUri = data.getData();
                    imageUris.add(imageUri); // Thêm URI vào danh sách
                }
                // Cập nhật RecyclerView sau khi thêm hình ảnh
                imageAdapter.notifyDataSetChanged(); // Cập nhật lại dữ liệu cho RecyclerView
            }
        }
        // Xử lý ảnh chụp từ camera
        if (requestCode == CAMERA_REQUEST && resultCode == MainActivity.RESULT_OK && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");  // Ảnh dạng Bitmap
            // Chuyển Bitmap thành URI hoặc lưu lại để dùng
            Uri imageUri = getImageUri(photo);  // Hàm getImageUri để chuyển Bitmap thành Uri
            imageUris.add(imageUri);  // Thêm URI của ảnh vào danh sách
            imageAdapter.notifyDataSetChanged();  // Cập nhật lại RecyclerView
        }
    }
    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }
    //mở camera
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST); // Mở camera
    }
    private void uploadImageToStorage(String uid, String statusId) {
        if (imageUris.isEmpty()) {
            Log.e("UploadImage", "No images selected.");
            return; // Nếu không có hình ảnh để upload
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        // Tạo StorageReference với đường dẫn theo yêu cầu
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("list_comment")
                .child(uid) // Thêm uid của người đăng
                .child(statusId); // Thêm statusId

        // Tạo thư mục với định dạng userId_index
        final String initialFolderName = userId + "_1"; // Bắt đầu từ chỉ số 1
        final StorageReference userFolderRef = storageRef.child(initialFolderName); // Tạo thư mục cho user

        // Kiểm tra xem thư mục có tồn tại không và tăng chỉ số nếu cần
        userFolderRef.listAll().addOnSuccessListener(listResult -> {
            int index = 1;

            // Tìm chỉ số tiếp theo bằng cách lặp qua các prefix
            for (StorageReference prefix : listResult.getPrefixes()) {
                String name = prefix.getName();
                // Kiểm tra nếu tên bắt đầu bằng userId
                if (name.startsWith(userId)) {
                    // Lấy chỉ số từ tên thư mục (userId_index) và cập nhật chỉ số
                    String[] parts = name.split("_");
                    if (parts.length > 1) {
                        try {
                            int currentIndex = Integer.parseInt(parts[1]);
                            index = Math.max(index, currentIndex + 1);
                        } catch (NumberFormatException e) {
                            Log.e("UploadImage", "Failed to parse index from folder name: " + name);
                        }
                    }
                }
            }

            // Cập nhật tên thư mục với chỉ số mới
            String folderName = userId + "_" + index; // Tạo tên thư mục mới
            StorageReference updatedUserFolderRef = storageRef.child(folderName); // Tạo thư mục mới với tên đã cập nhật
            Log.d("UploadImage", "Creating folder: " + folderName); // Log để kiểm tra folderName

            // Upload ảnh vào thư mục mới
            int imageIndex = 1; // Chỉ số cho tên tệp ảnh
            for (Uri imageUri : imageUris) {
                String imageName = "image_" + imageIndex + ".jpg"; // Tên tệp mới
                StorageReference imageRef = updatedUserFolderRef.child(imageName); // Tạo đường dẫn đến tệp mới

                // Upload ảnh từ URI
                uploadImage(imageUri, imageRef); // Upload ảnh vào tệp mới
                imageIndex++; // Tăng chỉ số
            }
        }).addOnFailureListener(exception -> {
            Log.e("UploadImage", "Failed to list files", exception);
        });
    }



    private void uploadImage(Uri imageUri, StorageReference storageRef) {
        // Upload ảnh từ URI
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Upload thành công
                    Log.d("UploadImage", "Image uploaded successfully to " + storageRef.getPath());
                }).addOnFailureListener(exception -> {
                    // Xử lý lỗi nếu upload thất bại
                    Log.e("UploadImage", "Failed to upload image", exception);
                });
    }
    private void pushDataToFirebase( String replyContent,String uid,String statusId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid(); // Lấy userId
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("list_comment")
                .child(uid)
                .child(statusId);

        // Kiểm tra xem node của userId đã tồn tại hay chưa
        reference.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int index = 1; // Bắt đầu từ 1
                String idStatus;

                // Tìm key tiếp theo cho idStatus
                while (snapshot.hasChild(userid + "_" + index)) {
                    index++;
                }
                idStatus = userid + "_" + index; // Tạo idStatus mới

                // Đẩy dữ liệu vào node con mới
                reference.child(userid).child(idStatus).child("content").setValue(replyContent);
                reference.child(userid).child(idStatus).child("commentCount").setValue(0);
                reference.child(userid).child(idStatus).child("likeCount").setValue(0);
                reference.child(userid).child(idStatus).child("list_comment").setValue(null);
//                uploadImageToStorage(uid, statusId);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu cần
                Log.e("PushData", "Database error: " + error.getMessage());
            }
        });
    }
    private void fetchUserName(String userId) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("nameProfile");

        // Lấy dữ liệu tên người dùng
        databaseRef.get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                String userName = dataSnapshot.getValue(String.class);
                TextView tvMyName = findViewById(R.id.tvMyName);
                tvMyName.setText(userName);
            }
        }).addOnFailureListener(e -> {
            Log.e("Firebase", "Failed to fetch user name", e);
        });
    }
}
