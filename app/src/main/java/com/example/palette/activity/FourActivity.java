package com.example.palette.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.palette.R;
import com.example.palette.hilt.MyInterface;
import com.example.palette.hilt.MyObserver;
import com.example.palette.view.TemperatureView;
import com.hc.serialport.SerialPortHelper;

import java.io.File;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * 串口调用等样例
 */
@AndroidEntryPoint
public class FourActivity extends AppCompatActivity implements SerialPortHelper.SerialPortListener {

    private static final String TAG = "FourActivity";

    @Inject
    MyObserver myObserver;

    @Inject
    MyInterface myInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four);
        TemperatureView temperatureView = findViewById(R.id.scaleView);
//        ScaleOrMoveView scaleOrMoveView = findViewById(R.id.scaleView);
//        scaleOrMoveView.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.white));
//        getLifecycle().addObserver(myObserver);
//        getLifecycle().addObserver(myInterface);
        SerialPortHelper helper = new SerialPortHelper(new File("/dev/ttyS1"),9600,this);
        temperatureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean send = helper.sendData("hello".getBytes());
                Log.d(TAG, "onClick: "+send);
            }
        });
//        GestureView gestureView = findViewById(R.id.gv);
//        ImageView imageView = findViewById(R.id.iv);
//        gestureView.addTouchListener(new GestureView.TouchListener() {
//            @Override
//            public void move(float x, float y) {
//                imageView.setImageBitmap(gestureView.getCurrentBitmap());
//            }
//        });
    }

    @Override
    public void onSuccess(File device) {
        Log.d(TAG, Thread.currentThread().getName()+"onSuccess: "+device.getAbsolutePath());
    }

    @Override
    public void onFail(File device, int failFlag) {
        Log.d(TAG, Thread.currentThread().getName()+failFlag+" onFail: "+device.getAbsolutePath());
    }

    @Override
    public void onReadData(byte[] data) {
        Log.d(TAG, Thread.currentThread().getName()+"onReadData: "+data);
    }
}