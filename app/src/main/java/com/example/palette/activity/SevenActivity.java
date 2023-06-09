package com.example.palette.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;


import com.example.palette.R;
import com.example.palette.adapter.TestAdapter;
import com.example.palette.banner.BannerLayout;
import com.example.palette.bean.TextJson;
import com.example.palette.encrypt.SM4Util;
import com.example.palette.layoutmanager.card.CardLayoutManager;
import com.example.palette.layoutmanager.card.ItemTouchHelperCallback;
import com.example.palette.layoutmanager.card.OnSwiperListener;
import com.example.palette.module.ProgressListener;
import com.example.palette.util.BitmapUtil;
import com.example.palette.util.OkHttpUtil;
import com.example.palette.util.RSA;
import com.example.palette.view.RotateView;
import com.example.palette.view.ScrollTextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SevenActivity extends AppCompatActivity {
    private String[] texts = new String[]{"你好","你好","你好","你好","你好","你好"};
    private int[] imgs = new int[]{R.drawable.bg_earth,R.drawable.bg_earth,R.drawable.bg_earth,R.drawable.bg_earth,R.drawable.bg_earth,R.drawable.bg_earth};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seven);
        ImageView imageView = findViewById(R.id.iv);
        imageView.setImageBitmap(BitmapUtil.createQRCode("b83e13be7fdd9ae675156423e5c424cb34210cd6cac77b8ac69b339ecc272659b5fa9e8d949dd2ba56334fd4ba4fffbf27cda1de5281e5894799a64adc20df149f72700b9bbbe3e85d02b48b70fa2025", 500, 500, null));
        String decrypt1 = RSA.decryptByPublicKey(Base64.decode("LIjsezWKzDtmvTyE9HURNVoT6c4JCzybxqMAx4/cefAf7KcZLINobfuy7CZI8WheP2npZj170GlpU+6irwFQSeQIFPIPAbte5eBb3x4pr/8KmHJroUqjti4krgKO8foCIQe5Xe0L9zFy/dfB7JMtoDOUelF+sbggVy6A7+yWAAs=", Base64.NO_WRAP), "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCYcIwl3VINdmhuvclyqmlzIX6iJNXri4ssZxEe9lcL6G9N66aqlv2i9aNnS7GJIQg+YiDVVxrBQ7389wYLg/wc6BfPDQPZGC5aiPw4CVCHZUpjI9wtSpNKR5A3DjlKLKZl/7UR3LYJcLLJOEi1hPYX+Tb8IJ5M44WN5cts5nu4bQIDAQAB");
        String decrypt2 = RSA.decryptByPublicKey(Base64.decode("CyR/1VEJAPFgH+gpdbxlmAbdzyL3ByeX5hm5eNuzVCAGWIQvAYFSAZ0waXbWjIbugC6rGmHMnfVMdP22PaiyntRjn25dcjXnzlY9F9RbUWxcPMziLfbn8yL7scd5LbWzaEw1EbCcLWCjUpy7msuc909xMitsP6iFQtGjUpvP/U0=", Base64.NO_WRAP), "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCYcIwl3VINdmhuvclyqmlzIX6iJNXri4ssZxEe9lcL6G9N66aqlv2i9aNnS7GJIQg+YiDVVxrBQ7389wYLg/wc6BfPDQPZGC5aiPw4CVCHZUpjI9wtSpNKR5A3DjlKLKZl/7UR3LYJcLLJOEi1hPYX+Tb8IJ5M44WN5cts5nu4bQIDAQAB");
        String decrypt3 = SM4Util.decryptString("LIjsezWKzDtmvTyE9HURNVoT6c4JCzybxqMAx4/cefAf7KcZLINobfuy7CZI8WheP2npZj170GlpU+6irwFQSeQIFPIPAbte5eBb3x4pr/8KmHJroUqjti4krgKO8foCIQe5Xe0L9zFy/dfB7JMtoDOUelF+sbggVy6A7+yWAAs=", "87bdb5e4685c0a2532dee8991b7dc7b2");
        String decrypt4 = SM4Util.decryptString("CyR/1VEJAPFgH+gpdbxlmAbdzyL3ByeX5hm5eNuzVCAGWIQvAYFSAZ0waXbWjIbugC6rGmHMnfVMdP22PaiyntRjn25dcjXnzlY9F9RbUWxcPMziLfbn8yL7scd5LbWzaEw1EbCcLWCjUpy7msuc909xMitsP6iFQtGjUpvP/U0=", "87bdb5e4685c0a2532dee8991b7dc7b2");

        Log.d("SevenActivity---","decrypt1:"+decrypt1);
        Log.d("SevenActivity---","decrypt2:"+decrypt2);
        Log.d("SevenActivity---","decrypt3:"+decrypt3);
        Log.d("SevenActivity---","decrypt4:"+decrypt4);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpUtil.getInstance().requestDownLoadWithParams("http://58.210.20.242:28080/apk/JieZhongAPPv2.5.9.1_43_release.apk", true, null, null, new File(Environment.getExternalStorageDirectory(), "test.apk"), new ProgressListener() {
                    @Override
                    public void onProgress(int progress) {
                        Log.i("SevenActivity", "onProgress: "+progress);
                    }
                }, new OkHttpUtil.ResultCallback() {
                    @Override
                    public void onNetError(String message) {
                        Log.i("SevenActivity", "onNetError: "+message);
                    }

                    @Override
                    public void onFailResponse(String info) {
                        Log.i("onFailResponse", "onFailResponse: "+info);
                    }

                    @Override
                    public void onSuccessResponse(String info) throws IOException {
                        Log.i("onSuccessResponse", "onSuccessResponse: "+info);
                    }
                });
            }
        });
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