package com.utt.tt21.cc_modulelogin.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utt.tt21.cc_modulelogin.R;

import java.util.ArrayList;
import java.util.List;

public class Search extends Fragment {

    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("TestSearch");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false); // Thay đổi với layout của bạn
        fetchUserData(view); // Gọi hàm lấy dữ liệu
        return view;
    }

    private void fetchUserData(View view) {
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Account> userList = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userId = userSnapshot.getKey(); // Lấy user ID
                    Account account = userSnapshot.getValue(Account.class);
                    if (account != null) {
                        account.setUserId(userId); // Cập nhật userId vào đối tượng Account
                        userList.add(account);
                    }
                }
                setupRecyclerView(userList, view); // Truyền view vào
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    private void setupRecyclerView(List<Account> userList, View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        ListAccountAdapter adapter = new ListAccountAdapter(userList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
}
