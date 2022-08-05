package com.example.palette.socket;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClientHelper extends Thread {
    Socket socket;
    PrintWriter writer;
    BufferedReader reader;
    String ip;
    int port;
    SocketClientHelperListener listener;
    boolean isReading = false;
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case -1:
                    listener.connectFail("连接服务端失败");
                    break;
                case 0:
                    listener.connectSuccess("连接服务端成功");
                    break;
                case 1:
                    writer.println((String) msg.obj);
                    break;
                case 2:
                    listener.receiveMessage((String) msg.obj);
                    break;
            }
        }
    };

    public SocketClientHelper(String ip, int port, SocketClientHelperListener listener) {
        this.ip = ip;
        this.port = port;
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(ip, port);
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8")), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
            handler.sendEmptyMessage(-1);
        }
        if (socket != null) {
            handler.sendEmptyMessage(0);
        }
    }

    public void sendMessageToServer(String msg) {
        Message message = Message.obtain();
        message.what = 1;
        message.obj = msg;
        handler.sendMessage(message);
    }

    public void readMessageFromServer() {
        if (reader != null && listener != null) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        isReading = true;
                        while (isReading && reader != null) {
                            String msg = reader.readLine();
                            if (!TextUtils.isEmpty(msg)) {
                                Message message = Message.obtain();
                                message.what = 2;
                                message.obj = msg;
                                handler.sendMessage(message);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    public void stopReadMessageFromServer() {
        isReading = false;
    }

    public void stopConnect() {
        try {
            isReading = false;
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface SocketClientHelperListener {
        void connectSuccess(String message);

        void connectFail(String message);

        void receiveMessage(String message);

    }
}
