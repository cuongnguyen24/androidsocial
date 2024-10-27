package com.utt.tt21.cc_modulelogin.search;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabAdapter extends FragmentStateAdapter {
    private final Fragment[] fragments;
    public TabAdapter(@NonNull FragmentActivity fragmentActivity, Fragment... fragments) {
        super(fragmentActivity);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return  fragments[position];
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

