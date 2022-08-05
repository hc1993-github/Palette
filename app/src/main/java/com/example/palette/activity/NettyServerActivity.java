package com.example.palette.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.palette.R;
import com.example.palette.socket.NettyServerHelper;

public class NettyServerActivity extends AppCompatActivity implements NettyServerHelper.ServerListener {
    NettyServerHelper serverHelper;
    TextView tv_server_start;
    TextView tv_server_stop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netty_server);
        serverHelper = new NettyServerHelper(8688,this);
        tv_server_start = findViewById(R.id.tv_server_start);
        tv_server_stop = findViewById(R.id.tv_server_stop);
        tv_server_start.setOnClickListener(v -> {
            serverHelper.start();
        });
        tv_server_stop.setOnClickListener(v -> {
            serverHelper.stop();
        });
    }

    @Override
    public void receiveMessage(String message) {
        Log.d("ServerHelper", Thread.currentThread().getName()+" message: "+message);
    }
}