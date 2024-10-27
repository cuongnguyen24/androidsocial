package com.utt.tt21.cc_modulelogin.messenger.chat;

import android.app.Activity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.utt.tt21.cc_modulelogin.R;

import java.util.List;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ChatRecyclerHolder> {
    List<ChatMessageModel> list;
    Context context;
    String uid;
    public ChatRecyclerAdapter(List<ChatMessageModel> list, Activity context, String uid) {
        this.list = list;
        this.context = context;
        this.uid = uid;
    }


    @NonNull
    @Override
    public ChatRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_recycler_row, parent, false);
        return new ChatRecyclerHolder(view);
    }

    @Override

    public void onBindViewHolder(@NonNull ChatRecyclerAdapter.ChatRecyclerHolder holder, int position) {
        if(list.get(position).getSenderId().equals(uid)) {
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftChatView.setVisibility(View.GONE);
            holder.rightChatView.setVisibility(View.VISIBLE);
            holder.rightChatView.setText(list.get(position).getMessage());
        } else {
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.rightChatView.setVisibility(View.GONE);
            holder.leftChatView.setVisibility(View.VISIBLE);
            holder.leftChatView.setText(list.get(position).getMessage());
        }
    }





    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    static class ChatRecyclerHolder extends RecyclerView.ViewHolder {
        private TextView leftChatView;
        private TextView rightChatView;
        private LinearLayout rightLayout;
        private LinearLayout leftLayout;
        public ChatRecyclerHolder(@NonNull View itemView) {
            super(itemView);
            leftChatView = itemView.findViewById(R.id.left_chat_textview);
            rightChatView = itemView.findViewById(R.id.right_chat_textview);
            rightLayout = itemView.findViewById(R.id.right_chat_layout);
            leftLayout = itemView.findViewById(R.id.left_chat_layout);

        }


    }
}
