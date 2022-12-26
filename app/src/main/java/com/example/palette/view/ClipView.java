package com.example.palette.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class ClipView extends RelativeLayout {
    CircleView circleView;
    ScaleOrMoveView scaleOrMoveView;
    public ClipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        circleView = new CircleView(context,attrs);
        scaleOrMoveView = new ScaleOrMoveView(context,attrs);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(scaleOrMoveView,layoutParams);
        addView(circleView,layoutParams);
    }

    public void setImageBitmap(Bitmap bitmap) {
        scaleOrMoveView.setImageBitmap(bitmap);
    }

    public Bitmap clip(){
        return scaleOrMoveView.clipBitmap();
    }
}
