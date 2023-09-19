package com.example.palette.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;

import com.example.palette.R;
import com.example.palette.util.ScreenUtil;

import java.text.DecimalFormat;

/**
 * 水平进度条
 * style="?android:attr/progressBarStyleHorizontal"
 */
public class HorizontalProgressbar extends ProgressBar {
    private int mProgress;
    private DecimalFormat decimalFormat;
    private Rect rect;
    private Paint paint;
    private int TEXT_SIZE_SP = 20;
    private int width;
    private int height;
    private float paintTextSize;
    private PorterDuffXfermode porterDuffXfermode;
    private RectF rectF;
    private int color;
    private Canvas bufferCanvas;
    public HorizontalProgressbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        decimalFormat = new DecimalFormat("#0");
        rect = new Rect();
        paint = new Paint();
        porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        color = Color.parseColor("#45C2D8");
        paint.setColor(color);
        paint.setAntiAlias(true);
        setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.bg_rectangle_blue_border_corner));
        setMax(100);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if(widthMode==MeasureSpec.EXACTLY){
            width = widthSize;
        }else {
            width = ScreenUtil.getWidthPx(getContext());
        }
        if(width>ScreenUtil.getWidthPx(getContext())){
            width = ScreenUtil.getWidthPx(getContext());
        }
        if(heightMode==MeasureSpec.EXACTLY){
            height = heightSize;
        }else {
            height = ScreenUtil.sp2px(getContext(),TEXT_SIZE_SP)+ScreenUtil.dp2px(getContext(),2)*2;
        }
        paintTextSize = height-ScreenUtil.dp2px(getContext(),2)*4;
        setMeasuredDimension(width,height);
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        mProgress = progress;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bufferCanvas = new Canvas();
        rectF = new RectF();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String text = getProgressText(mProgress);
        paint.setTextSize(paintTextSize);
        paint.getTextBounds(text,0,text.length(),rect);
        paint.setColor(color);

        Bitmap bufferBitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
        bufferCanvas.setBitmap(bufferBitmap);
        bufferCanvas.drawText(text,getWidth()/2-rect.centerX(),getHeight()/2-rect.centerY(),paint);
        paint.setXfermode(porterDuffXfermode);
        paint.setColor(Color.WHITE);
        rectF.set(0,0,getWidth()*mProgress/100,getHeight());
        bufferCanvas.drawRect(rectF,paint);
        canvas.drawBitmap(bufferBitmap,0,0,null);
        paint.setXfermode(null);
        if(!bufferBitmap.isRecycled()){
            bufferBitmap.recycle();
        }
    }

    private String getProgressText(int progress){
       return decimalFormat.format(progress)+"%";
    }
}
