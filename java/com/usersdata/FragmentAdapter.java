package com.usersdata;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class FragmentAdapter  extends FragmentPagerAdapter {
    private Context context;
    int totalTabs;

    public FragmentAdapter(Context mcontext, @NonNull FragmentManager fm,int totalTabs) {
        super(fm);
        context = mcontext;
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                UserFragment userFragment = new UserFragment();
                return userFragment;
            case 1:
                EnrollFragment enrollFragment = new EnrollFragment();
                return enrollFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
