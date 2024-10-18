package com.utt.tt21.cc_modulelogin.profile.threads;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ThreadFragment();
            case 1:
                return new ReplyThreadFragment();
            case 2:
                return new RepostFragment();
            default:
                return null;
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
                return "Thread";
            case 1:
                return "Thread trả lời";
            case 2:
                return "Bài đăng lại";
            default:
                return null;
        }
    }
}

