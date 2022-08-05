package com.example.palette.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.palette.R;
import com.example.palette.socket.SocketClientHelper;

public class SocketClientActivity extends AppCompatActivity implements SocketClientHelper.SocketClientHelperListener{
    private static final String TAG = "ClientActivity";
    SocketClientHelper socketClientHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        EditText et_message = findViewById(R.id.et_message);

        findViewById(R.id.tv_connect).setOnClickListener(v -> {
            if(socketClientHelper==null){
                socketClientHelper = new SocketClientHelper("10.193.110.71",8688,this);
                socketClientHelper.start();
            }
        });

        findViewById(R.id.tv_send).setOnClickListener(v -> {
            if(socketClientHelper!=null){
                socketClientHelper.sendMessageToServer(et_message.getText().toString());
            }
        });
        findViewById(R.id.tv_receive).setOnClickListener(v -> {
            if(socketClientHelper!=null){
                socketClientHelper.readMessageFromServer();
            }
        });
        findViewById(R.id.tv_stop).setOnClickListener(v -> {
            if(socketClientHelper!=null){
                socketClientHelper.stopConnect();
                socketClientHelper=null;
            }
        });
    }

    @Override
    public void connectSuccess(String message) {
        Log.d(TAG, message);
    }

    @Override
    public void connectFail(String message) {
        Log.d(TAG, message);
        socketClientHelper=null;
    }

    @Override
    public void receiveMessage(String message) {
        Log.d(TAG, "收到了服务端的信息为"+message);
    }
}