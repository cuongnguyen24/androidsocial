package com.utt.tt21.cc_modulelogin.search;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utt.tt21.cc_modulelogin.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowersFragment extends Fragment {
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    List<Account> userList ;
    ListAccountAdapter adapter;
    RecyclerView recyclerView;
    EditText edtTimKiem;
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follower, container, false);

        // khởi tạo recyclerView và UserAdapter
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        edtTimKiem = view.findViewById(R.id.edtTimKiem);
        // tạo dòng kẻ phân cách item
        DividerItemDecoration dividerItemDecoration= new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        // (Tùy chọn) Bạn có thể tùy chỉnh divider với một drawable riêng nếu muốn:
        Drawable dividerDrawable = ContextCompat.getDrawable(getContext(), R.drawable.custom_divider);
        dividerItemDecoration.setDrawable(dividerDrawable);
        recyclerView.addItemDecoration(dividerItemDecoration);

        userList = new ArrayList<>();
        adapter = new ListAccountAdapter(getContext(), userList, new ListAccountAdapter.IClickListener() {
            @Override
            public void onclickUpdate(List<Account> account) {
                onUpdate(account);
            }

            @Override
            public void onCLickDelete(Account account) {
                onClickDelete(account);
            }
        },2);
        recyclerView.setAdapter(adapter);
        mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId).child("followers");
        loadUsers();
        edtTimKiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        // Load users tu Firebase
        loadUsers();
        return view;
    }
    private void searchUsers(String query) {
        userRef.orderByChild("nameProfile")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userList.clear();
                        userList.clear();
                        List<String> followingList = new ArrayList<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String userId = snapshot.getKey(); // Lấy user ID
                            followingList.add(userId);
                        }
                        userList = getUserbyID(followingList, query);
                        if(followingList.size() == 0){
                            updateFollowingRecyclerView(new ArrayList<>());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Xử lý lỗi khi truy vấn thất bại
                    }
                });
    }
    private void loadUsers() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                List<String> followingList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userId = snapshot.getKey(); // Lấy user ID
                    followingList.add(userId);
                }
                userList = getUserbyID(followingList, null);
                if(followingList.size() == 0){
                    updateFollowingRecyclerView(new ArrayList<>());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
    private List<Account> getUserbyID(List<String> followers, String query){
        DatabaseReference userRef1 = FirebaseDatabase.getInstance().getReference("users");
        List<Account> acc = new ArrayList<>();
        for (String followerId: followers) {
            userRef1.child(followerId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String userId = dataSnapshot.getKey(); // Lấy user ID
                        Account account = dataSnapshot.getValue(Account.class);
                        String userLogin = mAuth.getCurrentUser().getUid();
                        account.setUserId(userId);
                        boolean hasFollowing = dataSnapshot.hasChild("followings") && dataSnapshot.child("followings").child(userLogin).exists();
                        boolean hasFollower = dataSnapshot.hasChild("followers") && dataSnapshot.child("followers").child(userLogin).exists();
                        if (account != null  && !account.getUserId().equals(userLogin)) {
                            if(hasFollowing && hasFollower){
                                account.setDantheodoi(true);
                            }
                            if(query != null){
                                if (account != null && account.getNameProfile() != null
                                        && account.getNameProfile().toLowerCase().contains(query)){
                                    acc.add(account);
                                }
                            }else
                                acc.add(account);
                        }
                        if(followerId.equals(followers.get(followers.size() - 1))){
                            updateFollowingRecyclerView(acc);

                        }

                    }
//                    else {
//                        Toast.makeText(getContext(), "Bạn chưa theo dõi ai cả.", Toast.LENGTH_SHORT).show();
//                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Lỗi khi lấy danh sách following.", Toast.LENGTH_SHORT).show();

                }
            });
        }
        return  acc;
    }
    private void updateFollowingRecyclerView(List<Account> followingUsers) {
        // Giả sử bạn đã có RecyclerView và Adapter để hiển thị danh sách
        ListAccountAdapter adapter = new ListAccountAdapter(getContext(), followingUsers, new ListAccountAdapter.IClickListener() {
            @Override
            public void onclickUpdate(List<Account> account) {
            }

            @Override
            public void onCLickDelete(Account account) {
                    onClickDelete(account);
            }
        },2);
        recyclerView.setAdapter(adapter);
    }
    private void onClickDelete(Account account) {
        // Tạo dialog
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.diglog_unfollow);
        // kích ra bên ngoài ko mất dialog
        dialog.setCanceledOnTouchOutside(false);

        // Tìm các thành phần trong layout của dialog
        CircleImageView dialogIcon = dialog.findViewById(R.id.img_avatar);
        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
        Button buttonUnfollow = dialog.findViewById(R.id.btn_unfollow);
        Button buttonCancel = dialog.findViewById(R.id.btn_cancel);

        // Thiết lập nội dung (tùy chỉnh nếu cần)
        dialogMessage.setText("Xóa người theo dõi "+ account.getNameProfile()+ " ?");

        if (account.getImgProfile() != null && !account.getImgProfile().isEmpty()) {
            Glide.with(getContext()).load(account.getImgProfile()).into(dialogIcon);
        } else {
            dialogIcon.setImageResource(R.drawable.ic_default_user); // Hình mặc định
        }

        // Xử lý sự kiện cho các nút
        buttonUnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRef = FirebaseDatabase.getInstance().getReference("users");
                // Xử lý khi chọn Bỏ theo dõi
                String currentUserId = mAuth.getCurrentUser().getUid();
                //ID của người mà bạn muốn theo dõi
                String targetUserId = account.getUserId();
                userRef.child(currentUserId).child("followers").child(targetUserId).removeValue();
                userRef.child(targetUserId).child("followings").child(currentUserId).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        dialog.dismiss();
                    }
                });
                userList.remove(account);
                updateFollowingRecyclerView(userList);
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
    public void refreshData() {
        mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();
        userList= new ArrayList<>();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId).child("followers");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> followingList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userId = snapshot.getKey(); // Lấy user ID
                    followingList.add(userId);
                }
                userList = getUserbyID(followingList, null);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });

    }
    private void onUpdate(List<Account> account){
        updateFollowingRecyclerView(account);
    }

}
