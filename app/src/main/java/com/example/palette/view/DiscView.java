package com.example.palette.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.palette.R;
import com.example.palette.util.ScreenUtil;

/**
 * 圆形滚动
 */
public class DiscView extends ViewGroup {
    private int defaultRadius;
    private int mRadius;
    private int mWidth;
    private int mHeight;
    private float mPadding;
    private double mStartAngle = 0;
    private int[] mItemImgs;
    private String[] mItemTexts;
    private int mItemCount;
    private int mItemLayoutId;
    private onMenuClickListener listener;
    public DiscView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        defaultRadius = Math.min(ScreenUtil.getWidthPx(context), ScreenUtil.getHeightPx(context)) / 2;
        mPadding = defaultRadius * (1/12f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else {
            mWidth = defaultRadius * 2;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            mHeight = defaultRadius * 2;
        }
        if (mWidth > defaultRadius * 2) {
            mWidth = defaultRadius * 2;
        }
        if (mHeight > defaultRadius * 2) {
            mHeight = defaultRadius * 2;
        }
        int min = Math.min(mWidth, mHeight);
        setMeasuredDimension(min, min);
        mRadius = Math.max(getMeasuredWidth(),getMeasuredHeight());
        int childSize = (int) (min*(1/4f));
        for(int i=0;i<getChildCount();i++){
            View child = getChildAt(i);
            int makeMeasureSpec = MeasureSpec.makeMeasureSpec(childSize, MeasureSpec.EXACTLY);
            child.measure(makeMeasureSpec,makeMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int childWidth = (int) (mRadius * (1/4f));
        float angle = 360 / (childCount -1);
        int left,top;
        for(int i=0;i<childCount;i++){
            View child = getChildAt(i);
            if(child.getId()==R.id.id_center){
                continue;
            }
            mStartAngle %= 360;
            float dis = mRadius / 2f - childWidth/2-mPadding;
            left = mRadius/2+(int)Math.round(dis*Math.cos(Math.toRadians(mStartAngle)) - 1/2f*childWidth);
            top = mRadius/2+(int)Math.round(dis*Math.sin(Math.toRadians(mStartAngle))-1/2f*childWidth);
            child.layout(left,top,left+childWidth,top+childWidth);
            mStartAngle += angle;
        }
        View view = findViewById(R.id.id_center);
        if(view!=null){
            view.setOnClickListener(v -> {
                if(listener!=null){
                    listener.itemCenterClick();
                }
            });
            int ct = mRadius/2-view.getMeasuredWidth()/2;
            int cr = ct+view.getMeasuredWidth();
            view.layout(ct,ct,cr,cr);
        }
    }

    private float mLastX;
    private float mLastY;
    private long mDownTime;
    private float mTmpAngle;
    private boolean isFling;
    private int mFlingableValue = 300;
    private AutoFlingRunnable mFlingRunnable;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                mDownTime = System.currentTimeMillis();
                mTmpAngle = 0;
                if(isFling){
                    removeCallbacks(mFlingRunnable);
                    isFling = false;
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float start = getAngle(mLastX,mLastY);
                float end = getAngle(x,y);
                if(getQuadrant(x,y)==1|| getQuadrant(x,y)==4){
                    mStartAngle +=end-start;
                    mTmpAngle +=end-start;
                }else {
                    mStartAngle +=start-end;
                    mTmpAngle +=start-end;
                }
                requestLayout();
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                float per = mTmpAngle*1000/(System.currentTimeMillis()-mDownTime);
                if(Math.abs(per)>mFlingableValue && !isFling){
                    post(mFlingRunnable = new AutoFlingRunnable(per));
                    return true;
                }
                if(Math.abs(mTmpAngle)>3){
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    private int getQuadrant(float x, float y) {
        int tmpX = (int) (x - mRadius / 2);
        int tmpY = (int) (y - mRadius / 2);
        if (tmpX >= 0)
        {
            return tmpY >= 0 ? 4 : 1;
        } else
        {
            return tmpY >= 0 ? 3 : 2;
        }
    }

    private float getAngle(float xT, float yT) {
        double x = xT - (mRadius / 2d);
        double y = yT - (mRadius / 2d);
        return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
    }

    public void setItemLayoutId(int layoutId){
        mItemLayoutId = layoutId;
    }

    public void setItems(int[] resIds,String[] texts){
        mItemImgs = resIds;
        mItemTexts = texts;
        mItemCount = resIds==null?texts.length:resIds.length;
        addItems();
    }

    private void addItems() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for(int i=0;i<mItemCount;i++){
            int j = i;
            View view = inflater.inflate(mItemLayoutId, this, false);
            ImageView imageView = view.findViewById(R.id.id_item_image);
            TextView textView = view.findViewById(R.id.id_item_text);
            if(imageView!=null){
                imageView.setVisibility(VISIBLE);
                imageView.setImageResource(mItemImgs[i]);
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listener!=null){
                            listener.itemClick(j);
                        }
                    }
                });
            }
            if(textView!=null){
                textView.setVisibility(VISIBLE);
                textView.setText(mItemTexts[i]);
                textView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listener!=null){
                            listener.itemClick(j);
                        }
                    }
                });
            }
            addView(view);
        }
    }

    public void setListener(onMenuClickListener listener) {
        this.listener = listener;
    }

    public interface onMenuClickListener{
        void itemClick(int position);
        void itemCenterClick();
    }
    private class AutoFlingRunnable implements Runnable{
        private float per;

        public AutoFlingRunnable(float per) {
            this.per = per;
        }

        @Override
        public void run() {
            if((int)Math.abs(per)<20){
                isFling = false;
                return;
            }
            isFling = true;
            mStartAngle +=(per/30);
            per /= 1.0666f;
            postDelayed(this,30);
            requestLayout();
        }
    }
}
