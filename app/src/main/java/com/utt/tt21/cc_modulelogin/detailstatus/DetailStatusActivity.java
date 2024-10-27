package com.utt.tt21.cc_modulelogin.detailstatus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout; // Thêm import LinearLayout
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.utt.tt21.cc_modulelogin.R;
import com.utt.tt21.cc_modulelogin.home.homeAdapter.ImageStringAdapter;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

public class DetailStatusActivity extends AppCompatActivity {

    private TextView tvUserName, tvStatusContent;
    private LinearLayout commentContainer; // Thay ListView thành LinearLayout
    ImageView myAccountImage;
    List<Comment> comments = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_status); // Giao diện detail_status.xml

        commentContainer = findViewById(R.id.commentContainer); // Khởi tạo LinearLayout
        // Nhận dữ liệu từ Intent
        String uid = getIntent().getStringExtra("uid");
        String username = getIntent().getStringExtra("username");
        String statusId = getIntent().getStringExtra("status_id");
        String content = getIntent().getStringExtra("content");
        String profileImageUrl = getIntent().getStringExtra("profile_image");

        // Lấy userId của người dùng đang đăng nhập
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Khởi tạo các view
        myAccountImage = findViewById(R.id.myAccountImage); // Khởi tạo ImageView cho avatar
        ImageView backButton = findViewById(R.id.back_button); // Khởi tạo back_button

        // Thêm sự kiện click cho back_button
        backButton.setOnClickListener(v -> {
            // Quay lại màn hình trước
            finish(); // Kết thúc Activity hiện tại
        });
        // Tải ảnh đại diện vào myAccountImage
        loadUserAvatar(currentUserId, myAccountImage);
        // Xử lý danh sách ảnh (ví dụ: hiển thị trong RecyclerView)
        ArrayList<String> imageList = getIntent().getStringArrayListExtra("image_list");
        RecyclerView recyclerView = findViewById(R.id.rvImage);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        ImageStringAdapter adapter = new ImageStringAdapter(this, imageList);
        recyclerView.setAdapter(adapter);

        tvUserName = findViewById(R.id.tvUserName);
        tvStatusContent = findViewById(R.id.tvStatusContent);
        tvUserName.setText(username);
        tvStatusContent.setText(content);
        ImageView accountImage = findViewById(R.id.accountImage);
        Glide.with(this).load(profileImageUrl).into(accountImage);
        // Thiết lập TextView để chuyển đến CommentLayoutActivity
        TextView commentTextView = findViewById(R.id.CommentText); // Giả sử bạn có TextView với ID này
        commentTextView.setOnClickListener(v -> {
            Intent intent = new Intent(DetailStatusActivity.this, CommentLayoutActivity.class);
            intent.putExtra("uid", uid);
            intent.putExtra("status_id", statusId);
            intent.putExtra("username", username);
            intent.putExtra("content", content);
            intent.putExtra("profileImageUrl", profileImageUrl);
            intent.putStringArrayListExtra("image_list", imageList);
            startActivityForResult(intent, 1); // Sử dụng requestCode là 1
        });

        // Lấy bình luận
        getComments(statusId, uid);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Kiểm tra nếu kết quả từ CommentLayoutActivity
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String statusId = getIntent().getStringExtra("status_id");
            String uid = getIntent().getStringExtra("uid");

            // Log để kiểm tra giá trị
            Log.d("DetailStatusActivity", "uid: " + uid + ", statusId: " + statusId);

            commentContainer.removeAllViews(); // Xóa tất cả bình luận cũ
            getComments(statusId, uid); // Gọi lại phương thức lấy bình luận để cập nhật
        }
    }



    private void getComments(String statusId, String uid) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("list_comment")
                .child(uid)
                .child(statusId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.clear();
                List<String> userProfileImages = new ArrayList<>(); // Danh sách ảnh người bình luận

                for (DataSnapshot userCommentSnapshot : snapshot.getChildren()) {
                    String userId = userCommentSnapshot.getKey();
                    for (DataSnapshot commentSnapshot : userCommentSnapshot.getChildren()) {
                        String commentKey = commentSnapshot.getKey();
                        String content = commentSnapshot.child("content").getValue(String.class);
                        int countLike = commentSnapshot.child("likeCount").getValue(Integer.class);

                        // Lấy tên và ảnh người dùng từ nhánh 'users'
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                String userName = userSnapshot.child("nameProfile").getValue(String.class);
                                String profileImageUrl = userSnapshot.child("profileImageUrl").getValue(String.class); // Giả sử đường dẫn ảnh lưu ở đây

                                Comment comment = new Comment(content, userId, userName, commentKey, statusId, countLike);
                                comments.add(comment);
                                userProfileImages.add(profileImageUrl); // Thêm ảnh vào danh sách
                                addCommentToView(uid, statusId, comment, databaseReference);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("DetailStatusActivity", "Error fetching user data: " + error.getMessage());
                            }
                        });
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DetailStatusActivity", "Error fetching comments: " + error.getMessage());
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

    private void showCommentOptions(String uid,String statusId,Comment comment) {
        // Tạo một AlertDialog để chọn sửa hoặc xóa
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn hành động")
                .setItems(new CharSequence[]{"Sửa", "Xóa"}, (dialog, which) -> {
                    if (which == 0) {
                        // Người dùng chọn sửa
                        showEditCommentDialog(uid,statusId,comment);
                    } else if (which == 1) {
                        // Người dùng chọn xóa
                        deleteComment(uid,statusId,comment);
                    }
                })
                .show();
    }

    private void showEditCommentDialog(String uid,String statusId,Comment comment) {
        // Tạo một AlertDialog với EditText để sửa bình luận
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa bình luận");

        final EditText input = new EditText(this);
        input.setText(comment.getContent());
        builder.setView(input);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
                    String newContent = input.getText().toString();
                    updateComment(uid,statusId,comment, newContent);
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.cancel())
                .show();
    }

    private void updateComment(String uid,String statusId,Comment comment, String newContent) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid(); // Lấy userId
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference commentRef = database.getReference("list_comment")
                .child(uid)
                .child(statusId)
                .child(userid) // Chỉ định nhánh cho userId
                .child(comment.getCommentId()); // Trỏ đến commentId cụ thể

        // In ra giá trị commentId
        Log.d("DetailStatusActivity", "Updating comment with ID: " + comment.getCommentId());

        // Cập nhật nội dung bình luận
        commentRef.child("content").setValue(newContent)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        comment.setContent(newContent); // Cập nhật nội dung trong đối tượng comment
                        Log.d("DetailStatusActivity","Content" + comment.getContent());
                        Log.d("DetailStatusActivity", "Comment updated successfully");
                        Toast.makeText(DetailStatusActivity.this, "Sửa bình luận thành công", Toast.LENGTH_SHORT).show();
                        // Refresh bình luận sau khi cập nhật
                        commentContainer.removeAllViews(); // Xóa tất cả bình luận cũ
                        getComments(statusId, uid);
                    } else {
                        Log.e("DetailStatusActivity", "Failed to update comment");
                    }
                });
    }





    private void deleteComment(String uid,String statusId,Comment comment) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid(); // Lấy userId
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference commentRef = database.getReference("list_comment")
                .child(uid)
                .child(statusId)
                .child(userid) // Chỉ định nhánh cho userId
                .child(comment.getCommentId()); // Trỏ đến commentId cụ thể

        commentRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                commentContainer.removeAllViews(); // Xóa tất cả bình luận cũ
                getComments(statusId, uid); // Lấy lại danh sách bình luận
                Toast.makeText(DetailStatusActivity.this, "Xóa bình luận thành công", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("DetailStatusActivity", "Failed to delete comment");
            }
        });
    }


    private void addCommentToView(String uid, String statusId, Comment comment, DatabaseReference db) {
        // Tạo một view cho bình luận mới
        DatabaseReference likes =  db.child(comment.getUserId()).child(comment.getCommentId());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();
        View commentView = getLayoutInflater().inflate(R.layout.list_comment, null);

        ImageView imgUserProfile = commentView.findViewById(R.id.accountImage);
        TextView tvUserName = commentView.findViewById(R.id.tvCommentUserName);
        tvUserName.setText(comment.getUserName());
        TextView tvCommentContent = commentView.findViewById(R.id.tvCommentContent);
        tvCommentContent.setText(comment.getContent()); // Gán nội dung bình luận
        ImageButton imgLike = commentView.findViewById(R.id.btnLike);
        TextView txtCountLike = commentView.findViewById(R.id.count_Like);
        txtCountLike.setText(String.valueOf(comment.getCountLike()));
        // Đặt ảnh mặc định trước khi tải ảnh từ Firebase Storage
        imgUserProfile.setImageResource(R.drawable.quang_default_avatar);
        likes.child("likes").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    imgLike.setImageResource(R.drawable.ic_heart_filled);
                }else{
                    imgLike.setImageResource(R.drawable.heart);
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
        // Thêm view vào LinearLayout ngay lập tức
        commentContainer.addView(commentView);

        // Tải ảnh đại diện bằng phương thức loadUserAvatar mà không làm chậm việc hiển thị bình luận
        loadUserAvatar(comment.getUserId(), imgUserProfile);
        // phan tim
        imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                likes.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                        if (snapshot.child("likes").hasChild(userid)){
                            likes.child("likes").child(userid).removeValue();
                            likes.child("likeCount").setValue(comment.getCountLike() - 1);
                            imgLike.setImageResource(R.drawable.heart);
                            comment.setCountLike(comment.getCountLike() - 1);
                        }else{
                            likes.child("likes").child(userid).setValue(true);
                            likes.child("likeCount").setValue(comment.getCountLike() + 1);
                            imgLike.setImageResource(R.drawable.ic_heart_filled);

                            comment.setCountLike(comment.getCountLike() + 1);
                        }
                        txtCountLike.setText(String.valueOf(comment.getCountLike()));
                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                    }
                });
            }
        });
        // Thêm sự kiện nhấn giữ cho bình luận
        commentView.setOnLongClickListener(v -> {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            // Kiểm tra xem bình luận có phải của người dùng đang đăng nhập không
            if (comment.getUserId().equals(currentUserId)) {
                showCommentOptions(uid, statusId, comment);
            } else {
                Toast.makeText(this, "Bạn không thể sửa hoặc xóa bình luận này", Toast.LENGTH_SHORT).show();
            }
            return true; // Trả về true để xác nhận rằng sự kiện đã được xử lý
        });
    }



}
