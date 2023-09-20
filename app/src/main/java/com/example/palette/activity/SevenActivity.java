package com.example.palette.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.palette.R;
import com.example.palette.module.ProgressListener;
import com.example.palette.util.BitmapUtil;
import com.example.palette.util.OkHttpUtil;
import com.example.palette.util.QuickClickListener;
import com.example.palette.util.SecurityUtil;
import com.google.zxing.activity.CaptureActivity;
import com.google.zxing.util.Constant;

import java.io.File;
import java.io.IOException;

public class SevenActivity extends AppCompatActivity {
    public static final String TAG = "SevenActivity";
    private String[] texts = new String[]{"你好","你好","你好","你好","你好","你好"};
    private int[] imgs = new int[]{R.drawable.bg_earth,R.drawable.bg_earth,R.drawable.bg_earth,R.drawable.bg_earth,R.drawable.bg_earth,R.drawable.bg_earth};
    private TextView tv_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seven);
        ImageView imageView = findViewById(R.id.iv);
        tv_info = findViewById(R.id.tv_info);
        Bitmap b = BitmapUtil.createQRCode("b83e13be7fdd9ae675156423e5c424cb34210cd6cac77b8ac69b339ecc272659b5fa9e8d949dd2ba56334fd4ba4fffbf27cda1de5281e5894799a64adc20df149f72700b9bbbe3e85d02b48b70fa2025", 500, 500, null);
        imageView.setImageBitmap(BitmapUtil.stringToBitmap(BitmapUtil.bitmapToString(b)));
        imageView.setOnClickListener(new QuickClickListener() {
            @Override
            public void onQuickClickPass(View v) {
                Intent intent = new Intent(SevenActivity.this, CaptureActivity.class);
                startActivityForResult(intent,1);
            }
        });
        Log.i(TAG,BitmapUtil.bitmapToString(b));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK){
            tv_info.setText(data.getStringExtra(Constant.INTENT_EXTRA_KEY_QR_SCAN));
        }
    }
}