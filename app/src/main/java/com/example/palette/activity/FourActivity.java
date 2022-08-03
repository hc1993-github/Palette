package com.example.palette.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.palette.R;
import com.example.palette.hilt.MyInterface;
import com.example.palette.hilt.MyObserver;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FourActivity extends AppCompatActivity {

    private static final String TAG = "FourActivity";

    @Inject
    MyObserver myObserver;

    @Inject
    MyInterface myInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four);
//        ScaleOrMoveView scaleOrMoveView = findViewById(R.id.scaleView);
//        scaleOrMoveView.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.white));
//        getLifecycle().addObserver(myObserver);
//        getLifecycle().addObserver(myInterface);
    }
}