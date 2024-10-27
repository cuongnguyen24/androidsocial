package com.utt.tt21.cc_modulelogin.messenger;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utt.tt21.cc_modulelogin.R;

import java.util.ArrayList;
import java.util.List;


public class Messenger extends Fragment {
    private ImageButton btn_back;
    private RecyclerView recyclerView;
    private MessengerUserAdapter adapter;
    private List<MessengerUserModel> list ;
    private EditText search;
    private TextView tvName;
    private FirebaseUser mUser;

    public Messenger() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_messenger, container, false);
        // Khởi tạo views

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initId(view);




        list = new ArrayList<>();
        loadData();
        adapter = new MessengerUserAdapter(list, this.getActivity());
        Log.e("listMessenger", list.toString());
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setAdapter(adapter);

        search.setOnEditorActionListener((textView, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    actionId == EditorInfo.IME_ACTION_SEND ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                // Xử lý sự kiện nhấn Enter
                String inputText = search.getText().toString();
                if(inputText.isEmpty())
                {
                    loadAllData();
                }
                else {
                    loadDataSearch(inputText);
                }
                return true;
            }
            return false;
        });

        getMyUserName();
    }

    private void loadAllData() {
        List<MessengerUserModel> listAll = new ArrayList<>();
        listAll.clear();
        listAll = list;
        adapter = new MessengerUserAdapter(listAll, this.getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private void getMyUserName() {
        FirebaseDatabase databaseGetName = FirebaseDatabase.getInstance();
        DatabaseReference referenceGetName = databaseGetName.getReference("users").child(mUser.getUid()).child("nameProfile");
        tvName.setText("Anonymous");
        referenceGetName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotGetName) {
                tvName.setText(snapshotGetName.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void loadDataSearch(String inputText) {
        List<MessengerUserModel> listSearch = new ArrayList<>();
        listSearch.clear();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().toLowerCase().contains(inputText.toLowerCase())) {
                listSearch.add(list.get(i));
            }
        }
        adapter = new MessengerUserAdapter(listSearch, this.getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setAdapter(adapter);

    }

    private void loadData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();


        list.add(new MessengerUserModel("1", "2", "Alice", "", "See you later!", "10:30 AM"));
        list.add(new MessengerUserModel("1", "2","alibu", "", "See you later!", "10:30 AM"));
        list.add(new MessengerUserModel("1", "2", "Binh", "", "See you later!", "10:30 AM"));
        list.add(new MessengerUserModel("1", "2", "Nam", "", "See you later!", "10:30 AM"));
        list.add(new MessengerUserModel("1", "2", "Khanh", "", "See you later!", "10:30 AM"));
        list.add(new MessengerUserModel("1", "2", "Quang", "", "See you later!", "10:30 AM"));
    }
    private void initId(View view) {
        btn_back = view.findViewById(R.id.btn_back);
        recyclerView = view.findViewById(R.id.recyclerViewMessenger);
        search = view.findViewById(R.id.ed_search);
        tvName = view.findViewById(R.id.tv_name);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();
    }
}