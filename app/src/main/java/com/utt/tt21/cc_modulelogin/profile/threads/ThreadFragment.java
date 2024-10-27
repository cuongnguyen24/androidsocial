package com.utt.tt21.cc_modulelogin.profile.threads;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.utt.tt21.cc_modulelogin.R;
import com.utt.tt21.cc_modulelogin.home.homeAdapter.HomeAdapter;
import com.utt.tt21.cc_modulelogin.home.homeAdapter.ImageStringAdapter;
import com.utt.tt21.cc_modulelogin.home.homeModel.HomeModel;

import java.util.ArrayList;
import java.util.List;

public class ThreadFragment extends Fragment {
    private RecyclerView recyclerView ;
    private List<HomeModel> list;
    private List<String> imagePost;
    private FirebaseAuth user;
    private BottomNavigationView bottomNavigationView;
    private Context context;
    private ImageView imvAvatar;
    private TextView tv_nickname;
    private TextView timestamp;
    private FirebaseUser mUser;
    DocumentReference reference;
    private ThreadFragmentAdapter adapter;
    private ImageStringAdapter imageStringAdapter;
    private SwipeRefreshLayout refreshLayout;
    public ThreadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_thread, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        list = new ArrayList<>();
        adapter = new ThreadFragmentAdapter(list, getContext());
        recyclerView.setAdapter(adapter);
        loadDataFromFirestore();
        adapter.notifyDataSetChanged();

        scrollScreen();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataFromFirestore();
                list.clear();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //adapter.notifyDataSetChanged();
    }

    // Keo tha navigation_bar
    private void scrollScreen() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    bottomNavigationView.animate().translationY(bottomNavigationView.getHeight()).setDuration(300);
                } else if (dy < 0) {
                    bottomNavigationView.animate().translationY(0).setDuration(300);
                }
            }
        });
    }

    private void loadDataFromFirestore() {
        Toast.makeText(context, mUser.getEmail(), Toast.LENGTH_SHORT).show();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference getMyStatus = database.getReference("list_status").child(mUser.getUid());

        getMyStatus.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshotStatus, @Nullable String previousChildName) {
                        HomeModel homeModelList = new HomeModel();
                        homeModelList.setContent(snapshotStatus.child("content").getValue(String.class));
                        // Sửa lại reaction
                        homeModelList.setCmtCount(0);
                        homeModelList.setLikeCount(0);
                        homeModelList.setPostCount(0);
                        homeModelList.setReupCount(0);
                        //Lay anh cho profile trong storage
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        String imagePath = "users/" + mUser.getUid() + "/"+mUser.getUid()+".jpg";
                        Log.d("FirebaseStorage", "URL ảnh: " + imagePath);
                        String imageUrl = "";
                        StorageReference imageRef = storage.getReference().child(imagePath);
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Đây là URL download của ảnh
                                String imageUrl = uri.toString();
                                homeModelList.setProfileImage(imageUrl);
                                Log.d("FirebaseStorage", "URL ảnh: " + imageUrl);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Xử lý khi có lỗi xảy ra
                                Log.e("FirebaseStorage", "Lỗi khi lấy URL: " + exception.getMessage());
                            }
                        });

                        List<String> imageLists = new ArrayList<>();
                        //lấy tất cả list photo trong storage
                        FirebaseStorage storage1 = FirebaseStorage.getInstance();
                        // Số thứ tự bài viết


                        String folderPath = "users/" + mUser.getUid() + "/IdImgStt_" + snapshotStatus.child("uid").getValue(Integer.class);
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

                        FirebaseDatabase databaseGetName = FirebaseDatabase.getInstance();
                        DatabaseReference referenceGetName = databaseGetName.getReference("users").child(mUser.getUid()).child("nameProfile");
                        homeModelList.setUserName("Anonymous");
                        referenceGetName.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshotGetName) {
                                homeModelList.setUserName(snapshotGetName.getValue(String.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
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
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        //adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        //adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        //adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //adapter.notifyDataSetChanged();
                    }
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
