package com.utt.tt21.cc_modulelogin.search;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoiiFragment extends Fragment {
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    List<Account> userList ;
    BanBeAdapter adapter;
    String userLogin;
    RecyclerView recyclerView;
    EditText edtTimKiem;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follower, container, false);

        // khởi tạo recyclerView và UserAdapter
        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        edtTimKiem = view.findViewById(R.id.edtTimKiem);

        // tạo dòng kẻ phân cách item
        DividerItemDecoration dividerItemDecoration= new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        // (Tùy chọn) Bạn có thể tùy chỉnh divider với một drawable
        Drawable dividerDrawable = ContextCompat.getDrawable(getContext(), R.drawable.custom_divider);
        dividerItemDecoration.setDrawable(dividerDrawable);
        recyclerView.addItemDecoration(dividerItemDecoration);

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
        userList = new ArrayList<>();
        adapter = new BanBeAdapter(getContext(),userList);
        recyclerView.setAdapter(adapter);
        mAuth = FirebaseAuth.getInstance();
        userLogin = mAuth.getCurrentUser().getUid();
        // Khởi tạo tham chiếu và xác thực Firebase
        userRef = FirebaseDatabase.getInstance().getReference("users");
        // Tải người dùng từ Firebase
        loadUsers();
        return view;
    }
    private void searchUsers(String query) {
        userRef.orderByChild("nameProfile")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userList.clear();
                        Map<String, Boolean> following =  new HashMap<>();
                        Object test =  dataSnapshot.child("followings").getValue();
                        if(dataSnapshot.hasChild("followings") && (test instanceof java.util.HashMap)){
                            following =  (Map<String, Boolean>) dataSnapshot.child("followings").getValue() ;
                        }
                        if (following == null) {
                            following = new HashMap<>();
                        }
                        String id = following.size() > 0 ? following.keySet().toArray()[following.size() - 1].toString() : "";
                        List<Account> friendsList = new ArrayList<>();

                        // Lấy dữ liệu của tất cả những người được người dùng này theo dõi
                        for (String followingId : following.keySet()) {
                            userRef.child(followingId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot followingUserSnapshot) {
                                    // Kiểm tra nếu người đó cũng theo dõi lại người dùng hiện tại
                                    if (followingUserSnapshot.child("followings").hasChild(userLogin)) {
                                        Account account = followingUserSnapshot.getValue(Account.class);
                                        account.setUserId(followingUserSnapshot.getKey());
                                        if(query != null){
                                            if (account != null && account.getNameProfile() != null
                                                    && account.getNameProfile().toLowerCase().contains(query)){
                                                friendsList.add(account);
                                            }
                                        }else
                                            friendsList.add(account);
                                    }
                                    if (followingId.equals(id)) {
                                        updateFollowingRecyclerView(friendsList);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle possible errors
                    }
                });
    }
    //Chức năng tải người dùng từ Firebase và cập nhật RecyclerView
    public void loadUsers() {
        userRef.child(userLogin).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                Map<String, Boolean> following =  new HashMap<>();
                Object test =  dataSnapshot.child("followings").getValue();
                if(dataSnapshot.hasChild("followings") && (test instanceof java.util.HashMap)){
                    following =  (Map<String, Boolean>) dataSnapshot.child("followings").getValue() ;
                }
                if (following == null) {
                    following = new HashMap<>();
                }
                String id = following.size() > 0 ? following.keySet().toArray()[following.size() - 1].toString() : "";
                List<Account> friendsList = new ArrayList<>();

                // Lấy dữ liệu của tất cả những người được người dùng này theo dõi
                for (String followingId : following.keySet()) {
                    userRef.child(followingId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot followingUserSnapshot) {
                            // Kiểm tra nếu người đó cũng theo dõi lại người dùng hiện tại
                            if (followingUserSnapshot.child("followings").hasChild(userLogin)) {
                                Account account = followingUserSnapshot.getValue(Account.class);
                                account.setUserId(followingUserSnapshot.getKey());
                                friendsList.add(account);
                            }
                            if (followingId.equals(id)) {
                                updateFollowingRecyclerView(friendsList);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }


    private void updateFollowingRecyclerView(List<Account> followingUsers) {
        // Giả sử bạn đã có RecyclerView và Adapter để hiển thị danh sách
        BanBeAdapter adapter = new BanBeAdapter(getContext(), followingUsers);
        recyclerView.setAdapter(adapter);
    }

}
