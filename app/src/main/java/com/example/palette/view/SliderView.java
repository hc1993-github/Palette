package com.example.palette.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.example.palette.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 可拖动的数字选择
 */
public class SliderView extends View {
    int mDefaultWidth = 200;
    int mDefaultHeight = 100;
    int mWidth;
    int mHeight;
    int mScreenWidth;
    int mScreenHeight;
    Paint mBgPaint;
    Paint mMovePaint;
    Paint mMoveFillPaint;
    Paint mTextPaint;
    Rect mTextRect;
    int mMoveDistance;
    int mMaxDistance;
    int mStart;
    int mEnd;
    List<Float> mDistanceRange = new ArrayList<>();
    List<Integer> mItemRange = new ArrayList<>();
    DecimalFormat mDecimalFormat = new DecimalFormat(".0000");
    int mIndex;
    int mCurrentItem;
    onActionUpListener mListener;
    public SliderView(Context context,AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SliderView);
        mStart = typedArray.getInteger(R.styleable.SliderView_slider_start,-3);
        mEnd = typedArray.getInteger(R.styleable.SliderView_slider_end,3);
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;
        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(Color.GRAY);
        mBgPaint.setStyle(Paint.Style.FILL);
        mMovePaint = new Paint();
        mMovePaint.setAntiAlias(true);
        mMovePaint.setColor(Color.WHITE);
        mMovePaint.setStyle(Paint.Style.FILL);
        mMoveFillPaint = new Paint();
        mMoveFillPaint.setAntiAlias(true);
        mMoveFillPaint.setColor(Color.BLUE);
        mMoveFillPaint.setStyle(Paint.Style.FILL);
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.BLUE);
        mTextRect = new Rect();
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if(widthMode==MeasureSpec.EXACTLY){
            mWidth = widthSize;
        }else {
            mWidth = mDefaultWidth;
        }
        if(heightMode==MeasureSpec.EXACTLY){
            mHeight = heightSize;
        }else {
            mHeight = mDefaultHeight;
        }
        if(mWidth> mScreenWidth){
            mWidth = mScreenWidth;
        }
        if(mHeight>mScreenHeight){
            mHeight = mScreenHeight;
        }
        mMaxDistance = mWidth-mHeight;
        initRange();
        mTextPaint.setTextSize((mHeight/2)*0.75f);
        setMeasuredDimension(mWidth,mHeight);
    }

    private void initRange() {
        mDistanceRange.clear();
        mItemRange.clear();
        if(mEnd<=mStart){
            throw new RuntimeException("the end size must bigger than the start size");
        }
        float pattern = Float.parseFloat(mDecimalFormat.format((float) mMaxDistance / (mEnd-mStart)));
        for (int i = 1; i <= mEnd-mStart-1; i++) {
            mDistanceRange.add(i*pattern);
        }
        for (int i = mStart; i <= mEnd; i++) {
            mItemRange.add(i);
        }
        mDistanceRange.add((float) mMaxDistance);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBg(canvas);
        drawMoveFill(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        int pos;
        switch (action){
            case MotionEvent.ACTION_DOWN:
                pos = (int) x;
                if(pos<=0){
                    mMoveDistance = 0;
                }else if(pos>mMaxDistance){
                    mMoveDistance = mMaxDistance;
                }else {
                    mMoveDistance = pos;
                }
                postInvalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                pos = (int) x;
                if(pos<=0){
                    mMoveDistance = 0;
                }else if(pos>mMaxDistance){
                    mMoveDistance = mMaxDistance;
                }else {
                    mMoveDistance = pos;
                }
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                float f = mDistanceRange.get(mIndex);
                if(mMoveDistance<=0){
                    mMoveDistance = 0;
                }else {
                    mMoveDistance = (int) f;
                }
                if(mListener!=null){
                    mListener.onCurrentSize(mCurrentItem);
                }
                postInvalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void drawBg(Canvas canvas) {
        int half = mHeight / 2;
        canvas.drawCircle(half,half,half,mBgPaint);
        canvas.drawRect(half,0,mWidth-half,mHeight,mBgPaint);
        canvas.drawCircle(mWidth-half,half,half,mBgPaint);
    }

    private void drawMoveFill(Canvas canvas) {
        String text = getCurrentText();
        int half = mHeight / 2;
        if(mMoveDistance>0){
            canvas.drawCircle(half,half,half,mMoveFillPaint);
            canvas.drawRect(half,0,half+mMoveDistance,mHeight,mMoveFillPaint);
        }
        canvas.drawCircle(half+mMoveDistance,half,half,mMovePaint);
        mTextPaint.getTextBounds(text,0,text.length(),mTextRect);
        canvas.drawText(text,half-mTextRect.centerX()+mMoveDistance,half-mTextRect.centerY(),mTextPaint);
    }

    private String getCurrentText() {
        for (int i = 0; i < mDistanceRange.size(); i++) {
            Float item = mDistanceRange.get(i);
            if(mMoveDistance<=item){
                if(mMoveDistance<=0){
                    mIndex = 0;
                    mCurrentItem = mItemRange.get(mIndex);
                }else {
                    mIndex = i;
                    mCurrentItem = mItemRange.get(mIndex+1);
                }
                return String.valueOf(mCurrentItem);
            }
        }
        return null;
    }

    public void addOnActionUpListener(onActionUpListener listener){
        mListener = listener;
    }

    public interface onActionUpListener{
        void onCurrentSize(int currentSize);
    }
}
