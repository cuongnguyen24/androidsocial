package com.utt.tt21.cc_modulelogin.home.homeAdapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.utt.tt21.cc_modulelogin.R;

import com.utt.tt21.cc_modulelogin.detailstatus.DetailStatusActivity;
import com.utt.tt21.cc_modulelogin.home.homeModel.HomeModel;
import com.utt.tt21.cc_modulelogin.post.EditPostActivity;
import com.utt.tt21.cc_modulelogin.profile.guestProfile.GuestProfileActivity;
import com.utt.tt21.cc_modulelogin.profile.profileModel.ImageItems;
import com.utt.tt21.cc_modulelogin.search.Account;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeHolder> {

    private List<HomeModel> list;
    Context context;
    private ImageStringAdapter imageStringAdapter;
    private int countViewStatus = 0;
    private FirebaseDatabase database;
    private String currentUserId;
    public HomeAdapter(List<HomeModel> list, Context context) {
        this.list = list;
        this.context = context;
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item, parent, false);
        return new HomeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeHolder holder, int position) {
        holder.tvUserName.setText(list.get(position).getUserName());
        holder.tvTime.setText(list.get(position).getTimestamp());

        String imageUrl = list.get(position).getProfileImage();
        // Sử dụng Glide để tải ảnh từ URL
        Glide.with(context)
                .load(imageUrl)
                .placeholder(null)  // Ảnh hiển thị trong khi chờ tải
                .error(R.drawable.profile)       // Ảnh hiển thị khi có lỗi
                .into(holder.profileImage);

        HomeModel currentItem = list.get(position);
        // Hiển thị userID và idStatus nếu cần
        String userID = currentItem.getUserID();
        String idStatus = currentItem.getIdStatus();
        String content = currentItem.getContent();


        holder.count_Like.setText(String.valueOf(currentItem.getLikeCount()));

        // Lấy tham chiếu đến bài đăng trong Firebase
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("list_status").child(currentItem.getUserID()).child(currentItem.getIdStatus());

        // Kiểm tra xem người dùng đã "tim" bài viết này chưa
        postRef.child("likes").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.btnLike.setImageResource(R.drawable.ic_heart_filled); // Đã "tim"
                } else {
                    holder.btnLike.setImageResource(R.drawable.heart); // Chưa "tim"
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postRef.child("likes").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Nếu đã "tim", bỏ "tim"
                            postRef.child("likes").child(currentUserId).removeValue();
                            postRef.child("likeCount").setValue(currentItem.getLikeCount() - 1);
                            holder.btnLike.setImageResource(R.drawable.ic_heart_outline);
                            currentItem.setLikeCount(currentItem.getLikeCount() - 1);
                        } else {
                            // Nếu chưa "tim", thêm "tim"
                            postRef.child("likes").child(currentUserId).setValue(true);
                            postRef.child("likeCount").setValue(currentItem.getLikeCount() + 1);
                            holder.btnLike.setImageResource(R.drawable.ic_heart_filled);
                            currentItem.setLikeCount(currentItem.getLikeCount() + 1);
                        }
                        holder.count_Like.setText(String.valueOf(currentItem.getLikeCount()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
        });
        holder.count_Like.setOnClickListener(v -> {
            DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("list_status").child(currentItem.getUserID())
                    .child(currentItem.getIdStatus())
                    .child("likes");
            List<Account> likeList = new ArrayList<>();
            likesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot likeSnapshot : snapshot.getChildren()) {
                        String userId = likeSnapshot.getKey();
                        // Lấy thông tin người dùng từ node "users"
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                Account account = userSnapshot.getValue(Account.class);
                                if (account != null) {
                                    likeList.add(account);
                                }
                                // Khi hoàn thành việc tải dữ liệu, hiển thị dialog danh sách người dùng
                                if (snapshot.getChildrenCount() == likeList.size()) {
                                    showLikeListDialog(likeList);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        });

        holder.btnComment.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailStatusActivity.class);
            intent.putExtra("uid", currentItem.getUserID());
            intent.putExtra("status_id", currentItem.getIdStatus());
            intent.putExtra("content", currentItem.getContent());// Hoặc định danh khác cho bài viết
            intent.putExtra("username", currentItem.getUserName());

            // Truyền danh sách URL của ảnh (dạng ArrayList)
            ArrayList<String> imageList = new ArrayList<>(currentItem.getPostImage());
            intent.putStringArrayListExtra("image_list", imageList);

            // Truyền URL của ảnh đại diện
            intent.putExtra("profile_image", currentItem.getProfileImage());

            context.startActivity(intent);
        });
        if(userID != null)
        {
            String mUser = holder.mUser.getUid();
            if(userID.equals(mUser))
            {
                holder.btnMore.setVisibility(View.VISIBLE);
            }
            else {
                holder.btnMore.setVisibility(View.INVISIBLE);
            }
        }
        holder.btnMore.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Chọn hành động")
                    .setItems(new CharSequence[]{"Sửa", "Xóa"}, (dialog, which) -> {
                        if (which == 0) {
                            // Người dùng chọn sửa
                            Intent intent = new Intent(context, EditPostActivity.class);
                            intent.putExtra("uid", currentItem.getUserID());
                            intent.putExtra("status_id", currentItem.getIdStatus());
                            intent.putExtra("content", currentItem.getContent());
                            intent.putExtra("username", currentItem.getUserName());
                            // Truyền URL của ảnh đại diện
                            intent.putExtra("profile_image", currentItem.getProfileImage());
                            context.startActivity(intent);
                        } else if (which == 1) {
                            // Hiện hộp thoại xác nhận khi xóa
                            showDeleteConfirmationDialog(currentItem.getUserID(), currentItem.getIdStatus());
                        }
                    })
                    .show();
        });


        // Xử lý sự kiện khi nhấn vào bài viết
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailStatusActivity.class);
            intent.putExtra("uid", currentItem.getUserID());
            intent.putExtra("status_id", currentItem.getIdStatus());
            intent.putExtra("content", currentItem.getContent());// Hoặc định danh khác cho bài viết
            intent.putExtra("username", currentItem.getUserName());

            // Truyền danh sách URL của ảnh (dạng ArrayList)
            ArrayList<String> imageList = new ArrayList<>(currentItem.getPostImage());
            intent.putStringArrayListExtra("image_list", imageList);

            // Truyền URL của ảnh đại diện
            intent.putExtra("profile_image", currentItem.getProfileImage());

            context.startActivity(intent);
        });
