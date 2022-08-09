package com.example.palette.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.annotation.Nullable;
import com.example.palette.R;
import com.example.palette.util.ScreenUtil;
import java.util.ArrayList;
import java.util.List;

public class TemperatureView extends View {
    int defaultSize = 1050;
    int defaultPaintStrokeWidth = 3;
    float defaultLongSize;
    float defaultShortSize;
    float maxCircleRadius;
    float rotateAngle;
    int temperature;
    int minTemp = -30;
    int maxTemp = 30;
    int middleTemp = 0;
    int precent2Temp = 20;
    int precent02Temp = -20;
    int precent1Temp = 10;
    int precent01Temp = -10;
    int min;
    Paint arcPaint;
    Paint sizePaint;
    Paint numberPaint;
    Paint numberSizePaint;
    Paint huanPaint;
    Paint twoLinePaint;
    Paint circlePaint;
    Paint centerCirclePaint;
    Paint smallCirclePaint;
    Paint bottomTextPaint;
    List<Integer> paintColors;
    List<Integer> tempColors;
    String stringZD = "中等";
    String stringY = "优";
    String stringL = "良";
    String stringGR = "过热";
    String stringWX = "危险";
    String stringLKWDZK = "冷库温度状况:";
    String stringCurrentState;
    RectF arcRectF;
    LinearGradient linearGradient;
    public TemperatureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TemperatureView);
        temperature= typedArray.getInteger(R.styleable.TemperatureView_currentTemp,0);
        typedArray.recycle();
        initColors();
        initPaints();
    }

    private void initPaints() {
        arcRectF = new RectF();

        arcPaint = new Paint();
        arcPaint.setAntiAlias(true);
        arcPaint.setStrokeWidth(defaultPaintStrokeWidth);
        arcPaint.setColor(paintColors.get(1));
        arcPaint.setStyle(Paint.Style.STROKE);

        sizePaint = new Paint();
        sizePaint.setAntiAlias(true);
        sizePaint.setStrokeWidth(defaultPaintStrokeWidth);
        sizePaint.setColor(paintColors.get(1));
        sizePaint.setStyle(Paint.Style.STROKE);

        numberPaint = new Paint();
        numberPaint.setAntiAlias(true);
        numberPaint.setStrokeWidth(defaultPaintStrokeWidth);
        numberPaint.setColor(paintColors.get(0));
        numberPaint.setStyle(Paint.Style.FILL);

        numberSizePaint = new Paint();
        numberSizePaint.setAntiAlias(true);
        numberSizePaint.setStrokeWidth(defaultPaintStrokeWidth);
        numberSizePaint.setColor(paintColors.get(2));
        numberSizePaint.setStyle(Paint.Style.FILL);

        huanPaint = new Paint();
        huanPaint.setColor(tempColors.get(0));
        huanPaint.setStyle(Paint.Style.FILL);

        twoLinePaint = new Paint();
        twoLinePaint.setAntiAlias(true);
        twoLinePaint.setStrokeWidth(defaultPaintStrokeWidth);
        twoLinePaint.setColor(paintColors.get(1));
        twoLinePaint.setStyle(Paint.Style.STROKE);

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.FILL);

        centerCirclePaint = new Paint();
        centerCirclePaint.setAntiAlias(true);
        centerCirclePaint.setStyle(Paint.Style.STROKE);

        smallCirclePaint = new Paint();
        smallCirclePaint.setAntiAlias(true);
        smallCirclePaint.setStyle(Paint.Style.FILL);

        bottomTextPaint = new Paint();
        bottomTextPaint.setAntiAlias(true);
        bottomTextPaint.setStrokeWidth(3);
        bottomTextPaint.setColor(paintColors.get(5));
        bottomTextPaint.setStyle(Paint.Style.FILL);
    }

    private void initColors() {
        paintColors = new ArrayList<>();
        paintColors.add(Color.parseColor("#6f6f6f"));
        paintColors.add(Color.parseColor("#ebebeb"));
        paintColors.add(Color.parseColor("#cccccc"));
        paintColors.add(Color.parseColor("#f4f3f3"));
        paintColors.add(Color.parseColor("#E27A3F"));
        paintColors.add(Color.parseColor("#b3b3b3"));
        paintColors.add(Color.parseColor("#FFFFFF"));
        paintColors.add(Color.parseColor("#e7e7e7"));
        tempColors = new ArrayList<>();
        tempColors.add(Color.parseColor("#46a012"));//-30
        tempColors.add(Color.parseColor("#46a012"));//-29
        tempColors.add(Color.parseColor("#46a012"));//-28
        tempColors.add(Color.parseColor("#46a012"));//-27
        tempColors.add(Color.parseColor("#46a012"));//-26
        tempColors.add(Color.parseColor("#46a012"));//-25
        tempColors.add(Color.parseColor("#46a012"));//-24
        tempColors.add(Color.parseColor("#5bab10"));//-23
        tempColors.add(Color.parseColor("#5bab10"));//-22
        tempColors.add(Color.parseColor("#84c40c"));//-21
        tempColors.add(Color.parseColor("#84c40c"));//-20
        tempColors.add(Color.parseColor("#9ed30b"));//-19
        tempColors.add(Color.parseColor("#b1de08"));//-18
        tempColors.add(Color.parseColor("#b1de08"));//-17
        tempColors.add(Color.parseColor("#c3e806"));//-16
        tempColors.add(Color.parseColor("#d6ef07"));//-15
        tempColors.add(Color.parseColor("#d6ef07"));//-14
        tempColors.add(Color.parseColor("#e7f103"));//-13
        tempColors.add(Color.parseColor("#e7f103"));//-12
        tempColors.add(Color.parseColor("#f1ef0c"));//-11
        tempColors.add(Color.parseColor("#fbef02"));//-10
        tempColors.add(Color.parseColor("#fbef02"));//-9
        tempColors.add(Color.parseColor("#fde101"));//-8
        tempColors.add(Color.parseColor("#fad50c"));//-7
        tempColors.add(Color.parseColor("#fdcb02"));//-6
        tempColors.add(Color.parseColor("#fcaa02"));//-5
        tempColors.add(Color.parseColor("#fd9901"));//-4
        tempColors.add(Color.parseColor("#fd9901"));//-3
        tempColors.add(Color.parseColor("#fd8401"));//-2
        tempColors.add(Color.parseColor("#fd7001"));//-1
        tempColors.add(Color.parseColor("#fd7001"));//0
        tempColors.add(Color.parseColor("#fd6301"));//1
        tempColors.add(Color.parseColor("#fc5601"));//2
        tempColors.add(Color.parseColor("#fb4801"));//3
        tempColors.add(Color.parseColor("#fb3701"));//4
        tempColors.add(Color.parseColor("#fd2101"));//5
        tempColors.add(Color.parseColor("#fc0f01"));//6
        tempColors.add(Color.parseColor("#f90301"));//7
        tempColors.add(Color.parseColor("#f90301"));//8
        tempColors.add(Color.parseColor("#f90301"));//9
        tempColors.add(Color.parseColor("#f90301"));//10
        tempColors.add(Color.parseColor("#f90301"));//11
        tempColors.add(Color.parseColor("#e80301"));//12
        tempColors.add(Color.parseColor("#d70301"));//13
        tempColors.add(Color.parseColor("#d70301"));//14
        tempColors.add(Color.parseColor("#c40401"));//15
        tempColors.add(Color.parseColor("#c40401"));//16
        tempColors.add(Color.parseColor("#b30602"));//17
        tempColors.add(Color.parseColor("#b30602"));//18
        tempColors.add(Color.parseColor("#97080c"));//19
        tempColors.add(Color.parseColor("#97080c"));//20
        tempColors.add(Color.parseColor("#8c0b1b"));//21
        tempColors.add(Color.parseColor("#8c0b1b"));//22
        tempColors.add(Color.parseColor("#850f2d"));//23
        tempColors.add(Color.parseColor("#850f2d"));//24
        tempColors.add(Color.parseColor("#7e103a"));//25
        tempColors.add(Color.parseColor("#7e103a"));//26
        tempColors.add(Color.parseColor("#7b1242"));//27
        tempColors.add(Color.parseColor("#7b1242"));//28
        tempColors.add(Color.parseColor("#7b1242"));//29
        tempColors.add(Color.parseColor("#7b1242"));//30
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
            width = defaultSize;
        }
        if(heightMode==MeasureSpec.EXACTLY){
            height = heightSize;
        }else {
            height = defaultSize;
        }
