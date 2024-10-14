package com.utt.tt21.cc_modulelogin.addStatus;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utt.tt21.cc_modulelogin.R;

public class AddStatusActivity extends AppCompatActivity {
    private TextView tvNickname;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add);  // Đổi thành layout XML của bạn

        // Ánh xạ TextView
        tvNickname = findViewById(R.id.tv_nickname);

        // Tham chiếu đến Firebase Realtime Database

        readDatabase();

    }
    private void readDatabase(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid(); // Lấy UID của người dùng
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("list_user").child(userId);
//        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//        DatabaseReference Myref = firebaseDatabase.getReference("QuangTest/nickname");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String value = snapshot.child("nameProfile").getValue(String.class);
                    Log.d("FirebaseData", "Nickname: " + value);  // Log dữ liệu nhận được
                    tvNickname.setText(value);
                } else {
                    Log.d("FirebaseData", "No data found at QuangTest/nickname");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error: " + error.getMessage());  // Log lỗi nếu có
            }

        });
    }
}

