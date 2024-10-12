package com.utt.tt21.cc_modulelogin;

import android.content.Intent;
import android.view.View;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.utt.tt21.cc_modulelogin.authentication.SignInActivity;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.utt.tt21.cc_modulelogin.adapter.ViewPager2Adapter;

public class MainActivity extends AppCompatActivity {


    private TableLayout tableLayout;
    private ViewPager2 viewPager2;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Thiết lập các insets cho cửa sổ
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo nút đăng xuất
        //btnLogout = findViewById(R.id.btn_logout);

        // Sự kiện OnClickListener cho nút đăng xuất
//        btnLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut(); // Đăng xuất khỏi Firebase
//                Intent intent = new Intent(MainActivity.this, SignInActivity.class); // Chuyển về màn hình đăng ký
//                startActivity(intent);
//                finishAffinity(); // Đóng tất cả các activity trước đó
//            }
//        });

        initId();
        ViewPager2Adapter adapter = new ViewPager2Adapter(this);
        viewPager2.setAdapter(adapter);
        ChangePage();
        viewPager2.setOffscreenPageLimit(1);

    }

    private void ChangePage() {
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position)
                {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.action_home).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.action_search).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.action_add).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.action_notification).setChecked(true);
                        break;
                    case 4:
                        bottomNavigationView.getMenu().findItem(R.id.action_profile).setChecked(true);
                        break;

                }
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.action_home)
                {
                    viewPager2.setCurrentItem(0);
                } else if (item.getItemId() == R.id.action_search) {
                    viewPager2.setCurrentItem(1);
                }
                else if (item.getItemId() == R.id.action_add) {
                    viewPager2.setCurrentItem(2);
                }
                else if (item.getItemId() == R.id.action_notification)
                {
                    viewPager2.setCurrentItem(3);
                }
                else if (item.getItemId() == R.id.action_profile)
                {
                    viewPager2.setCurrentItem(4);
                }

                return true;
            }
        });
    }


    private void initId() {
        viewPager2 = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

    }
}
