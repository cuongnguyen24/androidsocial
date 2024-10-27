package com.utt.tt21.cc_modulelogin.profile.guestProfile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class SectionsGuestPagerAdapter extends FragmentPagerAdapter {
    private String userId; // Thêm biến để lưu UID

    public SectionsGuestPagerAdapter(@NonNull FragmentManager fm, String userId) {
        super(fm);
        this.userId = userId; // Gán giá trị UID
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ThreadGuestFragment threadGuestFragment = new ThreadGuestFragment();
                Bundle bundle = new Bundle();
                bundle.putString("uid", userId); // Truyền UID qua Bundle
                threadGuestFragment.setArguments(bundle);
                return threadGuestFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 1; // Số lượng tab
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Thread";
            default:
                return null;
        }
    }
}

