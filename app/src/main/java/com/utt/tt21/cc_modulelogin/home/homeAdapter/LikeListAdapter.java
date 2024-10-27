package com.utt.tt21.cc_modulelogin.home.homeAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.utt.tt21.cc_modulelogin.R;
import com.utt.tt21.cc_modulelogin.search.Account;
import com.utt.tt21.cc_modulelogin.search.BanBeAdapter;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LikeListAdapter extends RecyclerView.Adapter<LikeListAdapter.UserViewHolder> {

    private Context mContext;
    private List<Account> accountList;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    public LikeListAdapter(Context mContext, List<Account> accountList) {
        this.mContext = mContext;
        this.accountList = accountList;
        this.mAuth = FirebaseAuth.getInstance();
        this.userRef = FirebaseDatabase.getInstance().getReference("users");
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follower, parent, false);
        View view = LayoutInflater.from(mContext).inflate(R.layout.line_like, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        Account account = accountList.get(position);
        holder.accountName.setText(account.getNameProfile());

        // Hiển thị hình ảnh người dùng với Glide (nếu có URL)
        if (account.getImgProfile() != null && !account.getImgProfile().isEmpty()) {
            Glide.with(mContext).load(account.getImgProfile()).into(holder.accountImage);
        } else {
            holder.accountImage.setImageResource(R.drawable.ic_default_user); // Hình mặc định
        }

        holder.nickname.setText(account.getDesProfile());
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView accountImage;
        TextView accountName,nickname, accountFollowers;
        Button btnChat;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            accountName = itemView.findViewById(R.id.name);
//            accountFollowers = itemView.findViewById(R.id.soTD);
            btnChat = itemView.findViewById(R.id.btnChat);
            accountImage = itemView.findViewById(R.id.img_avatar);
            nickname = itemView.findViewById(R.id.fullName);
        }
    }
}
