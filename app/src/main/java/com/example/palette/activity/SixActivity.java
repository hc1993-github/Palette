package com.example.palette.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.palette.R;
import com.example.palette.socket.MouseClient;
import com.example.palette.util.ScreenUtil;
import com.example.palette.view.GestureView;

public class SixActivity extends AppCompatActivity implements GestureView.TouchListener, View.OnClickListener,MouseClient.ClientListener{
    GestureView gestureView;
    EditText editText;
    TextView tv_connect;
    TextView tv_set;
    TextView tv_click;
    TextView tv_stop;
    MouseClient client;
    int pcWidth;
    int pcHeight;
    int width;
    int height;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_six);

        gestureView = findViewById(R.id.gv);
        editText = findViewById(R.id.et);
        tv_connect = findViewById(R.id.tv_connect);
        tv_set = findViewById(R.id.tv_set);
        tv_click = findViewById(R.id.tv_click);
        tv_stop = findViewById(R.id.tv_stop);
        gestureView.addTouchListener(this);
        tv_connect.setOnClickListener(this);
        tv_set.setOnClickListener(this);
        tv_click.setOnClickListener(this);
        tv_stop.setOnClickListener(this);
    }

    @Override
    public void move(float x, float y) {
        if(client!=null){
            sendPoint(x,y);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_connect:
                if(client!=null){
                    client.connect();
                }
                break;
            case R.id.tv_set:
                set();
                break;
            case R.id.tv_click:
                if(client!=null){
                    click();
                }
                break;
            case R.id.tv_stop:
                if(client!=null){
                    stop();
                    client.disconnect();
                }
                finish();
                break;
        }
    }

    private void stop() {
        client.sendMessage("stop");
    }

    private void click() {
        client.sendMessage("click");
    }

    private void set() {
        String text = editText.getText().toString().trim();
        String[] split = text.split("-");
        pcWidth = Integer.parseInt(split[0]);
        pcHeight = Integer.parseInt(split[1]);
        String ip = split[2];
        width = ScreenUtil.dp2px(this,320);
        height = ScreenUtil.dp2px(this,220);
        client = new MouseClient(ip,9999,this);
    }

    private void sendPoint(float x, float y) {
        int densWidth = (int) (x*pcWidth/width);
        int densHeight  = (int) (y*pcHeight/height);
        client.sendMessage(densWidth+"."+densHeight);
    }

    @Override
    public void connectSuccess(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void connectFail(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void receiveMessage(String message) {

    }
}