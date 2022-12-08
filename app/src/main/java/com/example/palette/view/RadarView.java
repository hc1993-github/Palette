package com.example.palette.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import com.example.palette.R;
import com.example.palette.util.ScreenUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class RadarView extends View {
    private Paint circlePaint;
    private Paint gradientPaint;
    private Paint rainDropPaint;
    private float degree = 0;
    private float speed;
    private List<RainDrop> rainDrops = new ArrayList<>();
    private int circleNum;
    private int gradientColor;
    private int raindropColor;
    private int circleColor;
    private int raindropNum;
    private int raindropMaxWidth;
    private boolean showCross;
    private boolean showRaindrop;
    private int flicker;
    private boolean pause = false;
    public RadarView(Context context,AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RadarView);
        circleColor = typedArray.getColor(R.styleable.RadarView_circleColor,Color.RED);
        circleNum = typedArray.getInteger(R.styleable.RadarView_circleNum, 3);
        gradientColor = typedArray.getColor(R.styleable.RadarView_gradientColor, Color.RED);
        raindropColor = typedArray.getColor(R.styleable.RadarView_raindropColor,Color.RED);
        raindropNum = typedArray.getInteger(R.styleable.RadarView_raindropNum,3);
        showCross = typedArray.getBoolean(R.styleable.RadarView_showCross,true);
        showRaindrop = typedArray.getBoolean(R.styleable.RadarView_showRaindrop,true);
        speed = typedArray.getFloat(R.styleable.RadarView_speed,3);
        flicker = typedArray.getInteger(R.styleable.RadarView_flicker,3);
        raindropMaxWidth = typedArray.getInteger(R.styleable.RadarView_raindropMaxWidth,50);
        typedArray.recycle();
        init();
    }

    private void init() {
        circlePaint = new Paint();
        circlePaint.setColor(circleColor);
        circlePaint.setStrokeWidth(2);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setAntiAlias(true);
        gradientPaint = new Paint();
        gradientPaint.setAntiAlias(true);
        rainDropPaint = new Paint();
        rainDropPaint.setStyle(Paint.Style.FILL);
        rainDropPaint.setAntiAlias(true);
        setOnClickListener(v -> {
            if(pause){
                pause = false;
            }else {
                pause = true;
            }
            postInvalidate();
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int defaultsize = ScreenUtil.dp2px(getContext(),200);
        int width = measureWidth(widthMeasureSpec,defaultsize);
        int height = measureHeight(heightMeasureSpec,defaultsize);
        setMeasuredDimension(width,height);
    }

    private int measureHeight(int heightMeasureSpec, int defaultsize) {
        int result = 0;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if(mode==MeasureSpec.EXACTLY){
            result = size;
        }else {
            result = getPaddingTop()+getPaddingBottom()+defaultsize;
        }
        return result;
    }

    private int measureWidth(int widthMeasureSpec,int defaultsize) {
        int result = 0;
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        if(mode==MeasureSpec.EXACTLY){ //固定值或match_parent
            result = size;
        }else { //wrap_content
            result = getPaddingLeft()+getPaddingRight()+defaultsize;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth() - getPaddingRight() - getPaddingLeft();
        int height = getHeight() - getPaddingTop() - getPaddingBottom();
        int radius = Math.min(width,height)/2; //计算圆半径
        int cx = getPaddingLeft()+width/2;
        int cy = getPaddingTop()+height/2;
        drawCircle(canvas,cx,cy,radius);
        if(showCross){
            drawCross(canvas,cx,cy,radius);
        }
        if(showRaindrop){
            drawRainDrop(canvas,cx,cy,radius);
        }
        drawGradient(canvas,cx,cy,radius);
        degree = (degree + speed) % 360;
        if(!pause){
            invalidate();
        }
    }

    public void setPause(boolean pause){
        this.pause = pause;
        postInvalidate();
    }

    private void drawCross(Canvas canvas, int cx, int cy, int radius) {
        canvas.drawLine(cx,cy-radius,cx,cy+radius,circlePaint);
        canvas.drawLine(cx-radius,cy,cx+radius,cy,circlePaint);
    }

    private void drawRainDrop(Canvas canvas, int cx, int cy, int radius) {
        generateRainDrops(cx,cy,radius);
        for(RainDrop rainDrop:rainDrops){
            rainDropPaint.setColor(rainDrop.changeAlpha());
            canvas.drawCircle(rainDrop.x,rainDrop.y,rainDrop.radius,rainDropPaint);
            rainDrop.radius+=1.0f * 20 / 60 / flicker;
            rainDrop.alpha-=1.0f * 255 / 60 / flicker;
        }
        removeRainDrop();
    }

    private void removeRainDrop() {
        Iterator<RainDrop> rainDropIterator = rainDrops.iterator();
        while (rainDropIterator.hasNext()){
            RainDrop rainDrop = rainDropIterator.next();
            if(rainDrop.radius> raindropMaxWidth || rainDrop.alpha<0){
                rainDropIterator.remove();
            }
        }
    }

    private void generateRainDrops(int cx, int cy, int radius) {
        if(rainDrops.size()<raindropNum){
            boolean generate = (int)Math.random()*20==0;
            if(generate){
                int x = 0;
                int y = 0;
                int xOffset = (int) (Math.random() * (radius - 20));
                int yOffset = (int) (Math.random() * (int) Math.sqrt(1.0 * (radius - 20) * (radius - 20) - xOffset * xOffset));

                if ((int) (Math.random() * 2) == 0) {
                    x = cx - xOffset;
                } else {
                    x = cx + xOffset;
                }

                if ((int) (Math.random() * 2) == 0) {
                    y = cy - yOffset;
                } else {
                    y = cy + yOffset;
                }
                rainDrops.add(new RainDrop(x,y,0,raindropColor));
            }
        }
    }

    private void drawGradient(Canvas canvas, int cx, int cy, int radius) {
        SweepGradient sweepGradient = new SweepGradient(cx,cy,new int[]{Color.TRANSPARENT,
                Color.argb(0,Color.red(gradientColor),Color.green(gradientColor),Color.blue(gradientColor)),
                Color.argb(255,Color.red(gradientColor),Color.green(gradientColor),Color.blue(gradientColor)),
                Color.argb(255,Color.red(gradientColor),Color.green(gradientColor),Color.blue(gradientColor))},
                new float[]{0f,0.6f,0.99f,1f});
        gradientPaint.setShader(sweepGradient);
        canvas.rotate(-90+degree,cx,cy);
        canvas.drawCircle(cx,cy,radius,gradientPaint);
    }

    private void drawCircle(Canvas canvas, int cx, int cy, int radius) {
        int dex = radius / circleNum;
        for(int i=0;i<circleNum;i++){
            canvas.drawCircle(cx,cy,radius,circlePaint);
            radius-=dex;
        }
    }

    protected static class RainDrop{
        int x;
        int y;
        float radius;
        int color;
        int alpha = 255;
        public RainDrop(int x, int y, float radius, int color) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.color = color;
        }

        public int changeAlpha(){
            return Color.argb(alpha,Color.red(color),Color.green(color),Color.blue(color));
        }
    }
}
