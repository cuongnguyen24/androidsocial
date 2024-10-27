package com.utt.tt21.cc_modulelogin.messenger.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utt.tt21.cc_modulelogin.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private TextView nameProfile;
    private CircleImageView profileImage;
    private ImageButton btnBack, btnMore;
    private EditText edtMessage;
    private ImageButton btnSend;
    public String ChatRoom;
    public FirebaseUser user;
    private ChatRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    public List<ChatMessageModel> chatList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        initId();

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String imageUrl = intent.getStringExtra("imageUrl");
        String uid = intent.getStringExtra("uid");
        user = FirebaseAuth.getInstance().getCurrentUser();
        ChatRoom = getChatRoomId(user.getUid(), uid);
        nameProfile.setText(name);
        //nameProfile.setText(name);

        Glide.with(this)
                .load(imageUrl)
                .placeholder(null)  // Ảnh hiển thị trong khi chờ tải
                .error(R.drawable.profile)       // Ảnh hiển thị khi có lỗi
                .into(profileImage);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = edtMessage.getText().toString().trim();
                if (message.isEmpty()) {
                    return;
                }
                sendMessageToUser(message);
            }
        });

        CreateRoomChat(uid);
        loadData();
//

    }

    private void loadData() {
        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ChatData").child("ChatRoom").child(ChatRoom).child("chats");




//        adapter = new ChatRecyclerAdapter(chatList, this);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(adapter);
    }

    private void setupChatRecyclerView() {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ChatData").child("ChatRoom").child(ChatRoom).child("chats");
//        reference.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                chatList = new ArrayList<>();
//                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                    ChatMessageModel chatMessageModel = postSnapshot.getValue(ChatMessageModel.class);
//                    chatList.add(chatMessageModel);
//                }
//
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//



   }

    private void sendMessageToUser(String message) {
         // Lấy user hiện tại từ Firebase Auth
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("ChatData").child("ChatRoom").child(ChatRoom).child("chats");
        String randomID = reference.push().getKey();
        reference.child(randomID).child("message").setValue(message);
        reference.child(randomID).child("senderId").setValue(user.getUid());
        reference.child(randomID).child("timestamp").setValue(Timestamp.now().toDate().toString());
        edtMessage.setText("");
    }

    private void CreateRoomChat(String uid) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // Lấy user hiện tại từ Firebase Auth
        if (user == null) {
            Log.e("PushData", "User is not logged in.");
            return; // Nếu không có người dùng nào đang đăng nhập, không thực hiện hành động
        }
        String userid = user.getUid(); // Lấy userId
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("ChatData").child("ChatRoom");

        reference.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String RoomId = getChatRoomId(userid, uid);
                reference.child(RoomId);
                DatabaseReference reference1 = database.getReference("ChatData").child("ChatRoom").child(RoomId);
                reference1.child("lastMesseageSenderId").setValue(uid);
                reference1.child("lassMessageTime").setValue(Timestamp.now().toDate().toString());
                DatabaseReference reference2 = database.getReference("ChatData").child("ChatRoom").child(RoomId);
                reference2.child("UserIds").child(userid).setValue("");
                reference2.child("UserIds").child(uid).setValue("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu cần
                Log.e("PushData", "Database error: " + error.getMessage());
            }
        });
    }

    String getChatRoomId(String userId1, String userId2) {
        if(userId1.hashCode() < userId2.hashCode()) {
            return userId1 +"_"+ userId2;
        }
        else {
            return userId2 +"_"+ userId1;
        }
    }


    private void initId() {
        nameProfile = findViewById(R.id.nameProfile);
        profileImage = findViewById(R.id.profileImage);
        btnBack = findViewById(R.id.btnBack);
        btnMore = findViewById(R.id.btnMore);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        recyclerView = findViewById(R.id.recyclerView);
    }
}