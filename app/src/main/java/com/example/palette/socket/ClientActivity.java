package com.example.palette.socket;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.palette.R;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientActivity extends AppCompatActivity {
    Socket socket;
    PrintWriter writer;
    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    writer.println((String)msg.obj);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        EditText et_message = findViewById(R.id.et_message);

        findViewById(R.id.tv_connect).setOnClickListener(v -> {
            new Thread(() -> connectServer()).start();
        });

        findViewById(R.id.tv_send).setOnClickListener(v -> {
            String string = et_message.getText().toString();
            if(!TextUtils.isEmpty(string) && writer!=null){
                Message message = Message.obtain();
                message.what = 1;
                message.obj = string;
                handler.sendMessage(message);
            }
        });
        findViewById(R.id.tv_stop).setOnClickListener(v -> {
            try {
                if(socket!=null){
                    socket.close();
                    socket = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void connectServer() {
        while (socket==null){
            try {
                socket = new Socket("10.238.10.172",8688);
                writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"utf-8")),true);
            }catch (Exception e){
                SystemClock.sleep(1000);
            }
        }
    }
}