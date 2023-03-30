package com.example.palette.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.example.palette.R;

import java.text.DecimalFormat;

public class CircleProgressbar extends View {
    int defaultSize;
    int defaultMax;
    int mRingWidth;
    int mRadius;
    Paint mPaint;
    float mScale;
    Rect mRect;
    DecimalFormat mDecimalFormat;
    String mProgress = "0%";
    int mStartAngle;
    int mBorderScale;
    int mTextScale;
    int mBorderBgColor;
    int mCenterBgColor;
    int mLoadProgressColor;
    int mLoadTextColor;

    public CircleProgressbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mRect = new Rect();
        mDecimalFormat = new DecimalFormat("#0");
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressbar);
        defaultSize = typedArray.getInteger(R.styleable.CircleProgressbar_default_size, 200);
        defaultMax = typedArray.getInteger(R.styleable.CircleProgressbar_progress_max, 100);
        mStartAngle = typedArray.getInteger(R.styleable.CircleProgressbar_start_angle, 90);
        mBorderScale = typedArray.getInteger(R.styleable.CircleProgressbar_border_scale, 10);
        mTextScale = typedArray.getInteger(R.styleable.CircleProgressbar_text_scale, 2);
        mBorderBgColor = typedArray.getColor(R.styleable.CircleProgressbar_border_bg_color, Color.GRAY);
        mCenterBgColor = typedArray.getColor(R.styleable.CircleProgressbar_center_bg_color, Color.WHITE);
        mLoadProgressColor = typedArray.getColor(R.styleable.CircleProgressbar_loadProgress_color, Color.GREEN);
        mLoadTextColor = typedArray.getColor(R.styleable.CircleProgressbar_loadText_color, Color.GREEN);
        typedArray.recycle();
    }

    public void setProgress(int current) {
        this.mScale = (float) current / defaultMax;
        getBounds();
        postInvalidate();
    }

    private void getBounds() {
        mProgress = mDecimalFormat.format(mScale * defaultMax) + "%";
        mPaint.getTextBounds(mProgress, 0, mProgress.length(), mRect);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width, height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = defaultSize;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = defaultSize;
        }
        mRadius = Math.min(width, height);
        mRingWidth = mRadius / mBorderScale;
        mPaint.setTextSize(mRingWidth * mTextScale);
        setMeasuredDimension(mRadius, mRadius);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(mBorderBgColor);
        canvas.drawCircle(mRadius / 2, mRadius / 2, mRadius / 2, mPaint);
        mPaint.setColor(mLoadProgressColor);
        canvas.drawArc(0, 0, mRadius, mRadius, mStartAngle, mScale * 360, true, mPaint);
        mPaint.setColor(mCenterBgColor);
        canvas.drawCircle(mRadius / 2, mRadius / 2, mRadius / 2 - mRingWidth, mPaint);
        mPaint.setColor(mLoadTextColor);
        getBounds();
        canvas.drawText(mProgress, mRadius / 2 - mRect.width() / 2, mRadius / 2 + mRect.height() / 2, mPaint);
    }
}
