package com.example.palette.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;

import com.example.palette.R;
import com.example.palette.adapter.ViewPagerAdapter;
import com.example.palette.fragment.RecyclerViewFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class NineActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private String[] titles = new String[]{"个性推荐","歌单","主播电台","排行榜"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nine);

        viewPager = findViewById(R.id.vp);
        tabLayout = findViewById(R.id.tl);
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(this,getf());
        View view = viewPager.getChildAt(0);
        view.setOverScrollMode(View.OVER_SCROLL_NEVER);
        viewPager.setAdapter(pagerAdapter);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(TabLayout.Tab tab, int position) {
                tab.setText(titles[position]);
            }
        });
        tabLayoutMediator.attach();
    }

    private List<RecyclerViewFragment> getf(){
        List<RecyclerViewFragment> list = new ArrayList<>();
        list.add(new RecyclerViewFragment());
        list.add(new RecyclerViewFragment());
        list.add(new RecyclerViewFragment());
        list.add(new RecyclerViewFragment());
        return list;
    }

}