//        if(width>ScreenUtil.getWidthPx(getContext())){
//            width = ScreenUtil.getWidthPx(getContext());
//        }
//        if(height>ScreenUtil.getHeightPx(getContext())){
//            height=ScreenUtil.getHeightPx(getContext());
//        }
        min = Math.min(width, height);

        setMeasuredDimension(min,min);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        defaultLongSize = 68f*getWidth()/1050f/2;
        defaultShortSize = 68f*getWidth()/1050f/4;
        maxCircleRadius = (min - 68f*getWidth()/1050f*2 - defaultLongSize*2)/2;
        arcRectF.left = -maxCircleRadius;
        arcRectF.top = -maxCircleRadius;
        arcRectF.right = maxCircleRadius;
        arcRectF.bottom = maxCircleRadius;
        if(linearGradient==null){
            linearGradient = new LinearGradient(getWidth()/2,getHeight()/2-maxCircleRadius+74f*getWidth()/1050f+36f*getWidth()/1050f,
                    getWidth()/2,getHeight()/2+maxCircleRadius-74f*getWidth()/1050f-36f*getWidth()/1050f,paintColors.get(6), paintColors.get(7), Shader.TileMode.MIRROR);
        }
        arcDraw(canvas);
        sizeDraw(canvas);
        textDraw(canvas);
        huanDraw(canvas);
        twoLineDraw(canvas);
        circleDraw(canvas);
        bitmapDraw(canvas);
        smallCircleDraw(canvas);
        bottomTextDraw(canvas);
    }

    private void bottomTextDraw(Canvas canvas) {
        canvas.save();
        bottomTextPaint.setTextSize(39f*min/1050f);
        canvas.drawText(stringLKWDZK,284f*getWidth()/1050f,
                (float) (getHeight()/2+Math.sin(Math.toRadians(45))*(maxCircleRadius+defaultLongSize)+30f*getWidth()/1050f*2),bottomTextPaint);
        if (temperature < middleTemp) {
            if (temperature < minTemp) {
                temperature = minTemp;
            }
            int abs = Math.abs(temperature);
            bottomTextPaint.setColor(tempColors.get((int) (30f*(Math.abs(minTemp) - abs)/ Math.abs(minTemp))));
            if (temperature <= precent02Temp) {
                stringCurrentState = stringY;
            } else if (temperature <= precent01Temp) {
                stringCurrentState = stringL;
            } else {
                stringCurrentState = stringZD;
            }
        } else if (temperature == middleTemp) {
            bottomTextPaint.setColor(tempColors.get(30));
            stringCurrentState = stringZD;
        } else {
            if (temperature > maxTemp) {
                temperature = maxTemp;
            }
            if (temperature >= precent2Temp) {
                stringCurrentState = stringWX;
            } else if (temperature >= precent1Temp) {
                stringCurrentState = stringGR;
            } else {
                stringCurrentState = stringZD;
            }
            bottomTextPaint.setColor(tempColors.get(60 - (int) (30f*(maxTemp - temperature)/ maxTemp)));
        }
        canvas.drawRect(getWidth()/2+36f*getWidth()/1050f/2,
                (float) (getHeight()/2+Math.sin(Math.toRadians(45))*(maxCircleRadius+defaultLongSize)+30f*getWidth()/1050f),
                getWidth()/2+74f*getWidth()/1050f+36f*getWidth()/1050f/2,
                (float) (getHeight()/2+Math.sin(Math.toRadians(45))*(maxCircleRadius+defaultLongSize)+67f*getWidth()/1050f),bottomTextPaint);
        bottomTextPaint.setColor(paintColors.get(5));
        canvas.drawText(stringCurrentState, getWidth()/2+74f*getWidth()/1050f+36f*getWidth()/1050f,
                (float) (getHeight()/2+Math.sin(Math.toRadians(45))*(maxCircleRadius+defaultLongSize)+30f*getWidth()/1050f*2), bottomTextPaint);
        canvas.restore();
    }

    private void smallCircleDraw(Canvas canvas) {
        canvas.save();
        caculateAngle(temperature);
        canvas.translate(getWidth()/2,getHeight()/2);
        smallCirclePaint.setTextSize((maxCircleRadius-74f*getWidth()/1050f*2-36f*getWidth()/1050f*3));
        float tempWidth = smallCirclePaint.measureText(String.valueOf(temperature));
        float tempHeight = 0.5f * (smallCirclePaint.ascent() + smallCirclePaint.descent());
        if (temperature < middleTemp) {
            if (temperature < minTemp) {
                temperature = minTemp;
            }
            int abs = Math.abs(temperature);
            smallCirclePaint.setColor(tempColors.get((int) (30f*(Math.abs(minTemp) - abs) / Math.abs(minTemp))));
        } else if (temperature == 0) {
            smallCirclePaint.setColor(tempColors.get(30));
        } else {
            if (temperature > maxTemp) {
                temperature = maxTemp;
            }
            smallCirclePaint.setColor(tempColors.get(60 - (int) (30f*(maxTemp - temperature) / maxTemp)));
        }
        canvas.drawText(temperature+"°",-tempWidth/2,-tempHeight,smallCirclePaint);
        canvas.rotate(rotateAngle);
        canvas.drawCircle(0,-(maxCircleRadius-74f*getWidth()/1050f*2-36f*getWidth()/1050f*3/2),(36f*getWidth()/1050f/2)*0.7f,smallCirclePaint);
        canvas.restore();
    }

    private void bitmapDraw(Canvas canvas) {
        canvas.save();
        centerCirclePaint.setColor(paintColors.get(3));
        centerCirclePaint.setStrokeWidth(defaultPaintStrokeWidth*2);
        canvas.drawCircle(getWidth()/2,getHeight()/2,maxCircleRadius-74f*getWidth()/1050f*2-36f*getWidth()/1050f,centerCirclePaint);
        centerCirclePaint.setColor(paintColors.get(6));
        centerCirclePaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getWidth()/2,getHeight()/2,maxCircleRadius-74f*getWidth()/1050f*2-36f*getWidth()/1050f*2,centerCirclePaint);
        canvas.restore();
    }

    private void circleDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth()/2,getHeight()/2);
        circlePaint.setShader(linearGradient);
        canvas.drawCircle(0,0,maxCircleRadius-74f*getWidth()/1050f-36f*getWidth()/1050f,circlePaint);
        canvas.drawCircle(0,0,maxCircleRadius-74f*getWidth()/1050f-36f*getWidth()/1050f,arcPaint);
        canvas.restore();
    }

    private void twoLineDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth()/2,getHeight()/2);
        canvas.rotate(-135f);
        canvas.drawLine(0,-maxCircleRadius,0,-maxCircleRadius+36f*getWidth()/1050f,twoLinePaint);
        canvas.rotate(-90f);
        canvas.drawLine(0,-maxCircleRadius,0,-maxCircleRadius+36f*getWidth()/1050f,twoLinePaint);
        canvas.restore();
    }

    private void huanDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth()/2,getHeight()/2);
        canvas.rotate(45f);
        huanPaint.setColor(tempColors.get(60));
        canvas.drawRect(maxCircleRadius-36f*getWidth()/1050f-74f*getWidth()/1050f,-37f*getWidth()/1050f/2,maxCircleRadius-36f*getWidth()/1050f,0,huanPaint);
        for(int j=59;j>0;j--){
            canvas.rotate(-4.5f);
            huanPaint.setColor(tempColors.get(j));
            canvas.drawRect(maxCircleRadius-36f*getWidth()/1050f-74f*getWidth()/1050f, -37f*getWidth()/1050f/2, maxCircleRadius-36f*getWidth()/1050f, 37f*getWidth()/1050f/2, huanPaint);
        }
        canvas.rotate(-4.5f);
        huanPaint.setColor(tempColors.get(0));
        canvas.drawRect(maxCircleRadius-36f*getWidth()/1050f-74f*getWidth()/1050f, 0, maxCircleRadius-36f*getWidth()/1050f, 37f*getWidth()/1050f/2, huanPaint);
        canvas.restore();
    }

    private void textDraw(Canvas canvas) {
        canvas.save();
        numberPaint.setTextSize(39f*getWidth()/1050f);
        numberSizePaint.setTextSize(31f*getWidth()/1050f);
        canvas.drawText(minTemp+"°", (float) (getWidth()/2-Math.sin(Math.toRadians(45))*(maxCircleRadius+defaultLongSize)-68f*getWidth()/1050f), (float) (getHeight()/2+Math.sin(Math.toRadians(45))*(maxCircleRadius+defaultLongSize)+30f*getWidth()/1050f),numberPaint);
        canvas.drawText(precent02Temp+"°",getWidth()/2-maxCircleRadius-defaultLongSize-68f*getWidth()/1050f,getHeight()/2+30f*getWidth()/1050f/2,numberPaint);
        canvas.drawText(precent01Temp+"°", (float) (getWidth()/2-Math.sin(Math.toRadians(45))*(maxCircleRadius+defaultLongSize)-68f*getWidth()/1050f), (float) (getHeight()/2-Math.sin(Math.toRadians(45))*(maxCircleRadius+defaultLongSize)),numberPaint);
        canvas.drawText(middleTemp+"°",getWidth()/2-33f*getWidth()/1050f/2,getHeight()/2-maxCircleRadius-defaultLongSize,numberPaint);
        canvas.drawText(precent1Temp+"°",(float) (getWidth()/2+Math.sin(Math.toRadians(45))*(maxCircleRadius+defaultLongSize)),(float) (getHeight()/2-Math.sin(Math.toRadians(45))*(maxCircleRadius+defaultLongSize)),numberPaint);
        canvas.drawText(precent2Temp+"°",getWidth()/2+maxCircleRadius+defaultLongSize,getHeight()/2+30f*getWidth()/1050f/2,numberPaint);
        canvas.drawText(maxTemp+"°", (float) (getWidth()/2+Math.sin(Math.toRadians(45))*(maxCircleRadius+defaultLongSize)), (float) (getHeight()/2+Math.sin(Math.toRadians(45))*(maxCircleRadius+defaultLongSize)+30f*getWidth()/1050f),numberPaint);
        canvas.drawText(stringY,(float)(getWidth()/2-Math.sin(Math.toRadians(67.5))*(maxCircleRadius+defaultShortSize)-36f*getWidth()/1050f),(float)(getHeight()/2+Math.cos(Math.toRadians(67.5))*(maxCircleRadius+defaultShortSize)+37f*getWidth()/1050f/2),numberSizePaint);
        canvas.drawText(stringL,(float)(getWidth()/2-Math.sin(Math.toRadians(67.5))*(maxCircleRadius+defaultShortSize)-36f*getWidth()/1050f),(float)(getHeight()/2-Math.cos(Math.toRadians(67.5))*(maxCircleRadius+defaultShortSize)),numberSizePaint);
        canvas.drawText(stringZD,(float)(getWidth()/2-Math.sin(Math.toRadians(22.5))*(maxCircleRadius+defaultShortSize)-36f*getWidth()/1050f),(float)(getHeight()/2-Math.cos(Math.toRadians(22.5))*(maxCircleRadius+defaultShortSize)-37f*getWidth()/1050f/2),numberSizePaint);
        canvas.drawText(stringZD,(float)(getWidth()/2+Math.sin(Math.toRadians(22.5))*(maxCircleRadius+defaultShortSize)-36f*getWidth()/1050f/2),(float)(getHeight()/2-Math.cos(Math.toRadians(22.5))*(maxCircleRadius+defaultShortSize)-37f*getWidth()/1050f/2),numberSizePaint);
        canvas.drawText(stringGR,(float)(getWidth()/2+Math.sin(Math.toRadians(67.5))*(maxCircleRadius+defaultShortSize)),(float)(getHeight()/2-Math.cos(Math.toRadians(67.5))*(maxCircleRadius+defaultShortSize)),numberSizePaint);
        canvas.drawText(stringWX,(float)(getWidth()/2+Math.sin(Math.toRadians(67.5))*(maxCircleRadius+defaultShortSize)),(float)(getHeight()/2+Math.cos(Math.toRadians(67.5))*(maxCircleRadius+defaultShortSize)+37f*getWidth()/1050f/2),numberSizePaint);
        canvas.restore();
    }

    private void sizeDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth()/2, getHeight()/2);
        canvas.rotate(-120f);
        sizePaint.setTextSize(39f*getWidth()/1050f);
        for (int i = 1; i < 18; i++) {
            if (i % 3 == 0) {
                canvas.drawLine(0, -maxCircleRadius, 0, -maxCircleRadius - defaultLongSize*getWidth()/1050f,sizePaint);
            } else {
                canvas.drawLine(0, -maxCircleRadius, 0, -maxCircleRadius - defaultShortSize*getWidth()/1050f,sizePaint);
            }
            canvas.rotate(15f);
        }
        canvas.restore();
    }

    private void arcDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth()/2,getHeight()/2);
        canvas.rotate(135f);
        canvas.drawArc(arcRectF, 0, 270f, false, arcPaint);
        canvas.restore();
    }

    private void caculateAngle(int currentTemp) {
        if (currentTemp < middleTemp) {
            if (currentTemp < minTemp) {
                rotateAngle = -135;
            } else {
                rotateAngle = 135*((float)currentTemp/(float) Math.abs(minTemp));
            }
        } else if (currentTemp == middleTemp) {
            rotateAngle = 0;
        } else {
            if (currentTemp > maxTemp) {
                rotateAngle = 135;
            } else {
                rotateAngle = 135*((float)currentTemp/(float)maxTemp);
            }
        }
    }

    public void setTemperature(int temp) {
        temperature = temp;
        postInvalidate();
    }
    public void setMaxAndMinTemp(float max,float min){
        maxTemp = (int) max;
        precent2Temp = (int) (max*2f/3f);
        precent1Temp = (int) (max/3f);
        minTemp = (int) min;
        precent02Temp = (int) (min*2f/3f);
        precent01Temp = (int) (min/3f);
        postInvalidate();
    }
}
