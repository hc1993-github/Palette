package com.example.palette.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class GuaGuaKaView extends View {
    Bitmap mPic;
    Bitmap mBitmap;
    Canvas mCanvas;
    Paint mPathPaint;
    Path mPath;
    float pX;
    float pY;
    int mColor;
    PorterDuffXfermode mPorterDuffXfermode;
    Rect rect;
    Matrix mMatrix;
    String text="谢谢惠顾";
    int type = 1;
    private boolean isComplete = false;

    public GuaGuaKaView(Context context,AttributeSet attrs) {
        super(context, attrs);
        mPath = new Path();
        rect = new Rect();
        mMatrix = new Matrix();
        mColor = Color.parseColor("#c0c0c0");
        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        mPathPaint = new Paint();
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setStrokeCap(Paint.Cap.ROUND);
        mPathPaint.setStrokeJoin(Paint.Join.ROUND);
    }

    public void setBitmap(Bitmap pic) {
        this.isComplete = false;
        this.mPic = pic;
        this.type = 2;
        postInvalidate();
    }

    public void setText(String str){
        this.isComplete = false;
        this.text = str;
        this.type = 1;
        postInvalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                pX = event.getX();
                pY = event.getY();
                mPath.moveTo(event.getX(),event.getY());
                return true;
            case MotionEvent.ACTION_MOVE:
                float x = (pX + event.getX()) / 2;
                float y = (pY + event.getY()) / 2;
                mPath.quadTo(pX,pY,x,y);
                pX = event.getX();
                pY = event.getY();
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                post(runnable);
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(mColor);
        mPathPaint.setStrokeWidth(Math.min(getWidth(),getHeight())/6);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(type==1){
            drawText(canvas);
        }else if(type==2){
            drawBitmap(canvas);
        }else {
            drawText(canvas);
        }
        if(!isComplete){
            mPathPaint.setXfermode(mPorterDuffXfermode);
            mCanvas.drawPath(mPath,mPathPaint);
            canvas.drawBitmap(mBitmap,0,0,null);
        }
    }

    private Runnable runnable = new Runnable() {
        private int[] mPixels;
        @Override
        public void run() {
            int width = getWidth();
            int height = getHeight();
            float currentArea = 0;
            float totalArea = width*height;
            Bitmap bitmap = mBitmap;
            mPixels = new int[width*height];
            bitmap.getPixels(mPixels,0,width,0,0,width,height);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int index = i+j*width;
                    if(mPixels[index]==0){
                        currentArea++;
                    }
                }
            }
            if(currentArea>0 && totalArea>0){
                if((int)(currentArea*100/totalArea)>60){
                    isComplete = true;
                    postInvalidate();
                }
            }
        }
    };

    private void drawText(Canvas canvas) {
        mPathPaint.setStyle(Paint.Style.FILL);
        mPathPaint.setTextScaleX(2f);
        mPathPaint.setTextSize(22);
        mPathPaint.getTextBounds(text,0,text.length(),rect);
        canvas.drawText(text,getWidth()/2-rect.width()/2,getHeight()/2+rect.height()/2,mPathPaint);
        mPathPaint.setStyle(Paint.Style.STROKE);
    }

    private void drawBitmap(Canvas canvas) {
        mMatrix.reset();
        int bitmapWidth = mPic.getWidth();
        int bitmapHeight = mPic.getHeight();
        if (bitmapWidth > getWidth() || bitmapHeight > getHeight()) {
            if (bitmapWidth - getWidth() > bitmapHeight - getHeight()) {
                float ratio = getWidth() / (bitmapWidth * 1.0f);
                mMatrix.postScale(ratio, ratio);
                float translateY = (getHeight() - (bitmapHeight * ratio)) / 2f;
                mMatrix.postTranslate(0, translateY);
            } else {
                float ratio = getHeight() / (bitmapHeight * 1.0f);
                mMatrix.postScale(ratio, ratio);
                float translateX = (getWidth() - (bitmapWidth * ratio)) / 2f;
                mMatrix.postTranslate(translateX, 0);
            }
        } else {
            float translateX = (getWidth() - mPic.getWidth()) / 2f;
            float translateY = (getHeight() - mPic.getHeight()) / 2f;
            mMatrix.postTranslate(translateX, translateY);
        }
        canvas.drawBitmap(mPic,mMatrix,null);
    }
}
