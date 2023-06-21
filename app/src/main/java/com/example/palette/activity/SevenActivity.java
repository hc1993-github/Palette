package com.example.palette.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.palette.R;
import com.example.palette.module.ProgressListener;
import com.example.palette.util.BitmapUtil;
import com.example.palette.util.OkHttpUtil;
import com.example.palette.util.SecurityUtil;

import java.io.File;
import java.io.IOException;

public class SevenActivity extends AppCompatActivity {
    public static final String TAG = "SevenActivity";
    private String[] texts = new String[]{"你好","你好","你好","你好","你好","你好"};
    private int[] imgs = new int[]{R.drawable.bg_earth,R.drawable.bg_earth,R.drawable.bg_earth,R.drawable.bg_earth,R.drawable.bg_earth,R.drawable.bg_earth};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seven);
        ImageView imageView = findViewById(R.id.iv);
        Bitmap b = BitmapUtil.createQRCode("b83e13be7fdd9ae675156423e5c424cb34210cd6cac77b8ac69b339ecc272659b5fa9e8d949dd2ba56334fd4ba4fffbf27cda1de5281e5894799a64adc20df149f72700b9bbbe3e85d02b48b70fa2025", 500, 500, null);
        imageView.setImageBitmap(BitmapUtil.stringToBitmap(BitmapUtil.bitmapToString(b)));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpUtil.getInstance().requestDownLoadWithParams("http://58.210.20.242:28080/apk/JieZhongAPPv2.5.9.1_43_release.apk", true, null, null, new File(Environment.getExternalStorageDirectory(), "test.apk"), new ProgressListener() {
                    @Override
                    public void onProgress(int progress) {
                        Log.i(TAG, "onProgress: "+progress);
                    }
                }, new OkHttpUtil.ResultCallback() {
                    @Override
                    public void onNetFailure(String message) {
                        Log.i(TAG, "onNetError: "+message);
                    }

                    @Override
                    public void onResponseFailure(String info) {
                        Log.i(TAG, "onFailResponse: "+info);
                    }

                    @Override
                    public void onResponseSuccess(String info) throws IOException {
                        Log.i(TAG, "onSuccessResponse: "+info);
                    }
                });
            }
        });
        Log.i(TAG,BitmapUtil.bitmapToString(b));
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