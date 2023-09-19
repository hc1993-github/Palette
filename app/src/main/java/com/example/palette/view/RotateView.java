package com.example.palette.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.example.palette.util.ScreenUtil;

/**
 * XY旋转
 */
public class RotateView extends RelativeLayout {
    private float mMaxRadius = 300;
    private float mMaxCameraRotate = 30;
    private float mCameraRotateX;
    private float mCameraRotateY;
    private OnClickListener listener;
    private long downTime;
    private long upTime;
    public RotateView(Context context,AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        this.listener = l;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downTime = System.currentTimeMillis();
                getRotate(event);
                break;
            case MotionEvent.ACTION_MOVE:
                getRotate(event);
                break;
            case MotionEvent.ACTION_UP:
                upTime = System.currentTimeMillis();
                if(upTime-downTime<1000){
                    if(listener!=null){
                        listener.onClick(this);
                    }
                }
                break;
        }
        return true;
    }

    private void getRotate(MotionEvent event) {
        float rotateX = -(event.getY() - getHeight() / 2);
        float rotateY = (event.getX() - getWidth() / 2);
        float percentX = rotateX / mMaxRadius;
        float percentY = rotateY / mMaxRadius;
        if (percentX > 1) {
            percentX = 1;
        } else if (percentX < -1) {
            percentX = -1;
        }

        if (percentY > 1) {
            percentY = 1;
        } else if (percentY < -1) {
            percentY = -1;
        }
        mCameraRotateX = percentX * mMaxCameraRotate;
        mCameraRotateY = percentY * mMaxCameraRotate;
        setRotationX(mCameraRotateX);
        setRotationY(mCameraRotateY);
    }
}