//        Random random = new Random();
//        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
//        Glide.with(context.getApplicationContext())
//                .load(list.get(position).getPostImage())
//                .placeholder(new ColorDrawable(color))
//                .timeout(7000)
//                .into(holder.recyclerViewImage);


        List<String> listImg = list.get(position).getPostImage();
        holder.recyclerViewImage.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        imageStringAdapter = new ImageStringAdapter(context, listImg);
        imageStringAdapter.notifyDataSetChanged();
        holder.recyclerViewImage.setAdapter(imageStringAdapter);
        holder.recyclerViewImage.setHasFixedSize(true);

        // Xu ly count like
        int countLike = list.get(position).getLikeCount();

        if(countLike == 0)
        {
            holder.count_Like.setVisibility(View.INVISIBLE);
        }
        else {
            holder.count_Like.setVisibility(View.VISIBLE);
        }
        //xy ly count cmt
        int countCmt = list.get(position).getCmtCount();

        if(countCmt == 0)
        {
            holder.tvCmtCount.setVisibility(View.INVISIBLE);
        }
        else {
            holder.tvCmtCount.setText(countCmt+"");
        }
        //xy ly count reup
        int countReUp = list.get(position).getReupCount();

        if(countReUp == 0)
        {
            holder.tvReupCount.setVisibility(View.INVISIBLE);
        }
        else {
            holder.tvReupCount.setText(countReUp+"");
        }
        //xy ly count post
        int countPost = list.get(position).getPostCount();

        if(countCmt == 0)
        {
            holder.tvPostCount.setVisibility(View.INVISIBLE);
        }
        else {
            holder.tvPostCount.setText(countPost+"");
        }
        holder.tvDes.setText(list.get(position).getContent());
        // Cường: Thêm sự kiện click vào profileImage và tvUserName
        setOnClickListener(holder, position);
    }
    // hien thi tim
    private void showLikeListDialog( List<Account> likeList) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_like_list);
        RecyclerView recyclerView = dialog.findViewById(R.id.like_rcvView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        LikeListAdapter adapter = new LikeListAdapter(context, likeList);
        recyclerView.setAdapter(adapter);
        dialog.show();
    private void deletePost(String userId, String statusId) {
        // Khởi tạo Firebase Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("list_status").child(userId).child(statusId);

        // Xóa bài viết khỏi Realtime Database
        databaseRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseDatabase", "Post deleted successfully.");
                    // Xóa ảnh khỏi Firebase Storage
                    deleteImagesFromStorage(userId, statusId);
                    Toast.makeText(context, "Bài viết đã được xóa.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(exception -> {
                    Log.e("FirebaseDatabase", "Error deleting post", exception);
                    Toast.makeText(context, "Lỗi khi xóa bài viết.", Toast.LENGTH_SHORT).show();
                });
    }

    // Phương thức để hiển thị hộp thoại xác nhận xóa
    private void showDeleteConfirmationDialog(String userId, String statusId) {
        AlertDialog.Builder confirmationBuilder = new AlertDialog.Builder(context);
        confirmationBuilder.setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa bài viết này không?")
                .setPositiveButton("Có", (dialog, which) -> {
                    // Gọi phương thức xóa bài viết
                    deletePost(userId, statusId);
                })
                .setNegativeButton("Không", (dialog, which) -> {
                    dialog.dismiss(); // Đóng hộp thoại
                })
                .show();
    }

    private void deleteImagesFromStorage(String userId, String statusId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String[] parts = statusId.split("_");
        String idImgStt = parts[1];

        // Xây dựng đường dẫn đến thư mục ảnh
        String imagesFolderPath = "users/" + userId + "/IdImgStt_" + idImgStt + "/";
        StorageReference imagesFolderRef = storage.getReference(imagesFolderPath);

        // Xóa tất cả các ảnh trong thư mục
        imagesFolderRef.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference item : listResult.getItems()) {
                item.delete()
                        .addOnSuccessListener(aVoid -> {
                            Log.d("FirebaseStorage", "Image deleted: " + item.getName());
                        })
                        .addOnFailureListener(exception -> {
                            Log.e("FirebaseStorage", "Error deleting image", exception);
                        });
            }
        }).addOnFailureListener(exception -> {
            Log.e("FirebaseStorage", "Error listing images", exception);
        });
    }
    // Cường: thêm setonclick để vào trang profile guest
    private void setOnClickListener(HomeHolder holder, int position) {
        // Lấy uid của người đăng bài
        String uid = list.get(position).getUserID();

        // Thiết lập sự kiện nhấn cho profileImage
        holder.profileImage.setOnClickListener(v -> {
            //            Toast.makeText(context, "UID: " + uid, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, GuestProfileActivity.class);
            intent.putExtra("uid", uid);
            context.startActivity(intent);
        });

        // Thiết lập sự kiện nhấn cho tvUserName
        holder.tvUserName.setOnClickListener(v -> {
            Intent intent = new Intent(context, GuestProfileActivity.class);
            intent.putExtra("uid", uid);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class HomeHolder extends RecyclerView.ViewHolder{

        private CircleImageView profileImage;
        private TextView tvUserName, tvTime, count_Like, tvCmtCount, tvPostCount, tvReupCount, tvDes;
        private RecyclerView recyclerViewImage;
        private ImageButton btnLike, btnComment, btnReUp, btnPost;
        private ImageAdapter imageAdapter;
        private ImageItems imageItems;
        private ImageButton btnMore;
        private FirebaseUser mUser;
        public HomeHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.detailProfileImage);
            tvUserName = itemView.findViewById(R.id.tvName);
            tvDes = itemView.findViewById(R.id.tvDes);
            tvTime = itemView.findViewById(R.id.tvTimeStamp);
            count_Like = itemView.findViewById(R.id.count_Like);
            tvCmtCount = itemView.findViewById(R.id.tvCountCmt);
            tvPostCount = itemView.findViewById(R.id.tvCountPost);
            tvReupCount = itemView.findViewById(R.id.tvCountReUp);
            recyclerViewImage = itemView.findViewById(R.id.rcv_postImage);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnReUp = itemView.findViewById(R.id.btnReup);
            btnPost = itemView.findViewById(R.id.btnPost);
            btnMore = itemView.findViewById(R.id.btnMore);
            FirebaseAuth auth = FirebaseAuth.getInstance();
             mUser = auth.getCurrentUser();
        }
    }
}
