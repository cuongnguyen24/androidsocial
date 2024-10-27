package com.utt.tt21.cc_modulelogin.messenger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.utt.tt21.cc_modulelogin.R;
import com.utt.tt21.cc_modulelogin.messenger.chat.ChatActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessengerUserAdapter extends RecyclerView.Adapter<MessengerUserAdapter.MessengerUserHolder> {
    private List<MessengerUserModel> list;
    Context context;
    public MessengerUserAdapter(List<MessengerUserModel> list, Activity context) {
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public MessengerUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messenger_item, parent, false);
        return new MessengerUserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessengerUserHolder holder, int position) {
        holder.nameProfile.setText(list.get(position).getName());
        holder.lastMessenger.setText(list.get(position).getLastMessage());
        holder.timeMessenger.setText(list.get(position).getTimeActivity());
        String imageUrl = list.get(position).getImageUrl();
        // Sử dụng Glide để tải ảnh từ URL
        Glide.with(context)
                .load(imageUrl)
                .placeholder(null)  // Ảnh hiển thị trong khi chờ tải
                .error(R.drawable.profile)       // Ảnh hiển thị khi có lỗi
                .into(holder.profileImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("name", list.get(position).getName());
            intent.putExtra("imageUrl", list.get(position).getImageUrl());
            context.startActivity(intent);
        });

    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MessengerUserHolder extends RecyclerView.ViewHolder {
        private CircleImageView profileImage;
        private TextView nameProfile;
        private TextView lastMessenger;
        private TextView timeMessenger;
        private FirebaseUser mUser;
        public MessengerUserHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            nameProfile = itemView.findViewById(R.id.nameProfile);
            lastMessenger = itemView.findViewById(R.id.lastMessenger);
            timeMessenger = itemView.findViewById(R.id.timeActivity);
            FirebaseAuth auth = FirebaseAuth.getInstance();
            mUser = auth.getCurrentUser();
        }


    }
}
