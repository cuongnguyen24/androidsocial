package com.utt.tt21.cc_modulelogin.home;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.utt.tt21.cc_modulelogin.R;
import com.utt.tt21.cc_modulelogin.home.homeAdapter.HomeAdapter;
import com.utt.tt21.cc_modulelogin.home.homeAdapter.ImageAdapter;
import com.utt.tt21.cc_modulelogin.home.homeAdapter.ImageStringAdapter;
import com.utt.tt21.cc_modulelogin.home.homeModel.HomeModel;
import com.utt.tt21.cc_modulelogin.profile.profileModel.ImageItems;

import java.util.ArrayList;
import java.util.List;


public class Home extends Fragment {

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
    private HomeAdapter adapter;
    private ImageStringAdapter imageStringAdapter;
    private SwipeRefreshLayout refreshLayout;
    public Home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);




        list = new ArrayList<>();
        adapter = new HomeAdapter(list, getContext());
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


        DatabaseReference getListStatus = database.getReference("list_status");

        getListStatus.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshotList, @Nullable String previousChildName) {
                DatabaseReference getList = database.getReference("list_status").child(snapshotList.getKey());

                Log.e("uid", snapshotList.getKey());
                getList.addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        HomeModel homeModelList = new HomeModel();
                        homeModelList.setContent(snapshot.child("content").getValue(String.class));
                        homeModelList.setCmtCount(0);
                        homeModelList.setLikeCount(0);
                        homeModelList.setPostCount(0);
                        homeModelList.setReupCount(0);



                        //Lay anh cho profile trong storage

                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        String imagePath = "users/" + snapshotList.getKey() + "/"+snapshotList.getKey()+".jpg";
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

                                // Bạn có thể dùng URL này để hiển thị ảnh trong ImageView hoặc xử lý khác
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Xử lý khi có lỗi xảy ra
                                Log.e("FirebaseStorage", "Lỗi khi lấy URL: " + exception.getMessage());
                            }
                        });


//
                        List<String> imageLists = new ArrayList<>();


                        //get all list photo in storage

                        FirebaseStorage storage1 = FirebaseStorage.getInstance();
                        // Số thứ tự bài viết
                        String folderPath = "users/" + snapshotList.getKey() + "/IdImgStt_" + snapshot.child("uid").getValue(Integer.class);
                        Log.e("FirebaseStorageIamge", "URL ảnh: " + folderPath);
                        StorageReference listRef1 = storage1.getReference().child(folderPath);
                        listRef1.listAll()
                                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                    @Override
                                    public void onSuccess(ListResult listResult) {
                                        // Duyệt qua tất cả các tệp trong thư mục
                                        for (StorageReference item : listResult.getItems()) {
                                            // Lấy URL download của từng ảnh
                                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    // Đây là URL của ảnh
                                                    String imageUrl = uri.toString();
                                                    imageUrl+=".jpg";
                                                    imageLists.add(imageUrl);
                                                    Log.e("FirebaseStorageImage", "URL ảnh: " + imageUrl);
                                                    // Bạn có thể sử dụng imageUrl để hiển thị ảnh trong ImageView hoặc lưu trữ URL
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    // Xử lý khi có lỗi xảy ra
                                                    Log.e("FirebaseStorageImage", "Lỗi khi lấy URL: " + exception.getMessage());
                                                }
                                            });
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Xử lý khi có lỗi xảy ra
                                        Log.e("FirebaseStorage", "Lỗi khi liệt kê các tệp: " + exception.getMessage());
                                    }
                                });

                        homeModelList.setPostImage(imageLists);


                        FirebaseDatabase databaseGetName = FirebaseDatabase.getInstance();
                        DatabaseReference referenceGetName = databaseGetName.getReference("users").child(snapshotList.getKey()).child("nameProfile");
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


                        homeModelList.setTimestamp(snapshot.child("timestamp").getValue(String.class));


                        list.add(homeModelList);


                        adapter.notifyDataSetChanged();
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

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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