package com.utt.tt21.cc_modulelogin.search;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabAdapter extends FragmentPagerAdapter {
    public TabAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FollowingFragment();
            case 1:
                return new FollowersFragment();
            case 2:
                return new GoiiFragment();
            default:
                return new FollowingFragment();
        }
    }

    @Override
    public int getCount() {
        return 3; // Số lượng tab
    }
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Đang theo dõi";
            case 1:
                return "Người theo dõi";
            case 2:
                return "Gợi í";
            default:
                return null;
        }
    }
}

