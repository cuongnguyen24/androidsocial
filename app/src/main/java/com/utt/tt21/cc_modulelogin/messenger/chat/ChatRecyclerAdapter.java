package com.utt.tt21.cc_modulelogin.messenger.chat;

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
import com.utt.tt21.cc_modulelogin.messenger.MessengerUserModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ChatRecyclerHolder> {
    List<ChatMessageModel> list;
    Context context;
    public ChatRecyclerAdapter(List<ChatMessageModel> list, Activity context) {
        list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public ChatRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_recycler_row, parent, false);
        return new ChatRecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRecyclerAdapter.ChatRecyclerHolder holder, int position) {
        if(list.get(position).getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            holder.leftChatView.setVisibility(View.GONE);
            holder.rightChatView.setVisibility(View.VISIBLE);
            holder.rightChatView.setText(list.get(position).getMessage());
        } else {
            holder.leftChatView.setVisibility(View.VISIBLE);
            holder.rightChatView.setVisibility(View.GONE);
            holder.leftChatView.setText(list.get(position).getMessage());
        }
    }





    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ChatRecyclerHolder extends RecyclerView.ViewHolder {
        private TextView leftChatView;
        private TextView rightChatView;

        public ChatRecyclerHolder(@NonNull View itemView) {
            super(itemView);
            leftChatView = itemView.findViewById(R.id.left_chat_textview);
            rightChatView = itemView.findViewById(R.id.right_chat_textview);
        }


    }
}
