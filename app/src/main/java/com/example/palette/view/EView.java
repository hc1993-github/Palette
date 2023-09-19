package com.example.palette.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * E字
 */
public class EView extends View {
    Paint paint;
    public static final int UP = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;
    public static final int RIGHT = 4;
    int mDirection = RIGHT;
    public EView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
    }
    public void setDirection(int direction){
        mDirection = direction;
        postInvalidate();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if(widthMode==MeasureSpec.EXACTLY){
            width = widthSize;
        }else {
            width = 100;
        }
        if(heightMode==MeasureSpec.EXACTLY){
            height = heightSize;
        }else {
            height = 100;
        }
        int min = Math.min(width, height);
        setMeasuredDimension(min,min);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawE(canvas);
    }

    private void drawE(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        if(mDirection==UP){
            canvas.drawRect(getWidth()/5,0,getWidth()*2/5,getHeight()*3/4,paint);
            canvas.drawRect(getWidth()*3/5,0,getWidth()*4/5,getHeight()*3/4,paint);
        }else if(mDirection==DOWN){
            canvas.drawRect(getWidth()/5,getHeight()/4,getWidth()*2/5,getHeight(),paint);
            canvas.drawRect(getWidth()*3/5,getHeight()/4,getWidth()*4/5,getHeight(),paint);
        }else if(mDirection==LEFT){
            canvas.drawRect(0,getHeight()/5,getWidth()*3/4,getHeight()*2/5,paint);
            canvas.drawRect(0,getHeight()*3/5,getWidth()*3/4,getHeight()*4/5,paint);
        }else if(mDirection==RIGHT){
            canvas.drawRect(getWidth()/4,getHeight()/5,getWidth(),getHeight()*2/5,paint);
            canvas.drawRect(getWidth()/4,getHeight()*3/5,getWidth(),getHeight()*4/5,paint);
        }
    }
}
