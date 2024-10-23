package com.utt.tt21.cc_modulelogin.search;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.utt.tt21.cc_modulelogin.R;

import java.util.List;

public class ListAccountAdapter extends RecyclerView.Adapter<ListAccountAdapter.UserViewHolder> {
    private List<Account> accountList;

    public ListAccountAdapter(List<Account> accountList) {
        this.accountList = accountList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Account account = accountList.get(position);
        holder.accountName.setText(account.getName());
        holder.nickname.setText(account.getNickname());
        holder.accountFollowers.setText(String.valueOf(account.getFollowers()));
        holder.followButton.setText(account.isFollowing() ? "Following" : "Follow");

        // Lấy userId từ account
        String userId = account.getUserId();

        // Gọi phương thức loadUserAvatar để tải ảnh đại diện
        loadUserAvatar(userId, holder.accountImage);
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
        public ImageView accountImage;
        TextView accountName, accountFollowers;
        Button followButton;
        TextView nickname;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            accountName = itemView.findViewById(R.id.accountName);
            accountFollowers = itemView.findViewById(R.id.accountFollowers);
            followButton = itemView.findViewById(R.id.followButton);
            accountImage = itemView.findViewById(R.id.accountImage);
            nickname = itemView.findViewById(R.id.accountNickname);
        }
    }
}
