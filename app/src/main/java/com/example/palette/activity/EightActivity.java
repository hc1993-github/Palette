package com.example.palette.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.palette.R;
import com.example.palette.util.NetUtil;
import com.example.palette.util.SPUtil;
import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;

import java.util.concurrent.TimeUnit;

public class EightActivity extends AppCompatActivity {
    Server mServer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eight);
        SPUtil.init(this);
        SPUtil.setName("test");
        SPUtil.put("name","helloworld");
        SPUtil.put("id","30");
        mServer = AndServer.webServer(this)
                .port(9999)
                .timeout(10, TimeUnit.SECONDS).listener(new Server.ServerListener() {
                    @Override
                    public void onStarted() {
                        Log.d("EightActivity","服务器绑定地址:"+ NetUtil.getLocalIPAddress().getHostAddress());
                    }

                    @Override
                    public void onStopped() {

                    }

                    @Override
                    public void onException(Exception e) {

                    }
                })
                .build();

        mServer.startup();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mServer.shutdown();
    }
}