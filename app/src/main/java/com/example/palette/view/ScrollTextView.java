package com.example.palette.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;
import com.example.palette.bean.TextJson;
import java.util.ArrayList;
import java.util.List;


@SuppressLint("AppCompatCustomView")
public class ScrollTextView extends TextView {
    private static final int scroll_slow = 0;
    private static final int scroll_normal = 1;
    private static final int scroll_fast = 2;
    private static final int direction_left = 0;
    private static final int direction_right = 1;
    private static final int direction_top = 2;
    private static final int direction_bottom = 3;
    private static final String str_left = "left";
    private static final String str_right = "right";
    private static final String str_top = "top";
    private static final String str_bottom = "bottom";
    private String text;
    private int textColor;
    private float textSize;
    private float offX = 0f;
    private float step = 1f;
    private Rect rect = new Rect();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int direction = direction_left;
    private int normalColor;
    private int blinkColor;
    private int blinkTime = 1000;
    private String[] strings;
    private int splitTotalHeight;

    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (direction == direction_left || direction == direction_right) {
            setMeasuredDimension(widthMeasureSpec, rect.height());
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (direction == direction_top || direction == direction_bottom) {
            strings = splitText(text, paint, getWidth());
            splitTotalHeight = strings.length * rect.height();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float x, y;
        if (direction == direction_left) {
            x = getWidth() - offX;
            y = getHeight() / 2 - (rect.top + rect.bottom) / 2;
            canvas.drawText(text, x, y, paint);
            offX += step;
            if (offX >= getWidth() + rect.width()) {
                offX = 0f;
            }
        } else if (direction == direction_right) {
            x = -rect.width() + offX;
            y = getHeight() / 2 - (rect.top + rect.bottom) / 2;
            canvas.drawText(text, x, y, paint);
            offX += step;
            if (offX >= getWidth() + rect.width()) {
                offX = 0f;
            }
        } else if (direction == direction_top) {
            x = 0;
            y = -splitTotalHeight + offX;
            for (int i = strings.length - 1; i >= 0; i--) {
                canvas.drawText(strings[i], x, y, paint);
                y -= rect.height();
            }
            offX += step;
            if (offX >= getHeight() + splitTotalHeight * 2) {
                offX = 0f;
            }
        } else if (direction == direction_bottom) {
            x = 0;
            y = getHeight() - offX;
            for (int i = 0; i < strings.length; i++) {
                canvas.drawText(strings[i], x, y, paint);
                y += rect.height();
            }
            offX += step;
            if (offX >= getHeight() + splitTotalHeight) {
                offX = 0f;
            }
        }
        if (textColor == normalColor) {
            postDelayed(() -> {
                textColor = blinkColor;
                paint.setColor(textColor);
            }, blinkTime);
        } else {
            postDelayed(() -> {
                textColor = normalColor;
                paint.setColor(textColor);
            }, blinkTime);
        }
        invalidate();
    }

    public void setTextJson(TextJson textJson) {
        String rollType = textJson.getRollType();
        if (str_left.equals(rollType)) {
            direction = direction_left;
            setSingleLine(true);
        } else if (str_right.equals(rollType)) {
            direction = direction_right;
            setSingleLine(true);
        } else if (str_top.equals(rollType)) {
            direction = direction_top;
        } else if (str_bottom.equals(rollType)) {
            direction = direction_bottom;
        }
        textSize = textJson.getFontSize();
        text = textJson.getContent();
        normalColor = Color.parseColor(textJson.getFontColor());
        blinkColor = Color.RED;
        textColor = normalColor;
        paint.setColor(textColor);
        paint.setTextSize(textSize);
//        paint.setTypeface(Typeface.createFromAsset(getContext().getAssets(),""));
        paint.getTextBounds(text, 0, text.length(), rect);
    }

    public void setSpeed(int scrollMod) {
        if (scrollMod == scroll_slow) {
            step = 0.5f;
        } else if (scrollMod == scroll_normal) {
            step = 1f;
        } else {
            step = 1.5f;
        }
    }

    private String[] splitText(String text, Paint paint, int width) {
        int length = text.length();
        float twidth = paint.measureText(text);
        if (twidth <= width) {
            return new String[]{text};
        }
        List<String> result = new ArrayList<>();
        int start = 0;
        int end = 1;
        while (start < length) {
            if (paint.measureText(text, start, end) >= width) {
                result.add(text.substring(start, end));
                start = end;
            }
            if (end == length) {
                result.add(text.substring(start, end));
                break;
            }
            end += 1;
        }
        return result.toArray(new String[0]);
    }
}
