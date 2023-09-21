package com.example.palette.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.palette.R;
import com.example.palette.adapter.StringAdapter;
import com.example.palette.socket.MouseClient;
import com.example.palette.util.ScreenUtil;
import com.example.palette.view.CommonDialog;
import com.example.palette.view.GestureView;

import java.util.ArrayList;
import java.util.List;

/**
 * java远程控制鼠标样例
 */
public class SixActivity extends AppCompatActivity implements GestureView.TouchListener, View.OnClickListener,MouseClient.ClientListener{
    GestureView gestureView;
    EditText editText;
    TextView tv_connect;
    TextView tv_set;
    TextView tv_click;
    TextView tv_stop;
    TextView tv_rightclick;
    TextView tv_open_keyboard;
    TextView tv_close_keyboard;
    TextView tv_gun_up;
    TextView tv_gun_down;
    MouseClient client;
    ConstraintLayout constraintLayout1;
    ConstraintLayout constraintLayout2;
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
        tv_rightclick = findViewById(R.id.tv_rightclick);
        tv_open_keyboard = findViewById(R.id.open_keyboard);
        tv_close_keyboard = findViewById(R.id.tv_close_keyboard);
        tv_gun_up = findViewById(R.id.tv_gun_up);
        tv_gun_down = findViewById(R.id.tv_gun_down);
        constraintLayout1 = findViewById(R.id.conslayout1);
        constraintLayout2 = findViewById(R.id.conslayout2);
        gestureView.addTouchListener(this);
        tv_connect.setOnClickListener(this);
        tv_set.setOnClickListener(this);
        tv_click.setOnClickListener(this);
        tv_stop.setOnClickListener(this);
        tv_rightclick.setOnClickListener(this);
        tv_open_keyboard.setOnClickListener(this);
        tv_close_keyboard.setOnClickListener(this);
        tv_gun_up.setOnClickListener(this);
        tv_gun_down.setOnClickListener(this);
        initRecylers();
    }

    private void initRecylers() {
        RecyclerView recycler_line1 = findViewById(R.id.recycler_line1);
        recycler_line1.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        List<String> data1 = new ArrayList<>();
        data1.add("Esc");
        data1.add("F1");
        data1.add("F2");
        data1.add("F3");
        data1.add("F4");
        data1.add("F5");
        data1.add("F6");
        data1.add("F7");
        data1.add("F8");
        data1.add("F9");
        data1.add("F10");
        data1.add("F11");
        data1.add("F12");
        StringAdapter stringAdapter1 = new StringAdapter(data1);
        recycler_line1.setAdapter(stringAdapter1);
        stringAdapter1.setListener(new StringAdapter.ItemListener() {
            @Override
            public void onFingerDown(String string) {
                sendMessage(string.trim(),true);
            }

            @Override
            public void onFingerUp(String string) {
                sendMessage(string.trim(),false);
            }
        });
        RecyclerView recycler_line2 = findViewById(R.id.recycler_line2);
        recycler_line2.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        List<String> data2 = new ArrayList<>();
        data2.add("~\n、");
        data2.add("!\n1");
        data2.add("@\n2");
        data2.add("#\n3");
        data2.add("$\n4");
        data2.add("%\n5");
        data2.add("^\n6");
        data2.add("&\n7");
        data2.add("*\n8");
        data2.add("(\n9");
        data2.add(")\n0");
        data2.add("—\n-");
        data2.add("+\n=");
        data2.add("Backspace\n退格");
        data2.add("Ins\n ");
        data2.add("Home\n ");
        data2.add("PgUp\n ");
        StringAdapter stringAdapter2 = new StringAdapter(data2);
        recycler_line2.setAdapter(stringAdapter2);
        stringAdapter2.setListener(new StringAdapter.ItemListener() {
            @Override
            public void onFingerDown(String string) {
                sendMessage(string.trim(),true);
            }

            @Override
            public void onFingerUp(String string) {
                sendMessage(string.trim(),false);
            }
        });
        RecyclerView recycler_line3 = findViewById(R.id.recycler_line3);
        recycler_line3.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        List<String> data3 = new ArrayList<>();
        data3.add("Tab\n制表");
        data3.add("Q\n ");
        data3.add("W\n ");
        data3.add("E\n ");
        data3.add("R\n ");
        data3.add("T\n ");
        data3.add("Y\n ");
        data3.add("U\n ");
        data3.add("I\n ");
        data3.add("O\n ");
        data3.add("P\n ");
        data3.add("{\n[");
        data3.add("}\n]");
        data3.add("|\n\\");
        data3.add("Del\n ");
        data3.add("End\n ");
        data3.add("PgDn\n ");
        StringAdapter stringAdapter3 = new StringAdapter(data3);
        recycler_line3.setAdapter(stringAdapter3);
        stringAdapter3.setListener(new StringAdapter.ItemListener() {
            @Override
            public void onFingerDown(String string) {
                sendMessage(string.trim(),true);
            }

            @Override
            public void onFingerUp(String string) {
                sendMessage(string.trim(),false);
            }
        });
        RecyclerView recycler_line4 = findViewById(R.id.recycler_line4);
        recycler_line4.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        List<String> data4 = new ArrayList<>();
        data4.add("CapsLock\n大写");
        data4.add("A\n ");
        data4.add("S\n ");
        data4.add("D\n ");
        data4.add("F\n ");
        data4.add("G\n ");
        data4.add("H\n ");
        data4.add("J\n ");
        data4.add("K\n ");
        data4.add("L\n ");
        data4.add(":\n;");
        data4.add("”\n’");
        data4.add("   Enter   \n回车");
        StringAdapter stringAdapter4 = new StringAdapter(data4);
        recycler_line4.setAdapter(stringAdapter4);
        stringAdapter4.setListener(new StringAdapter.ItemListener() {
            @Override
            public void onFingerDown(String string) {
                sendMessage(string.trim(),true);
            }

            @Override
            public void onFingerUp(String string) {
                sendMessage(string.trim(),false);
            }
        });
        RecyclerView recycler_line5 = findViewById(R.id.recycler_line5);
        recycler_line5.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        List<String> data5 = new ArrayList<>();
        data5.add("Fn\n ");
        data5.add("Z\n ");
        data5.add("X\n ");
        data5.add("C\n ");
        data5.add("V\n ");
        data5.add("B\n ");
        data5.add("N\n ");
        data5.add("M\n ");
        data5.add("<\n,");
        data5.add(">\n.");
        data5.add("?\n/");
        StringAdapter stringAdapter5 = new StringAdapter(data5);
        recycler_line5.setAdapter(stringAdapter5);
        stringAdapter5.setListener(new StringAdapter.ItemListener() {
            @Override
            public void onFingerDown(String string) {
                sendMessage(string.trim(),true);
            }

            @Override
            public void onFingerUp(String string) {
                sendMessage(string.trim(),false);
            }
        });
        RecyclerView recycler_line6 = findViewById(R.id.recycler_line6);
        recycler_line6.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        List<String> data6 = new ArrayList<>();
        data6.add("Ctrl\n控制");
        data6.add("   Shift   \n上档");
        data6.add("Win\n ");
        data6.add("Alt\n换档");
        data6.add("       Space       \n空格");
        data6.add("↑\n ");
        data6.add("↓\n ");
        data6.add("←\n ");
        data6.add("→\n ");
        StringAdapter stringAdapter6 = new StringAdapter(data6);
        recycler_line6.setAdapter(stringAdapter6);
        stringAdapter6.setListener(new StringAdapter.ItemListener() {
            @Override
            public void onFingerDown(String string) {
                sendMessage(string.trim(),true);
            }

            @Override
            public void onFingerUp(String string) {
                sendMessage(string.trim(),false);
            }
        });
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
                click();
                break;
            case R.id.tv_rightclick:
                rightclick();
                break;
            case R.id.tv_stop:
                stop();
                finish();
                break;
            case R.id.open_keyboard:
                constraintLayout1.setVisibility(View.GONE);
                constraintLayout2.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_close_keyboard:
                constraintLayout1.setVisibility(View.VISIBLE);
                constraintLayout2.setVisibility(View.GONE);
                break;
            case R.id.tv_gun_up:
                up();
                break;
            case R.id.tv_gun_down:
                down();
                break;
        }
    }

    private void down() {
        if(client!=null){
            client.sendMessage("down");
        }
    }

    private void up() {
        if(client!=null){
            client.sendMessage("up");
        }
    }

    private void rightclick() {
        if(client!=null){
            client.sendMessage("rightclick");
        }
    }

    private void stop() {
        if(client!=null){
            client.sendMessage("stop");
            client.disconnect();
        }
    }

    private void click() {
        if(client!=null){
            client.sendMessage("click");
        }
    }

    private void sendMessage(String string,boolean isPress){
        if(client!=null){
            if(isPress){
                client.sendMessage("Press"+string);
            }else {
                client.sendMessage("Release"+string);
            }
        }
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
        if(message.equals("服务端关闭")){
            CommonDialog commonDialog = new CommonDialog.Builder()
                    .setContext(this)
                    .setCancelable(false)
                    .setLayoutId(R.layout.dialog_confirm)
                    .setLeftViewId(R.id.negative)
                    .setRightViewId(R.id.positive)
                    .setDefaultListener(new CommonDialog.CommonDialogDefaultOnClickListener() {
                        @Override
                        public void onLeftViewClick(Dialog dialog) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onCommonViewClick(Dialog dialog) {

                        }

                        @Override
                        public void onRightViewClick(Dialog dialog) {
                            dialog.dismiss();
                            if(client!=null){
                                client.disconnect();
                            }
                            finish();
                        }
                    })
                    .setOtherListener((dialog, viewId) -> {

                    }).build();
            commonDialog.show();
        }
    }
}