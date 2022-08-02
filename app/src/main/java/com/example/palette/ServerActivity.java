package com.example.palette;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerActivity extends AppCompatActivity {
    private boolean isDestoryed = false;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        findViewById(R.id.tv_start).setOnClickListener(v -> {
            if(thread==null){
                thread = new Thread(new TcpRunnable());
                thread.start();
            }
        });
        findViewById(R.id.tv_stop).setOnClickListener(v -> {
            if(thread!=null){
                isDestoryed = true;
                thread.interrupt();
                thread = null;
            }
        });

    }

    private class TcpRunnable implements Runnable{
        @Override
        public void run() {
            ServerSocket serverSocket;
            try {
                serverSocket = new ServerSocket(8688);
            }catch (Exception e){
                return;
            }
            while (!isDestoryed){
                try {
                    Socket client = serverSocket.accept();
                    new Thread(() -> {
                        try {
                            response(client);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void response(Socket client) throws Exception{
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(),"utf-8"));
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())));
        while (!isDestoryed){
            String str = reader.readLine();
            if(!TextUtils.isEmpty(str)){
                Log.d("huachen", "收到了客户端的信息为"+str);
            }
        }
        writer.close();
        reader.close();
        client.close();
    }
}