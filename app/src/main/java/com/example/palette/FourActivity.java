package com.example.palette;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.palette.view.ScaleOrMoveView;

public class FourActivity extends AppCompatActivity {

    private static final String TAG = "FourActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four);
        ScaleOrMoveView scaleOrMoveView = findViewById(R.id.scaleView);
        scaleOrMoveView.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.white));
    }
}