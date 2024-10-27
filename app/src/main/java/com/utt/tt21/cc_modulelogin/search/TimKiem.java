package com.utt.tt21.cc_modulelogin.search;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
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

public class TimKiem extends AppCompatActivity {
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    List<Account> userList ;
    TKAdapter adapter;
    RecyclerView rcvView;
    ImageButton btnBack;
    EditText edtTimKiem;
    String userLogin;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_tkiemfriend);
        super.onCreate(savedInstanceState);
        // khởi tạo recyclerView và UserAdapter
        rcvView = findViewById(R.id.rcvView);
        edtTimKiem = findViewById(R.id.edtTimKiem );
        rcvView.setLayoutManager(new LinearLayoutManager(this));

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        userList = new ArrayList<>();
        adapter = new TKAdapter(TimKiem.this, userList, new TKAdapter.IClickListener() {
            @Override
            public void onclickUpdate(List<Account> account) {
                userList = account;
                adapter.notifyDataSetChanged();
            }
        }) ;
        rcvView.setAdapter(adapter);
        // Initialize Firebase reference and authentication
        userRef = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();
        userLogin = mAuth.getCurrentUser().getUid();
        // Lắng nghe sự thay đổi trong EditText để thực hiện tìm kiếm khi người dùng nhập.
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
        // Load users from Firebase
        loadUsers();
    }
    private void searchUsers(String query) {
        userRef.orderByChild("nameProfile")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Account account = snapshot.getValue(Account.class);
                            boolean hasFollowers = snapshot.hasChild("followers") && snapshot.child("followers").hasChild(userLogin);
                            String userLogin = mAuth.getCurrentUser().getUid();
                            account.setUserId(snapshot.getKey());
                            if (account != null &&!(account.getUserId().equals(userLogin)) && !hasFollowers && account.getNameProfile() != null
                                    && account.getNameProfile().toLowerCase().contains(query)){
                                userList.add(account);
                            }

                        }
                        adapter.notifyDataSetChanged();
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
                 for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userId = snapshot.getKey(); // Lấy user ID
                    Account account = snapshot.getValue(Account.class);
                    boolean hasFollowers = snapshot.hasChild("followers") && snapshot.child("followers").hasChild(userLogin);
                    //boolean hasFollowing = snapshot.hasChild("followings") && snapshot.child("followings").child(userLogin).getChildrenCount() > 0;

                    String userLogin = mAuth.getCurrentUser().getUid();
                    account.setUserId(userId);
                    if (account != null  && !hasFollowers  &&!(account.getUserId().equals(userLogin))) {
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
}
