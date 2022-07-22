package com.example.palette;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class SecActivity extends AppCompatActivity {

    private static final String TAG = "SecActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec);
        Log.d(TAG, ""+ScreenUtil.getWidthPx(this)+"-"+ScreenUtil.getDensity(this)+"-"+ScreenUtil.getDpi(this));
    }
}