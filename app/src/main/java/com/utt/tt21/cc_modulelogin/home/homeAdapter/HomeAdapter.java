package com.utt.tt21.cc_modulelogin.home.homeAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.utt.tt21.cc_modulelogin.R;
import com.utt.tt21.cc_modulelogin.home.homeModel.HomeModel;
import com.utt.tt21.cc_modulelogin.profile.guestProfile.GuestProfileActivity;
import com.utt.tt21.cc_modulelogin.profile.profileModel.ImageItems;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeHolder> {

    private List<HomeModel> list;
    Context context;
    private ImageStringAdapter imageStringAdapter;
    public HomeAdapter(List<HomeModel> list, Context context) {
        this.list = list;
        this.context = context;
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
        holder.recyclerViewImage.setAdapter(imageStringAdapter);

        // Xu ly count like
        int countLike = list.get(position).getLikeCount();

        if(countLike == 0)
        {
            holder.count_Like.setVisibility(View.INVISIBLE);
        }
        else {
            holder.count_Like.setText(countLike+"");
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

        }
    }
}
