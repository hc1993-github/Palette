package com.example.palette.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.palette.R;
import com.example.palette.util.BitmapUtil;
import com.example.palette.util.QuickClickListener;
import com.google.zxing.util.Constant;

public class SevenActivity extends AppCompatActivity {
    public static final String TAG = "SevenActivity";
    private TextView tv_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seven);

        ImageView imageView = findViewById(R.id.iv);
        tv_info = findViewById(R.id.tv_info);
        Bitmap b = BitmapUtil.createQRCode("b83e13be7fdd9ae675156423e5c424cb34210cd6cac77b8ac69b339ecc272659b5fa9e8d949dd2ba56334fd4ba4fffbf27cda1de5281e5894799a64adc20df149f72700b9bbbe3e85d02b48b70fa2025", 500, 500, null);
        imageView.setImageBitmap(b);
        imageView.setOnClickListener(new QuickClickListener() {
            @Override
            public void onQuickClickPass(View v) {
//                扫描二维码调用案例
//                Intent intent = new Intent(SevenActivity.this, CaptureActivity.class);
//                startActivityForResult(intent,1);
//                控件抖动案例
//                Animation animation = AnimationUtils.loadAnimation(SevenActivity.this, R.anim.shake);
//                imageView.startAnimation(animation);
            }

            @Override
            public void onQuickClickNoPass(View v) {
                Log.i(TAG,"click too quick");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK){
            tv_info.setText(data.getStringExtra(Constant.INTENT_EXTRA_KEY_QR_SCAN));
        }
    }
}