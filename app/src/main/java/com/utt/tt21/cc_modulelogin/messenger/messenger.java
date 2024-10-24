package com.utt.tt21.cc_modulelogin.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.utt.tt21.cc_modulelogin.MainActivity;
import com.utt.tt21.cc_modulelogin.R;
import com.utt.tt21.cc_modulelogin.fragment.Notification;
import com.utt.tt21.cc_modulelogin.home.Home;
import com.utt.tt21.cc_modulelogin.search.Search;

import java.util.ArrayList;
import java.util.List;

public class messenger extends AppCompatActivity {
    private ImageButton btn_back;
    private GestureDetector gestureDetector;
    private RecyclerView recyclerView;
    private MessengerUserAdapter adapter;
    private List<MessengerUserModel> list ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_messenger);
        initId();
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(messenger.this, MainActivity.class);
                startActivity(intent);
            }
        });
        gestureDetector = new GestureDetector(this, new GestureListener());


        list = new ArrayList<>();
        adapter = new MessengerUserAdapter(list, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        fetchUserData();

    }
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight();
                    }
                }
            }
            return true;
        }
    }

    private void onSwipeRight() {
        Intent intent = new Intent(messenger.this, MainActivity.class);
        startActivity(intent);
    }
    private void initId() {
        btn_back = findViewById(R.id.btn_back);
        recyclerView = findViewById(R.id.recyclerViewMessenger);
    }
    void fetchUserData() {

    }
}
