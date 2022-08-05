package com.example.palette.socket;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServerHelper extends Thread {
    private boolean isDestoryed = false;
    private SocketServerHelperListener listener;
    private ServerSocket serverSocket;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    listener.receiveMessage((String) msg.obj);
                    break;
            }
        }
    };

    public SocketServerHelper(SocketServerHelperListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(8688);
        } catch (Exception e) {
            return;
        }
        while (!isDestoryed) {
            try {
                Socket client = serverSocket.accept();
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            response(client);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopServer() {
        try {
            isDestoryed = true;
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void response(Socket client) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), "utf-8"));
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "utf-8")), true);
        while (!isDestoryed) {
            String str = reader.readLine();
            if (!TextUtils.isEmpty(str) && listener != null) {
                Message message = Message.obtain();
                message.what = 1;
                message.obj = str;
                handler.sendMessage(message);
                //dosomething
            }
        }
        writer.close();
        reader.close();
        client.close();
    }

    public interface SocketServerHelperListener {
        void receiveMessage(String message);
    }
}
