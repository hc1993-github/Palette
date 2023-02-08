package com.example.palette.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.example.palette.R;
import com.example.palette.util.NetUtil;
import com.example.palette.util.SPUtil;
import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

public class EightActivity extends AppCompatActivity {
    Server mServer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eight);
        SPUtil.init(this);
        SPUtil.setName("test");
        SPUtil.put("bitmap",bitmapToString(BitmapFactory.decodeResource(getResources(),R.drawable.white),100));
        mServer = AndServer.webServer(this)
                .port(9999)
                .timeout(10, TimeUnit.SECONDS).listener(new Server.ServerListener() {
                    @Override
                    public void onStarted() {
                        Log.d("EightActivity-------","服务器绑定地址:"+ NetUtil.getLocalIPAddress().getHostAddress());
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

    private String bitmapToString(Bitmap bitmap, int bitmapQuality) {
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, bitmapQuality, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }


}