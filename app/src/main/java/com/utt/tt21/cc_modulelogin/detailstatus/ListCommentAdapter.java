package com.utt.tt21.cc_modulelogin.detailstatus;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
public class ListCommentAdapter extends RecyclerView.Adapter<ListCommentAdapter.CommentViewHolder> {

    private List<String> commentList;
    private String currentUserId;
    public ListCommentAdapter(List<String> commentList) {
        this.commentList = commentList;
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        String comment = commentList.get(position);
        holder.tvCommentContent.setText(comment);
      //  holder.count_Like.setText(String.valueOf(comment.getLikeCount()));

        // Lấy tham chiếu đến bài đăng trong Firebase
//        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("list_status").child(currentItem.getUserID()).child(currentItem.getIdStatus());
//
//        // Kiểm tra xem người dùng đã "tim" bài viết này chưa
//        postRef.child("likes").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    holder.btnLike.setImageResource(R.drawable.ic_heart_filled); // Đã "tim"
//                } else {
//                    holder.btnLike.setImageResource(R.drawable.heart); // Chưa "tim"
//                }
//            }
//
//            @Override
//            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) { }
//        });
//        holder.btnLike.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                postRef.child("likes").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            // Nếu đã "tim", bỏ "tim"
//                            postRef.child("likes").child(currentUserId).removeValue();
//                            postRef.child("likeCount").setValue(currentItem.getLikeCount() - 1);
//                            holder.btnLike.setImageResource(R.drawable.ic_heart_outline);
//                            currentItem.setLikeCount(currentItem.getLikeCount() - 1);
//                        } else {
//                            // Nếu chưa "tim", thêm "tim"
//                            postRef.child("likes").child(currentUserId).setValue(true);
//                            postRef.child("likeCount").setValue(currentItem.getLikeCount() + 1);
//                            holder.btnLike.setImageResource(R.drawable.ic_heart_filled);
//                            currentItem.setLikeCount(currentItem.getLikeCount() + 1);
//                        }
//                        holder.count_Like.setText(String.valueOf(currentItem.getLikeCount()));
//                    }
//
//                    @Override
//                    public void onCancelled(@androidx.annotation.NonNull DatabaseError error) { }
//                });
//            }
//        });
   }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvCommentContent;
        ImageButton btnLike;
        TextView count_Like;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCommentContent = itemView.findViewById(R.id.tvCommentContent);
            btnLike = itemView.findViewById(R.id.btnLike);
            count_Like = itemView.findViewById(R.id.count_Like);
        }
    }
}
