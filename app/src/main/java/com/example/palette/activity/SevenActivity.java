package com.example.palette.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;


import com.example.palette.R;
import com.example.palette.adapter.TestAdapter;
import com.example.palette.banner.BannerLayout;
import com.example.palette.bean.TextJson;
import com.example.palette.layoutmanager.card.CardLayoutManager;
import com.example.palette.layoutmanager.card.ItemTouchHelperCallback;
import com.example.palette.layoutmanager.card.OnSwiperListener;
import com.example.palette.view.RotateView;
import com.example.palette.view.ScrollTextView;

import java.util.ArrayList;
import java.util.List;

public class SevenActivity extends AppCompatActivity {
    private String[] texts = new String[]{"你好","你好","你好","你好","你好","你好"};
    private int[] imgs = new int[]{R.drawable.bg_earth,R.drawable.bg_earth,R.drawable.bg_earth,R.drawable.bg_earth,R.drawable.bg_earth,R.drawable.bg_earth};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seven);
//        DiscView discView = findViewById(R.id.dv);
//        discView.setItemLayoutId(R.layout.item_menu);
//        discView.setItems(imgs,texts);

//        BannerLayout bannerLayout = findViewById(R.id.bl);
//        List<String> datas = new ArrayList<>();
//        for (int i = 0; i < 50; i++) {
//            datas.add("测试数据"+i);
//        }
//        TestAdapter adapters = new TestAdapter(this,R.layout.item_banner,datas);
//        bannerLayout.setAutoPlaying(false);
//        bannerLayout.setShowIndicator(false);
//        bannerLayout.setOrientation(OrientationHelper.VERTICAL);
//        bannerLayout.setAdapter(adapters);

//        RecyclerCoverFlowView recyclerView = findViewById(R.id.rv);
//        List<String> data = new ArrayList<>();
//        for (int i = 0; i < 50; i++) {
//            data.add("测试数据"+i);
//        }
//        recyclerView.setLayoutManager(new CoverFlowLayoutManager());
//        TestAdapter adapter = new TestAdapter(this,R.layout.item_banner,data);
//        recyclerView.setAdapter(adapter);

//        RecyclerView recyclerView = findViewById(R.id.rv);
//        List<String> data = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            data.add("测试数据"+i);
//        }
//        TestAdapter adapter = new TestAdapter(this,R.layout.item_banner,data);
//        recyclerView.setAdapter(adapter);
//        ItemTouchHelperCallback callback = new ItemTouchHelperCallback(recyclerView.getAdapter(),data);
//        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
//        CardLayoutManager manager = new CardLayoutManager(recyclerView,touchHelper);
//        recyclerView.setLayoutManager(manager);
//        touchHelper.attachToRecyclerView(recyclerView);
//        callback.setOnSwipeListener(new OnSwiperListener<String>() {
//            @Override
//            public void onSwiping(RecyclerView.ViewHolder viewHolder, float ratio, int direction) {
//
//            }
//
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, String s, int direction) {
//                Log.d("onSwiped", "onSwiped: "+direction);
//            }
//
//            @Override
//            public void onSwipedClear() {
//
//            }
//        });
//        RotateView rotateView = findViewById(R.id.rov);
//        rotateView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(SevenActivity.this,"点击",Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}