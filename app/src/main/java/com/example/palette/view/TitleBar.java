package com.example.palette.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.palette.R;

public class TitleBar extends ConstraintLayout {
    TextView textView;
    ImageView imageView;
    public TitleBar(Context context,AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_title,this,true);
        textView = findViewById(R.id.tv);
        imageView = findViewById(R.id.img);
    }
    public void setText(String string){
        textView.setText(string);
    }
}
