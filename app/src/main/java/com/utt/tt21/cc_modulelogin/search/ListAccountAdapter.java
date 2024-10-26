package com.utt.tt21.cc_modulelogin.search;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.utt.tt21.cc_modulelogin.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListAccountAdapter extends RecyclerView.Adapter<ListAccountAdapter.UserViewHolder> {
//    private List<Account> accountList;
//    public ListAccountAdapter(List<Account> accountList) {
//        this.accountList = accountList;
//    }
    private Context mContext;
    private List<Account> accountList;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    int vitri;
    private IClickListener clickListener;
    // bắt sk -> sd interface-> đưa sk ra bên ngoài main để xử lí
    public interface IClickListener{
        void onclickUpdate(Account account);
        void onCLickDelete(Account account);
    }


    public ListAccountAdapter(Context mContext, List<Account> accountList,IClickListener listener,int vitri) {
        this.mContext = mContext;
        this.accountList = accountList;
        this.clickListener = listener;
        this.vitri = vitri;
        this.mAuth = FirebaseAuth.getInstance();
        this.userRef = FirebaseDatabase.getInstance().getReference("users");
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follower, parent, false);
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_follower, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        //Toast.makeText(mContext, "vi tri" + vitri, Toast.LENGTH_SHORT).show();
        Account account = accountList.get(position);
        holder.accountName.setText(account.getNameProfile());

        // Hiển thị hình ảnh người dùng với Glide (nếu có URL)
        if (account.getImgProfile() != null && !account.getImgProfile().isEmpty()) {
            Glide.with(mContext).load(account.getImgProfile()).into(holder.accountImage);
        } else {
            holder.accountImage.setImageResource(R.drawable.ic_default_user); // Hình mặc định
        }

        holder.nickname.setText(account.getDesProfile());
//        holder.accountFollowers.setText(String.valueOf(account.getFollowers()));
       // holder.followButton.setText(account.isFollowing() ? "Following" : "Follow");

        //currentUserId được lấy từ Firebase Authentication - ngdung hiện tại
        String currentUserId = mAuth.getCurrentUser().getUid();
        //ID của người mà bạn muốn theo dõi
        String targetUserId = account.getUserId();

        // Kiểm tra trạng thái follow
        checkFollowStatus(currentUserId, targetUserId, holder.followButton);
        // Xử lý nút follow
        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.followButton.getText().toString().equals("Follow")) {
                    followUser(currentUserId, targetUserId, holder.followButton, account);
                } else {
                    // Tạo dialog
                    final Dialog dialog = new Dialog(mContext);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    // Đặt background trong suốt cho dialog để thấy bo tròn rõ ràng
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog.setContentView(R.layout.diglog_unfollow);
                    // kích ra bên ngoài ko mất dialog
                    dialog.setCanceledOnTouchOutside(false);
                    // Tìm các thành phần trong layout của dialog
                    CircleImageView dialogIcon = dialog.findViewById(R.id.img_avatar);
                    TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
                    Button buttonUnfollow = dialog.findViewById(R.id.btn_unfollow);
                    Button buttonCancel = dialog.findViewById(R.id.btn_cancel);
                    buttonUnfollow.setText("Bỏ theo dõi");
                    // Thiết lập nội dung (tùy chỉnh nếu cần)

                    dialogMessage.setText("Bỏ theo dõi "+ account.getNameProfile()+ " ?");
                    if (account.getImgProfile() != null && !account.getImgProfile().isEmpty()) {
                        Glide.with(mContext).load(account.getImgProfile()).into(dialogIcon);
                    } else {
                        dialogIcon.setImageResource(R.drawable.ic_default_user); // Hình mặc định
                    }

                    // Xử lý sự kiện cho các nút
                    buttonUnfollow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            unfollowUser(currentUserId, targetUserId, holder.followButton, account);
                            clickListener.onclickUpdate(account);
                            dialog.dismiss();
                        }
                    });
                    buttonCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Xử lý khi chọn Hủy
                            dialog.dismiss();
                        }
                    });
                    // Hiển thị dialog
                    dialog.show();
                }
            }
        });
        if(vitri == 1){
            holder.btnXoa.setVisibility(View.GONE);
            // Di chuyển Button sang vị trí của ImageButton
            // Lấy LayoutParams  thông tin vị trí của Button.
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.followButton.getLayoutParams();

            // Xóa các quy tắc vị trí cũ (nếu có)
            layoutParams.removeRule(RelativeLayout.ALIGN_LEFT);
            layoutParams.removeRule(RelativeLayout.ALIGN_RIGHT);
            layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_START);

            // Đặt quy tắc để căn Button về cuối dòng
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);

            // Áp dụng LayoutParams mới cho Button
            holder.followButton.setLayoutParams(layoutParams);
        }

        // xu lí nút xóa
        holder.btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onCLickDelete(account);
               // showCustomDialog();
            }
        });

        // Lấy userId từ account
       String userId = account.getUserId();

        // Gọi phương thức loadUserAvatar để tải ảnh đại diện
      //  loadUserAvatar(userId, holder.accountImage);
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
    @Override
    public int getItemCount() {
        return accountList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView accountImage;
        TextView accountName,nickname, accountFollowers;
        Button followButton;
        ImageButton btnXoa;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            accountName = itemView.findViewById(R.id.name);
//            accountFollowers = itemView.findViewById(R.id.soTD);
            followButton = itemView.findViewById(R.id.btnfollow);
            accountImage = itemView.findViewById(R.id.img_avatar);
            nickname = itemView.findViewById(R.id.fullName);
            btnXoa = itemView.findViewById(R.id.btnXoa);
            // Lấy LinearLayout chứa các nút
        }
    }
    // Kiểm tra trạng thái follow
    // Hàm kiểm tra xem currentUserId có đang follow targetUserId hay không
    private void checkFollowStatus(String currentUserId, String targetUserId, Button followButton) {
        userRef.child(currentUserId).child("followings").child(targetUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    followButton.setText("Unfollow");
                } else {
                    followButton.setText("Follow");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi
            }
        });
    }
     //Hàm follow người dùng
    private void followUser(String currentUserId, String targetUserId, Button followButton, Account account) {
        userRef.child(currentUserId).child("followings").child(targetUserId).setValue(true);
        userRef.child(targetUserId).child("followers").child(currentUserId).setValue(true);
        followButton.setText("Unfollow");
    }

    // Hàm unfollow
    private void unfollowUser(String currentUserId, String targetUserId, Button followButton, Account account) {
        userRef.child(targetUserId).child("followers").child(currentUserId).removeValue();
        userRef.child(currentUserId).child("followings").child(targetUserId).removeValue();
        followButton.setText("Follow");

    }
}

