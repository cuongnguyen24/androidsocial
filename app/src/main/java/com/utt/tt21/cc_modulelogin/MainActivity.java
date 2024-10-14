package com.utt.tt21.cc_modulelogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.utt.tt21.cc_modulelogin.authentication.SignInActivity;

public class MainActivity extends AppCompatActivity {

<<<<<<< Updated upstream
    private Button btnLogout;
=======

    private TableLayout tableLayout;
    private ViewPager2 viewPager2;

    private BottomNavigationView bottomNavigationView;

>>>>>>> Stashed changes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);



        // Khởi tạo nút đăng xuất
        btnLogout = findViewById(R.id.btn_logout);

        // Sự kiện OnClickListener cho nút đăng xuất
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut(); // Đăng xuất khỏi Firebase
                Intent intent = new Intent(MainActivity.this, SignInActivity.class); // Chuyển về màn hình đăng ký
                startActivity(intent);
                finishAffinity(); // Đóng tất cả các activity trước đó
            }
        });
    }
}
