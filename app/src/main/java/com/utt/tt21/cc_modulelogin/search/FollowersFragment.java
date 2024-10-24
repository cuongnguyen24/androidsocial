package com.utt.tt21.cc_modulelogin.search;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utt.tt21.cc_modulelogin.R;

import java.util.ArrayList;
import java.util.List;

public class FollowersFragment extends Fragment {
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    List<Account> userList ;
    ListAccountAdapter adapter;
    RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follower, container, false);

        // khởi tạo recyclerView và UserAdapter
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // tạo dòng kẻ phân cách item
        DividerItemDecoration dividerItemDecoration= new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        // (Tùy chọn) Bạn có thể tùy chỉnh divider với một drawable riêng nếu muốn:
        Drawable dividerDrawable = ContextCompat.getDrawable(getContext(), R.drawable.custom_divider);
        dividerItemDecoration.setDrawable(dividerDrawable);
        recyclerView.addItemDecoration(dividerItemDecoration);

        userList = new ArrayList<>();
        adapter = new ListAccountAdapter(getContext(),userList);
        recyclerView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId).child("followers");
        loadUsers();
        return view;
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
                userList = getUserbyID(followingList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
    private List<Account> getUserbyID(List<String> followers){
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
                        if (account != null  && !account.getUserId().equals(userLogin)) {
                            acc.add(account);
                        }
                        updateFollowingRecyclerView(acc);

                    }
                    else {
                        Toast.makeText(getContext(), "Bạn chưa theo dõi ai cả.", Toast.LENGTH_SHORT).show();
                    }
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
        ListAccountAdapter adapter = new ListAccountAdapter(getContext(), followingUsers);
        recyclerView.setAdapter(adapter);
    }
}
