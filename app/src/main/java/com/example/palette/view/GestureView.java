package com.example.palette.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.palette.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 手指轨迹跟踪
 */
public class GestureView extends View {
    Path path;
    Paint paint;
    float pX;
    float pY;
    TouchListener listener;
    int pathColor;
    int pathWidth;
    int bgColor;
    boolean isDraw = false;
    public GestureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GestureView);
        pathColor = typedArray.getColor(R.styleable.GestureView_pathColor,Color.BLACK);
        pathWidth = typedArray.getInteger(R.styleable.GestureView_pathWidth,3);
        bgColor = typedArray.getColor(R.styleable.GestureView_bgColor,Color.WHITE);
        typedArray.recycle();
        init();
    }

    private void init() {
        path = new Path();
        paint = new Paint();
        paint.setColor(pathColor);
        paint.setStrokeWidth(pathWidth);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                pX = event.getX();
                pY = event.getY();
                path.moveTo(event.getX(),event.getY());
                return true;
            case MotionEvent.ACTION_MOVE:
                float x = (pX + event.getX()) / 2;
                float y = (pY + event.getY()) / 2;
                if(Math.abs(x)>1){
                    isDraw = true;
                }
                path.quadTo(pX,pY,x,y);
                pX = event.getX();
                pY = event.getY();
                if(listener!=null){
                    listener.move(x,y);
                }
                postInvalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(bgColor);
        canvas.drawPath(path,paint);
    }

    public Bitmap getCurrentBitmap(){
        Bitmap bitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(bgColor);
        canvas.drawPath(path, paint);
        return bitmap;
    }

    public void autoSaveImage() {
        try {
            Bitmap bitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE);
            canvas.drawPath(path, paint);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            File file=new File(Environment.getExternalStorageDirectory(),simpleDateFormat.format(date)+".jpg");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            outputStream.flush();
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean isDraw() {
        return isDraw;
    }

    public void reset(){
        path.reset();
        isDraw = false;
        postInvalidate();
    }
    public void addTouchListener(TouchListener listener){
        this.listener = listener;
    }
    public interface TouchListener{
        void move(float x,float y);
    }
}
