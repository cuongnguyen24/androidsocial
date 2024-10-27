package com.utt.tt21.cc_modulelogin.adapter;

import android.app.Notification;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.utt.tt21.cc_modulelogin.home.Home;

import com.utt.tt21.cc_modulelogin.messenger.Messenger;
import com.utt.tt21.cc_modulelogin.profile.Profile;
import com.utt.tt21.cc_modulelogin.search.Search;

public class ViewPager2Adapter extends FragmentStateAdapter {


    public ViewPager2Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new Home();
            case 1:
                return new Search();
            case 2:
                return new Messenger();

            case 3:
                return new Profile();
            default:
                return new Home();

        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
