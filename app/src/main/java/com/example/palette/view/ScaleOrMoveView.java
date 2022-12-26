package com.example.palette.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.palette.R;

public class ScaleOrMoveView extends View{
    public static final int STATUS_INIT = 1;
    public static final int STATUS_BIGGER = 2;
    public static final int STATUS_SMALLER = 3;
    public static final int STATUS_MOVE = 4;
    Matrix matrix = new Matrix();
    Bitmap sourceBitmap;
    int currentStatus;
    int width;
    int height;
    float centerPointX;
    float centerPointY;
    float currentBitmapWidth;
    float currentBitmapHeight;
    float lastMoveX = -1;
    float lastMoveY = -1;
    float movedDistanceX;
    float movedDistanceY;
    float totalTranslateX;
    float totalTranslateY;
    float totalRatio;
    float scaledRatio;
    float initRatio;
    double lastFingerDis;
    boolean isLimit;
    float maxTimes;
    float minTimes;
    public ScaleOrMoveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScaleOrMoveView);
        isLimit = typedArray.getBoolean(R.styleable.ScaleOrMoveView_bordLimit,false);
        maxTimes = typedArray.getFloat(R.styleable.ScaleOrMoveView_maxTimes,20f);
        minTimes = typedArray.getFloat(R.styleable.ScaleOrMoveView_minTimes,0f);
        typedArray.recycle();
        currentStatus = STATUS_INIT;
    }

    public void setImageBitmap(Bitmap bitmap) {
        sourceBitmap = bitmap;
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            width = getWidth();
            height = getHeight();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) {
                    lastFingerDis = calculate2FingerDistance(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1) {
                    float xMove = event.getX();
                    float yMove = event.getY();
                    if (lastMoveX == -1 && lastMoveY == -1) {
                        lastMoveX = xMove;
                        lastMoveY = yMove;
                    }
                    currentStatus = STATUS_MOVE;
                    movedDistanceX = xMove - lastMoveX;
                    movedDistanceY = yMove - lastMoveY;
                    if(isLimit){
                        if (totalTranslateX + movedDistanceX > 0) {
                            movedDistanceX = 0;
                        } else if (width - (totalTranslateX + movedDistanceX) > currentBitmapWidth) {
                            movedDistanceX = 0;
                        }
                        if (totalTranslateY + movedDistanceY > 0) {
                            movedDistanceY = 0;
                        } else if (height - (totalTranslateY + movedDistanceY) > currentBitmapHeight) {
                            movedDistanceY = 0;
                        }
                    }
                    invalidate();
                    lastMoveX = xMove;
                    lastMoveY = yMove;
                } else if (event.getPointerCount() == 2) {
                    calculateCenterBy2Finger(event);
                    double fingerDistance = calculate2FingerDistance(event);
                    if (fingerDistance > lastFingerDis) {
                        currentStatus = STATUS_BIGGER;
                    } else {
                        currentStatus = STATUS_SMALLER;
                    }
                    if ((currentStatus == STATUS_BIGGER && totalRatio < maxTimes * initRatio) || (currentStatus == STATUS_SMALLER) && totalRatio > minTimes * initRatio) {
                        scaledRatio = (float) (fingerDistance / lastFingerDis);
                        totalRatio = totalRatio * scaledRatio;
                        if (totalRatio > maxTimes * initRatio) {
                            totalRatio = maxTimes * initRatio;
                        } else if (totalRatio < minTimes * initRatio) {
                            totalRatio = minTimes * initRatio;
                        }
                        invalidate();
                        lastFingerDis = fingerDistance;
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() == 2) {
                    lastMoveX = -1;
                    lastMoveY = -1;
                }
                break;
            case MotionEvent.ACTION_UP:
                lastMoveX = -1;
                lastMoveY = -1;
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (currentStatus) {
            case STATUS_INIT:
                initBitmap(canvas);
                break;
            case STATUS_BIGGER:
            case STATUS_SMALLER:
                scaleBitmap(canvas);
                break;
            case STATUS_MOVE:
                moveBitmap(canvas);
                break;
        }
    }

    private void moveBitmap(Canvas canvas) {
        matrix.reset();
        float translateX = totalTranslateX + movedDistanceX;
        float translateY = totalTranslateY + movedDistanceY;
        matrix.postScale(totalRatio, totalRatio);
        matrix.postTranslate(translateX, translateY);
        totalTranslateX = translateX;
        totalTranslateY = translateY;
        canvas.drawBitmap(sourceBitmap, matrix, null);
    }

    private void scaleBitmap(Canvas canvas) {
        matrix.reset();
        matrix.postScale(totalRatio, totalRatio);
        float scaledWidth = sourceBitmap.getWidth() * totalRatio;
        float scaledHeight = sourceBitmap.getHeight() * totalRatio;
        float translateX = 0f;
        float translateY = 0f;
        if (currentBitmapWidth < width) {
            translateX = (width - scaledWidth) / 2f;
        } else {
            translateX = totalTranslateX * scaledRatio + centerPointX * (1 - scaledRatio);
            if (translateX > 0) {
                translateX = 0;
            } else if (width - translateX > scaledWidth) {
                translateX = width - scaledWidth;
            }
        }
        if (currentBitmapHeight < height) {
            translateY = (height - scaledHeight) / 2f;
        } else {
            translateY = totalTranslateY * scaledRatio + centerPointY * (1 - scaledRatio);
            if (translateY > 0) {
                translateY = 0;
            } else if (height - translateY > scaledHeight) {
                translateY = height - scaledHeight;
            }
        }
        matrix.postTranslate(translateX, translateY);
        totalTranslateX = translateX;
        totalTranslateY = translateY;
        currentBitmapWidth = scaledWidth;
        currentBitmapHeight = scaledHeight;
        canvas.drawBitmap(sourceBitmap, matrix, null);
    }

    private void initBitmap(Canvas canvas) {
        if (sourceBitmap != null) {
            matrix.reset();
            int bitmapWidth = sourceBitmap.getWidth();
            int bitmapHeight = sourceBitmap.getHeight();
            if (bitmapWidth > width || bitmapHeight > height) {
                if (bitmapWidth - width > bitmapHeight - height) {
                    float ratio = width / (bitmapWidth * 1.0f);
                    matrix.postScale(ratio, ratio);
                    float translateY = (height - (bitmapHeight * ratio)) / 2f;
                    matrix.postTranslate(0, translateY);
                    totalTranslateY = translateY;
                    totalRatio = initRatio = ratio;
                } else {
                    float ratio = height / (bitmapHeight * 1.0f);
                    matrix.postScale(ratio, ratio);
                    float translateX = (width - (bitmapWidth * ratio)) / 2f;
                    matrix.postTranslate(translateX, 0);
                    totalTranslateX = translateX;
                    totalRatio = initRatio = ratio;
                }
                currentBitmapWidth = bitmapWidth * initRatio;
                currentBitmapHeight = bitmapHeight * initRatio;
            } else {
                float translateX = (width - sourceBitmap.getWidth()) / 2f;
                float translateY = (height - sourceBitmap.getHeight()) / 2f;
                matrix.postTranslate(translateX, translateY);
                totalTranslateX = translateX;
                totalTranslateY = translateY;
                totalRatio = initRatio = 1f;
                currentBitmapWidth = bitmapWidth;
                currentBitmapHeight = bitmapHeight;
            }
            canvas.drawBitmap(sourceBitmap, matrix, null);
        }
    }

    public Bitmap clipBitmap(){
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
        Bitmap bitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(sourceBitmap,matrix,null);
        return Bitmap.createBitmap(bitmap,hpadding,vpadding,getWidth()-2*hpadding,getWidth()-2*hpadding);
    }

    private double calculate2FingerDistance(MotionEvent event) {
        float disX = Math.abs(event.getX(0) - event.getX(1));
        float disY = Math.abs(event.getY(0) - event.getY(1));
        return Math.sqrt(disX * disX + disY * disY);
    }

    private void calculateCenterBy2Finger(MotionEvent event) {
        centerPointX = (event.getX(0) + event.getX(1)) / 2;
        centerPointY = (event.getY(0) + event.getY(1)) / 2;
    }

}
