package com.example.palette.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.palette.R;
import com.example.palette.socket.SocketServerHelper;

public class SocketServerActivity extends AppCompatActivity implements SocketServerHelper.SocketServerHelperListener {
    private static final String TAG = "ServerActivity";
    SocketServerHelper socketServerHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        findViewById(R.id.tv_start).setOnClickListener(v -> {
            if(socketServerHelper==null || !socketServerHelper.isAlive()){
                socketServerHelper = new SocketServerHelper(this);
                socketServerHelper.start();
            }
        });
        findViewById(R.id.tv_stop).setOnClickListener(v -> {
            if(socketServerHelper!=null){
                socketServerHelper.stopServer();
            }
            while (true){
                if(socketServerHelper!=null && socketServerHelper.isAlive()){
                    Log.d(TAG, "正在关闭服务器中...");
                }else {
                    Log.d(TAG, "已关闭服务器");
                    break;
                }
            }
        });

    }

    @Override
    public void receiveMessage(String message) {
        Log.d(TAG, "收到了客户端的信息为"+message);
    }

}