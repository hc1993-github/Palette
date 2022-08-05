package com.example.palette.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.palette.R;
import com.example.palette.socket.NettyClientHelper;

public class NettyClientActivity extends AppCompatActivity implements NettyClientHelper.ClientListener {
    NettyClientHelper clientHelper;
    TextView tv_client_connect;
    TextView tv_client_send;
    TextView tv_client_disconnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netty_client);
        clientHelper = new NettyClientHelper("10.193.110.71",8688,this);
        tv_client_connect = findViewById(R.id.tv_client_connect);
        tv_client_send = findViewById(R.id.tv_client_send);
        tv_client_disconnect = findViewById(R.id.tv_client_disconnect);
        tv_client_connect.setOnClickListener(v -> {
            clientHelper.connect();
        });
        tv_client_send.setOnClickListener(v -> {
            clientHelper.sendMessage("hello world");
        });
        tv_client_disconnect.setOnClickListener(v -> {
            clientHelper.disconnect();
        });
    }

    @Override
    public void connectSuccess(String message) {
        Log.d("ClientHelper", Thread.currentThread().getName()+" message: "+message);
    }

    @Override
    public void connectFail(String message) {
        Log.d("ClientHelper", Thread.currentThread().getName()+" message: "+message);
    }

    @Override
    public void receiveMessage(String message) {
        Log.d("ClientHelper", Thread.currentThread().getName()+" message: "+message);
    }
}