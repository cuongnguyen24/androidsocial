package com.utt.tt21.cc_modulelogin.search;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.utt.tt21.cc_modulelogin.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TKAdapter extends RecyclerView.Adapter<TKAdapter.UserViewHolder>{
    private Context mContext;
    private List<Account> accountList;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    public TKAdapter(Context mContext, List<Account> accountList) {
        this.mContext = mContext;
        this.accountList = accountList;
        this.mAuth = FirebaseAuth.getInstance();
        this.userRef = FirebaseDatabase.getInstance().getReference("users");
    }
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.line_tkiem, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Account account = accountList.get(position);
        holder.accountName.setText(account.getNameProfile());

        // Hiển thị hình ảnh người dùng với Glide (nếu có URL)
        if (account.getImgProfile() != null && !account.getImgProfile().isEmpty()) {
            Glide.with(mContext).load(account.getImgProfile()).into(holder.accountImage);
        } else {
            holder.accountImage.setImageResource(R.drawable.ic_default_user); // Hình mặc định
        }
        holder.nickname.setText("Có thể bạn biet " + account.getNameProfile());
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView accountImage;
        TextView accountName,nickname, accountFollowers;
        Button followButton, btnXoa;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            accountName = itemView.findViewById(R.id.name);
//            accountFollowers = itemView.findViewById(R.id.soTD);
            followButton = itemView.findViewById(R.id.btnfollow);
            accountImage = itemView.findViewById(R.id.img_avatar);
            nickname = itemView.findViewById(R.id.fullName);
            btnXoa = itemView.findViewById(R.id.btnXoa);
        }
    }

}
