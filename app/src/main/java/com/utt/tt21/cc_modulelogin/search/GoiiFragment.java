package com.utt.tt21.cc_modulelogin.search;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class GoiiFragment extends Fragment {
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    List<Account> userList ;
    ListAccountAdapter adapter;
   // private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follower, container, false); // Thay đổi với layout của bạn

        // khởi tạo recyclerView và UserAdapter
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // tạo dòng kẻ phân cách item
        DividerItemDecoration dividerItemDecoration= new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        // (Tùy chọn) Bạn có thể tùy chỉnh divider với một drawable
        Drawable dividerDrawable = ContextCompat.getDrawable(getContext(), R.drawable.custom_divider);
        dividerItemDecoration.setDrawable(dividerDrawable);
        recyclerView.addItemDecoration(dividerItemDecoration);

        userList = new ArrayList<>();
        adapter = new ListAccountAdapter(getContext(), userList, new ListAccountAdapter.IClickListener() {
            @Override
            public void onclickUpdate(Account account) {

            }

            @Override
            public void onCLickDelete(Account account) {

            }
        },3);
        recyclerView.setAdapter(adapter);

        // Initialize Firebase reference and authentication
        userRef = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        // Load users from Firebase
        loadUsers();
        return view;
    }
    // Function to load users from Firebase and update the RecyclerView
    private void loadUsers() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userId = snapshot.getKey(); // Lấy user ID
                    Account account = snapshot.getValue(Account.class);
                    String userLogin = mAuth.getCurrentUser().getUid();
                    account.setUserId(userId);
                    if (account != null && (account.getUserId() != null &&!account.getUserId().equals(userLogin))) {
                       // Cập nhật userId vào đối tượng Account
                        userList.add(account);
                    }
                }
               adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

//    private void fetchUserData(View view) {
//        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                List<Account> userList = new ArrayList<>();
//                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
//                    String userId = userSnapshot.getKey(); // Lấy user ID
//                    Account account = userSnapshot.getValue(Account.class);
//                    if (account != null) {
//                        account.setUserId(userId); // Cập nhật userId vào đối tượng Account
//                        userList.add(account);
//                    }
//                }
//                setupRecyclerView(userList, view); // Truyền view vào
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Xử lý lỗi nếu cần
//            }
//        });
//    }
//    private void setupRecyclerView(List<Account> userList, View view) {
//        // khởi tạo recyclerView và UserAdapter
//        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        ListAccountAdapter adapter = new ListAccountAdapter(this,userList);
//
//        recyclerView.setAdapter(adapter);
//    }
}
