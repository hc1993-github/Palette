package com.example.palette.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.palette.fragment.RecyclerViewFragment;

import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {

    List<RecyclerViewFragment> fragments;

    public ViewPagerAdapter(FragmentActivity fragmentActivity,List<RecyclerViewFragment> fragments) {
        super(fragmentActivity);
        this.fragments = fragments;
    }


    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

}
