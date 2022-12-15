package com.example.palette.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.palette.R;
import com.example.palette.util.ScreenUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RippleView extends View {
    private Paint paint;
    private float mRadius = 0;
    private AnimatorSet animatorSet;
    private int duration;
    private int color;
    private int pippleWidth;
    private int space;
    public RippleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RippleView);
        duration = typedArray.getInteger(R.styleable.RippleView_pippleDuration, 1500);
        color = typedArray.getColor(R.styleable.RippleView_pippleColor, Color.BLUE);
        pippleWidth = typedArray.getInteger(R.styleable.RippleView_pippleWidthDp, 2);
        space = typedArray.getInteger(R.styleable.RippleView_pippleSpaceDp, 0);
        typedArray.recycle();
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(ScreenUtil.dp2px(getContext(),pippleWidth));
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
            width = (int) (Math.min(ScreenUtil.getWidthPx(getContext()),ScreenUtil.getHeightPx(getContext()))/4f);
        }
        if(heightMode==MeasureSpec.EXACTLY){
            height = heightSize;
        }else {
            height = (int) (Math.min(ScreenUtil.getWidthPx(getContext()),ScreenUtil.getHeightPx(getContext()))/4f);
        }
        if(width> ScreenUtil.getWidthPx(getContext())){
            width = ScreenUtil.getWidthPx(getContext());
        }
        if(height>ScreenUtil.getHeightPx(getContext())){
            height=ScreenUtil.getHeightPx(getContext());
        }
        int min = Math.min(width, height);
        setMeasuredDimension(min,min);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(getWidth()/2,getHeight()/2,mRadius,paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        doAnim(Math.min(w,h)/2);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        animatorSet.end();
        animatorSet = null;
    }

    public void doAnim(int radius) {
        int i = ScreenUtil.dp2px(getContext(), space);
        if(i>radius){
            i = radius;
        }else if(i==0){
            i = (int) (radius / 4f);
        }
        ValueAnimator animator = ValueAnimator.ofInt(radius-i,radius);
        animator.setDuration(duration);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRadius = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(RippleView.this,"alpha",1,0);
        animator1.setDuration(duration);
        animator1.setRepeatCount(ObjectAnimator.INFINITE);
        List<Animator> animatorList = new ArrayList<>();
        animatorList.add(animator);
        animatorList.add(animator1);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorList);
        animatorSet.start();
    }
}
