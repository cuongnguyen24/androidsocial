package com.utt.tt21.cc_modulelogin.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.utt.tt21.cc_modulelogin.fragment.Add;
import com.utt.tt21.cc_modulelogin.fragment.Home;
import com.utt.tt21.cc_modulelogin.fragment.Notification;
import com.utt.tt21.cc_modulelogin.accountmanagement.Profile;
import com.utt.tt21.cc_modulelogin.fragment.Search;

public class ViewPager2Adapter extends FragmentStateAdapter {


    public ViewPager2Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){

            case 1:
                return new Search();

            case 2:
                return new Add();

            case 3:
                return new Notification();

            case 4:
                return new Profile();

            default:
                return new Home();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
