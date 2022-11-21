package com.example.palette.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.palette.R;
import com.example.palette.util.ScreenUtil;

public class SwitchView extends View {
    int defaultWidthSize = 200;
    int defaultHeightSize = 100;
    int width;
    int height;
    int defaultSpaceDpSize;
    int defaultSpacePxSize;
    int distance;
    int dex;
    int duration;
    boolean isOpen;
    boolean isAnim;
    Paint bgPaint;
    int bgPaintOpenColor;
    int bgPaintCloseColor;
    Paint centerPaint;
    int centerPaintColor;
    Paint textPaint;
    int textPaintColor;
    String start;
    String end;
    Rect rect;
    StatusListener listener;
    public SwitchView(Context context,AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.SwitchView);
        defaultSpaceDpSize = typedArray.getInteger(R.styleable.SwitchView_defaultSpaceDpSize,3);
        duration = typedArray.getInteger(R.styleable.SwitchView_duration,500);
        isOpen = typedArray.getBoolean(R.styleable.SwitchView_isOpen,true);
        bgPaintOpenColor = typedArray.getColor(R.styleable.SwitchView_bgPaintOpenColor, Color.GREEN);
        bgPaintCloseColor = typedArray.getColor(R.styleable.SwitchView_bgPaintCloseColor,Color.GRAY);
        centerPaintColor = typedArray.getColor(R.styleable.SwitchView_centerPaintColor,Color.WHITE);
        textPaintColor = typedArray.getColor(R.styleable.SwitchView_textPaintColor,Color.BLACK);
        start = typedArray.getString(R.styleable.SwitchView_openText)==null?" ":typedArray.getString(R.styleable.SwitchView_openText);
        end = typedArray.getString(R.styleable.SwitchView_closeText)==null?" ":typedArray.getString(R.styleable.SwitchView_closeText);
        typedArray.recycle();
        init();
    }

    private void init() {
        rect = new Rect();
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(bgPaintOpenColor);
        bgPaint.setStyle(Paint.Style.FILL);
        centerPaint = new Paint();
        centerPaint.setAntiAlias(true);
        centerPaint.setColor(centerPaintColor);
        centerPaint.setStyle(Paint.Style.FILL);
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(textPaintColor);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if(widthMode==MeasureSpec.EXACTLY){
            width = widthSize;
        }else {
            width = defaultWidthSize;
        }
        if(heightMode==MeasureSpec.EXACTLY){
            height = heightSize;
        }else {
            height = defaultHeightSize;
        }
        if(width> ScreenUtil.getWidthPx(getContext())){
            width = ScreenUtil.getWidthPx(getContext());
        }
        if(height>ScreenUtil.getHeightPx(getContext())){
            height=ScreenUtil.getHeightPx(getContext());
        }
        defaultSpacePxSize = ScreenUtil.dp2px(getContext(),defaultSpaceDpSize);
        distance = width-height;
        textPaint.setTextSize((height/2-defaultSpacePxSize)*0.75f);
        textPaint.getTextBounds(start,0,start.length(),rect);
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBgAndCircleAndText(canvas);
    }

    private void drawBgAndCircleAndText(Canvas canvas) {
        if(dex>distance/2){
            bgPaint.setColor(bgPaintCloseColor);
        }else {
            bgPaint.setColor(bgPaintOpenColor);
        }
        //canvas.drawArc(0,0,height,height,90,180,false,bgPaint);
        canvas.drawCircle(height/2,height/2,height/2,bgPaint);
        canvas.drawRect(height/2,0,width-height/2,height,bgPaint);
        //canvas.drawArc(width-height,0,width,height,-90,180,false,bgPaint);
        canvas.drawCircle(width-height/2,height/2,height/2,bgPaint);
        int cx = width - height/2;
        int cy = height/2;
        canvas.drawCircle(cx-dex,cy,height/2-defaultSpacePxSize,centerPaint);
        if(dex>distance/2){
            canvas.drawText(end,cx-rect.width()/2-dex,cy+rect.height()/2,textPaint);
        }else {
            canvas.drawText(start,cx-rect.width()/2-dex,cy+rect.height()/2,textPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_UP){
            if(isAnim){
                return true;
            }
            onActionUp();
        }
        return true;
    }

    private void onActionUp() {
        isAnim = true;
        if(isOpen){
            ObjectAnimator animator = ObjectAnimator.ofInt(SwitchView.this,"dex",0,distance);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    isOpen = false;
                    if(listener!=null){
                        listener.onStatusChanged(isOpen);
                    }
                    isAnim = false;
                }
            });
            animator.setDuration(duration);
            animator.start();
        }else {
            ObjectAnimator animator = ObjectAnimator.ofInt(SwitchView.this,"dex",distance,0);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    isOpen = true;
                    if(listener!=null){
                        listener.onStatusChanged(isOpen);
                    }
                    isAnim = false;
                }
            });
            animator.setDuration(duration);
            animator.start();
        }
    }

    private void setDex(int dex){
        this.dex = dex;
        postInvalidate();
    }

    public void addStatusListener(StatusListener listener){
        this.listener = listener;
    }

    public interface StatusListener{
        void onStatusChanged(boolean isOpen);
    }
}
