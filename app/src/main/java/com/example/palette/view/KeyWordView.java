package com.example.palette.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

import com.example.palette.R;

/**
 * 密码输入框
 */
public class KeyWordView extends AppCompatEditText implements TextWatcher {
    private int mDefaultBgColor = Color.parseColor("#666666");
    private int mDefaultBgWith = 5;
    private int mDefaultPasswordCount = 4;
    private int mDefaultDivisionColor = Color.parseColor("#666666");
    private int mDefaultDivisionWith = 5;
    private int mDefaultPasswordColor = Color.parseColor("#666666");
    private int mDefaultPasswrdSize = 18;

    //背景框的圆角
    private int mBgRadius;
    //画笔
    private Paint mPaint;
    //背景框颜色
    private int mBgColor;
    //背景框线的宽度
    private int mBgWidth;
    //总密码的个数
    private int mPasswordCount;
    //分割线的颜色
    private int mDivisionColor;
    //分割线的宽度
    private int mDivisionWith;
    //密码圆点的颜色
    private int mPasswordColor;
    //密码圆点的大小
    private int mPasswordSize;

    public KeyWordView(Context context) {
        super(context);
        init();
    }

    public KeyWordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        init();
    }

    public KeyWordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        init();
    }

    /**
     * 初始化xml 设置的属性
     *
     * @param context
     * @param attrs
     */
    private void initAttr(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.KeyWordView);
            mBgWidth = (int) typedArray.getDimension(R.styleable.KeyWordView_bg_width, mDefaultBgWith);
            mBgColor = typedArray.getColor(R.styleable.KeyWordView_bg_color, mDefaultBgColor);
            mPasswordCount = typedArray.getInteger(R.styleable.KeyWordView_password_count, mDefaultPasswordCount);
            mDivisionColor = typedArray.getColor(R.styleable.KeyWordView_division_color, mDefaultDivisionColor);
            mDivisionWith = (int) typedArray.getDimension(R.styleable.KeyWordView_division_width, mDefaultDivisionWith);
            mPasswordColor = typedArray.getColor(R.styleable.KeyWordView_password_color, mDefaultPasswordColor);
            mPasswordSize = (int) typedArray.getDimension(R.styleable.KeyWordView_password_size, mDefaultPasswrdSize);
            mBgRadius = (int) typedArray.getDimension(R.styleable.KeyWordView_bg_radius, 0);
            typedArray.recycle();
        }
    }

    /**
     * 初始化画笔
     */
    private void init() {
        mPaint = new Paint();
        //抗锯齿
        mPaint.setAntiAlias(true);
        //防抖动
        mPaint.setDither(true);
        //设置输入最大长度
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(mPasswordCount)});
        //设置只能输入数字
        setInputType(InputType.TYPE_CLASS_NUMBER);
        //监听文本变化，回调
        addTextChangedListener(this);
        setBackground(null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制背景
        drawBg(canvas);
        //绘制分割线
        drawDivision(canvas);
        //绘制圆点密码
        drawPassword(canvas);
    }

    /**
     * 绘制圆点密码
     *
     * @param canvas
     */
    private void drawPassword(Canvas canvas) {
        //一个格子的长度
        int itemWith = (getMeasuredWidth() - (2 * mBgWidth)) / mPasswordCount;
        mPaint.setColor(mPasswordColor);
        mPaint.setStrokeWidth(mPasswordSize);
        mPaint.setStyle(Paint.Style.FILL);
        String text = getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {
            for (int i = 0; i < text.length(); i++) {
                float cx = mBgWidth + (itemWith / 2) + itemWith * i;
                float cy = getMeasuredHeight() / 2;
                canvas.drawCircle(cx, cy, mPasswordSize, mPaint);
            }
        }
    }

    /**
     * 绘制分割线
     *
     * @param canvas
     */
    private void drawDivision(Canvas canvas) {
        int itemWith = (getMeasuredWidth() - (2 * mBgWidth)) / mPasswordCount;
        mPaint.setColor(mDivisionColor);
        mPaint.setStrokeWidth(mDivisionWith);
        for (int i = 0; i < mPasswordCount - 1; i++) {
            float startX = mBgWidth + (itemWith * (i + 1));
            float startY = 0;
            float endY = startY + getMeasuredHeight();
            canvas.drawLine(startX, startY, startX, endY, mPaint);
        }
    }

    /**
     * 绘制背景
     *
     * @param canvas
     */
    private void drawBg(Canvas canvas) {
        RectF rectF = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
        mPaint.setColor(mBgColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mBgWidth);
        canvas.drawRoundRect(rectF, mBgRadius, mBgRadius, mPaint);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        //当输入密码为最长的时候 回调
        if (!TextUtils.isEmpty(s) && s.length() == mPasswordCount && this.listener != null) {
            this.listener.full(s.toString().trim());
        }
    }

    private IPasswordFullListener listener;

    public void setPasswordFullListener(IPasswordFullListener listener) {
        this.listener = listener;
    }

    public interface IPasswordFullListener {
        void full(String password);
    }
}
