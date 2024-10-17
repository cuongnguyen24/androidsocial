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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.utt.tt21.cc_modulelogin.R;
import com.utt.tt21.cc_modulelogin.home.homeAdapter.HomeAdapter;
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
        HomeAdapter adapter = new HomeAdapter(list, getContext());
        recyclerView.setAdapter(adapter);
        loadDataFromFirestore();
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("list_user");
//
//        myRef.setValue(list);
        scrollScreen();

        //Ngan cach cac bai viet
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        //readNicknameDatabase();

    }

    private void readNicknameDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("QuangTest/nickname");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                tv_nickname.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

//        CollectionReference reference = FirebaseFirestore.getInstance().collection("Users")
//                .document(user.getUid());


        String imageAva = "https://firebasestorage.googleapis.com/v0/b/modulelogin-7c245.appspot.com/o/users%2FckILMQoxSthO5sj6E3A8CYUpDT43%2FckILMQoxSthO5sj6E3A8CYUpDT43.jpg?alt=media&token=e93b8a00-21b7-4475-9da8-82418842145f";
        List<String> Imgs = new ArrayList<>();
        Imgs.add("https://firebasestorage.googleapis.com/v0/b/modulelogin-7c245.appspot.com/o/users%2FckILMQoxSthO5sj6E3A8CYUpDT43%2FIdImgStt_1%2Fimage.jpg?alt=media&token=ae7949dc-02fd-4468-bdf8-488593039546");
        Imgs.add("https://firebasestorage.googleapis.com/v0/b/modulelogin-7c245.appspot.com/o/users%2FckILMQoxSthO5sj6E3A8CYUpDT43%2FIdImgStt_1%2Fimage.jpg?alt=media&token=ae7949dc-02fd-4468-bdf8-488593039546");
        Imgs.add("https://firebasestorage.googleapis.com/v0/b/modulelogin-7c245.appspot.com/o/users%2FckILMQoxSthO5sj6E3A8CYUpDT43%2FIdImgStt_1%2Fimage.jpg?alt=media&token=ae7949dc-02fd-4468-bdf8-488593039546");
        Imgs.add("https://firebasestorage.googleapis.com/v0/b/modulelogin-7c245.appspot.com/o/users%2FckILMQoxSthO5sj6E3A8CYUpDT43%2FIdImgStt_1%2Fimage_2.jpg?alt=media&token=0bda6866-df23-43cd-8b5b-48f55b9cdd50");
        list.add(new HomeModel("Dabi", "13/10/2024", imageAva, Imgs , 10, 10, 20, 0, "accc", "123"));
        list.add(new HomeModel("Dabi", "13/10/2024", imageAva, Imgs , 10, 10, 20, 0, "accc", "123"));
        list.add(new HomeModel("Dabi", "13/10/2024", imageAva, Imgs , 10, 10, 20, 0, "accc", "123"));
        list.add(new HomeModel("Dabi", "13/10/2024", imageAva, Imgs , 10, 10, 20, 0, "accc", "123"));
        list.add(new HomeModel("Dabi", "13/10/2024", imageAva, Imgs , 10, 10, 20, 0, "accc", "123"));
        list.add(new HomeModel("Dabi", "13/10/2024", imageAva, Imgs , 10, 10, 20, 0, "accc", "123"));



        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference getList = database.getReference("list_status").child(mUser.getUid());
        getList.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                HomeModel model = snapshot.getValue(HomeModel.class);
                if(user != null)
                {
                    list.add(model);
                    if(user != null)
                    {
                        //adapter.notifyDataSetChanged();
                    }
                }

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
    }
}