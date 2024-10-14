package com.utt.tt21.cc_modulelogin.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.utt.tt21.cc_modulelogin.R;
import com.utt.tt21.cc_modulelogin.home.homeAdapter.HomeAdapter;
import com.utt.tt21.cc_modulelogin.home.homeModel.HomeModel;

import java.util.ArrayList;
import java.util.List;


public class Home extends Fragment {

    private RecyclerView recyclerView;
    private List<HomeModel> list;
    private FirebaseAuth user;

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

//       reference = FirebaseFirestore.getInstance().collection("Post").document(user.getUid());
//
        list = new ArrayList<>();
        HomeAdapter adapter = new HomeAdapter(list, getContext());
        recyclerView.setAdapter(adapter);
        loadDataFromFirestore();

    }

    private void loadDataFromFirestore() {
        list.add(new HomeModel("Dabi", "13/10/2024", "", "", 10, 0, 20, 0, "abc", "1"));
        list.add(new HomeModel("Dabi", "13/10/2024", "", "", 10, 0, 20, 0, "abc", "1"));
        list.add(new HomeModel("Dabi", "13/10/2024", "", "", 10, 0, 20, 0, "abc", "1"));
        list.add(new HomeModel("Dabi", "13/10/2024", "", "", 10, 0, 20, 0, "abc", "1"));

    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


    }
}