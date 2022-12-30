package com.example.palette.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class ClipView extends RelativeLayout {
    ScaleOrMoveView scaleOrMoveView;
    public static final int ROUND = 1;
    public static final int SQUARE = 2;
    public ClipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.parseColor("#aa000000"));
        scaleOrMoveView = new ScaleOrMoveView(context,attrs);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(scaleOrMoveView,layoutParams);
    }

    public void setImageBitmap(Bitmap bitmap) {
        scaleOrMoveView.setImageBitmap(bitmap);
    }

    public Bitmap clip(){
        return scaleOrMoveView.clipBitmap();
    }


    public void setClip(int type){
        if(type!=1 && type!=2){
            type = 2;
        }
        scaleOrMoveView.setClipType(type);
    }
}
