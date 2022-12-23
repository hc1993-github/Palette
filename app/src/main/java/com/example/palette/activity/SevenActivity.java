package com.example.palette.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.palette.R;
import com.example.palette.util.ScreenUtil;
import com.example.palette.view.DiscView;
import com.example.palette.view.RippleView;

public class SevenActivity extends AppCompatActivity {
    private String[] texts = new String[]{"你好","你好","你好","你好","你好","你好"};
    private int[] imgs = new int[]{R.drawable.bg_earth,R.drawable.bg_earth,R.drawable.bg_earth,R.drawable.bg_earth,R.drawable.bg_earth,R.drawable.bg_earth};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seven);
        DiscView discView = findViewById(R.id.dv);
        discView.setItemLayoutId(R.layout.item_menu);
        discView.setItems(imgs,texts);
    }
}