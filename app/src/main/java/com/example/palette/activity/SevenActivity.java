package com.example.palette.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.palette.R;
import com.example.palette.util.ScreenUtil;

public class SevenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seven);
        Log.d("SevenActivity", "onCreate: "+ScreenUtil.px2dp(this,800));
        Log.d("SevenActivity", "onCreate: "+ScreenUtil.px2dp(this,1280));
        findViewById(R.id.textView2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


}