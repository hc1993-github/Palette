package com.example.palette.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.palette.R;
import com.example.palette.adapter.ViewPagerAdapter;
import com.example.palette.fragment.RecyclerViewFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class ElevenActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_eleven);
        setContentView(R.layout.activity_eleven2);

        viewPager = findViewById(R.id.vp);
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(this,getf());
        viewPager.setAdapter(pagerAdapter);
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