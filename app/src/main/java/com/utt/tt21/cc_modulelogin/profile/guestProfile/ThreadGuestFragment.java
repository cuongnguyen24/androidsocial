package com.utt.tt21.cc_modulelogin.profile.guestProfile;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.utt.tt21.cc_modulelogin.R;
import com.utt.tt21.cc_modulelogin.home.homeAdapter.HomeAdapter;
import com.utt.tt21.cc_modulelogin.home.homeModel.HomeModel;
import com.utt.tt21.cc_modulelogin.profile.threads.ThreadFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

public class ThreadGuestFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<HomeModel> list;
    private FirebaseAuth user;
    private BottomNavigationView bottomNavigationView;
    private Context context;
    private ImageView imvAvatar;
    private TextView tv_nickname;
    private TextView timestamp;
    private FirebaseUser mUser;
    private ThreadFragmentAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private String userId; // Biến để lưu UID của người dùng
    public ThreadGuestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_guest_thread, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        // Nhận UID từ Bundle
        if (getArguments() != null) {
            userId = getArguments().getString("uid");
        }

        list = new ArrayList<>();
        adapter = new ThreadFragmentAdapter(list, getContext());
        recyclerView.setAdapter(adapter);

        if (userId != null) {
            loadDataFromFirestore(userId); // Gọi phương thức với UID đã nhận
        } else {
            Toast.makeText(context, "UID không hợp lệ", Toast.LENGTH_SHORT).show();
        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataFromFirestore(userId);
                list.clear();
            }
        });
    }

    private void loadDataFromFirestore(String uid) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference getMyStatus = database.getReference("list_status").child(uid); // Sử dụng UID đã nhận

        getMyStatus.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshotStatus, @Nullable String previousChildName) {
                HomeModel homeModelList = new HomeModel();
                homeModelList.setContent(snapshotStatus.child("content").getValue(String.class));
                homeModelList.setCmtCount(0);
                homeModelList.setLikeCount(0);
                homeModelList.setPostCount(0);
                homeModelList.setReupCount(0);

                // Lấy tên người dùng
                DatabaseReference userRef = database.getReference("users").child(uid).child("nameProfile");
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userName = snapshot.getValue(String.class);
                        homeModelList.setUserName(userName != null ? userName : "Anonymous");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // xử lý ngoai lệ
                    }
                });

                // Lấy ảnh cho profile trong storage
                FirebaseStorage storage = FirebaseStorage.getInstance();
                String imagePath = "users/" + uid + "/" + uid + ".jpg"; // Sử dụng UID đã nhận
                StorageReference imageRef = storage.getReference().child(imagePath);
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        homeModelList.setProfileImage(uri.toString());
                    }
                });

                // Lấy danh sách ảnh
                List<String> imageLists = new ArrayList<>();
                String folderPath = "users/" + uid + "/IdImgStt_" + snapshotStatus.child("uid").getValue(Integer.class);
                StorageReference listRef = storage.getReference().child(folderPath);
                listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            String fileName = item.getName();
                            if (fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
                                // Chỉ lấy URL download cho các tệp hình ảnh
                                item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();
                                        imageLists.add(imageUrl); // Thêm URL của ảnh vào danh sách
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Log.e("FirebaseStorageImage", "Lỗi khi lấy URL: " + exception.getMessage());
                                    }
                                });
                            } else {
                                Log.d("FirebaseStorageImage", "Bỏ qua tệp không phải ảnh: " + fileName);
                            }
                        }
                    }
                });

                homeModelList.setPostImage(imageLists);
                homeModelList.setTimestamp(snapshotStatus.child("timestamp").getValue(String.class));
                list.add(homeModelList);
                adapter.notifyDataSetChanged();
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    adapter.notifyDataSetChanged(); // Cập nhật dữ liệu sau 5 giây
                }, 1000);
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    private void init(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        context = getContext();
        imvAvatar = view.findViewById(R.id.detailProfileImage);
        tv_nickname = view.findViewById(R.id.tvName);
        timestamp = view.findViewById(R.id.tvTimeStamp);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();
        refreshLayout = view.findViewById(R.id.swipeRefreshLayout);

    }
}
