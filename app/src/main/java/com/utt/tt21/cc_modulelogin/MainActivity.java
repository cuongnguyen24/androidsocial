package com.utt.tt21.cc_modulelogin;

import android.content.Intent;
import android.view.View;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.utt.tt21.cc_modulelogin.authentication.SignInActivity;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.utt.tt21.cc_modulelogin.adapter.ViewPager2Adapter;

public class MainActivity extends AppCompatActivity {


    private Button btnLogout;


    private TableLayout tableLayout;
    private ViewPager2 viewPager2;
    private BottomNavigationView bottomNavigationView;
    private LinearLayout bottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private int idFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        initId();
        ViewPager2Adapter adapter = new ViewPager2Adapter(this);
        viewPager2.setAdapter(adapter);
        ChangePage();
        viewPager2.setOffscreenPageLimit(1);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        bottomNavigationView.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        bottomNavigationView.setVisibility(View.VISIBLE);
                        setCheckedIcon(idFragment);
                        break;
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void setCheckedIcon(int idFragment) {
        switch (idFragment)
        {
            case 0:
                bottomNavigationView.getMenu().findItem(R.id.action_home).setChecked(true);

                break;
            case 1:
                bottomNavigationView.getMenu().findItem(R.id.action_search).setChecked(true);


                break;
            case 3:
                bottomNavigationView.getMenu().findItem(R.id.action_notification).setChecked(true);


                break;
            case 4:
                bottomNavigationView.getMenu().findItem(R.id.action_profile).setChecked(true);


                break;

        }
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
                        idFragment = 0;
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.action_search).setChecked(true);
                        idFragment = 1;

                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.action_notification).setChecked(true);
                        idFragment = 3;

                        break;
                    case 4:
                        bottomNavigationView.getMenu().findItem(R.id.action_profile).setChecked(true);
                        idFragment = 4;

                        break;

                }
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.action_home)
                {
                    viewPager2.setCurrentItem(0, false);
                } else if (item.getItemId() == R.id.action_search) {
                    viewPager2.setCurrentItem(1, false);
                }
                else if (item.getItemId() == R.id.action_add) {
                    if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                    {

                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                    else
                    {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }
                else if (item.getItemId() == R.id.action_notification)
                {
                    viewPager2.setCurrentItem(3, false);
                }
                else if (item.getItemId() == R.id.action_profile)
                {
                    viewPager2.setCurrentItem(4, false);
                }
                return true;
            }
        });
    }


    private void initId() {
        viewPager2 = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
    }
}
