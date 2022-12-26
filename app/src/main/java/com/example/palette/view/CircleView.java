package com.example.palette.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


public class CircleView extends View {
    int default_bg;
    Paint paint;
    public CircleView(Context context,AttributeSet attrs) {
        super(context, attrs);
        default_bg = Color.parseColor("#aa000000");
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(default_bg);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircleClip(canvas);
    }

    private void drawCircleClip(Canvas canvas) {
        int vpadding;
        int border;
        int hpadding;
        if(getWidth()>getHeight()){
            vpadding = getHeight()/6;
            border = getHeight() - vpadding*2;
            hpadding = (getWidth()-border)/2;
        }else{
            hpadding = getWidth()/6;
            border = getWidth()-hpadding*2;
            vpadding = (getHeight()-border)/2;
        }
        canvas.drawRect(0,0,hpadding,getHeight(),paint);
        canvas.drawRect(getWidth()-hpadding,0,getWidth(),getHeight(),paint);
        canvas.drawRect(hpadding,0,getWidth()-hpadding,vpadding,paint);
        canvas.drawRect(hpadding,getHeight()-vpadding,getWidth()-hpadding,getHeight(),paint);
    }
}
