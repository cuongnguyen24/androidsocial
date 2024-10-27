package com.utt.tt21.cc_modulelogin.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class FollowingFragment extends Fragment {
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    List<Account> userList ;
    ListAccountAdapter adapter;
    RecyclerView recyclerView;
    EditText edtTimKiem;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follower, container, false);
        // khởi tạo recyclerView và UserAdapter
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        edtTimKiem = view.findViewById(R.id.edtTimKiem);
//        // tạo dòng kẻ phân cách item
//        DividerItemDecoration dividerItemDecoration= new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
//        // (Tùy chọn) Bạn có thể tùy chỉnh divider với một drawable riêng nếu muốn:
//        Drawable dividerDrawable = ContextCompat.getDrawable(getContext(), R.drawable.custom_divider);
//        dividerItemDecoration.setDrawable(dividerDrawable);
//        recyclerView.addItemDecoration(dividerItemDecoration);

        userList = new ArrayList<>();
        adapter = new ListAccountAdapter(getContext(),userList,new ListAccountAdapter.IClickListener() {
            @Override
            public void onclickUpdate(List<Account> account) {
                onUpdate(account);
            }

            @Override
            public void onCLickDelete(Account account) {
                onCLickDelete(account);
            }
        },1);
        recyclerView.setAdapter(adapter);
        mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId).child("followings");
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
                        getUserbyID(followingList, query);
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

               getUserbyID(followingList, null);
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
    private void getUserbyID(List<String> followers, String query){
        List<Account> acc = new ArrayList<>();
        DatabaseReference userRef1 = FirebaseDatabase.getInstance().getReference("users");
        for (String followerId: followers) {
            userRef1.child(followerId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String userId = dataSnapshot.getKey(); // Lấy user ID
                        Account account = dataSnapshot.getValue(Account.class);
                        String userLogin = mAuth.getCurrentUser().getUid();
                        account.setDantheodoi(true);
                        account.setUserId(userId);
                        if (account != null  && !account.getUserId().equals(userLogin)) {
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

    }
    private void updateFollowingRecyclerView(List<Account> followingUsers) {
        // Giả sử bạn đã có RecyclerView và Adapter để hiển thị danh sách
        ListAccountAdapter adapter = new ListAccountAdapter(getContext(), followingUsers, new ListAccountAdapter.IClickListener() {
            @Override
            public void onclickUpdate(List<Account> account) {
                onUpdate(account);
            }

            @Override
            public void onCLickDelete(Account account) {
                onCLickDelete(account);
            }
        },1);
        recyclerView.setAdapter(adapter);
    }
    public void refreshData() {
       // mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();
        userList= new ArrayList<>();
        //userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId).child("followings");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> followingList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userId = snapshot.getKey(); // Lấy user ID
                    followingList.add(userId);
                }
                getUserbyID(followingList, null);
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý các lỗi có thể xảy ra
            }
        });

    }
    private void onUpdate(List<Account> account){
        updateFollowingRecyclerView(account);
    };
}
