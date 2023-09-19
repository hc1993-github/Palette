package com.example.palette.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palette.R;
import com.example.palette.adapter.BaseAdapter;
import com.example.palette.adapter.BaseHolder;
import com.example.palette.util.ScreenUtil;

import java.util.List;

/**
 * 下拉选择
 */
public class DownPullView extends LinearLayout {
    private int itemLimit;
    private int selfLayout;
    private int downpullLayout;
    private int itemLayout;
    private TextView tv_content;
    private DownPullPopupWindow popupWindow;
    private List<String> datas;
    private int tvColor;
    private int tvSize;
    private DownPullListener listener;
    private Paint paint;
    private Rect rect;
    private String strDefault;
    public DownPullView(Context context,AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        rect = new Rect();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DownPullView);
        itemLimit = typedArray.getInteger(R.styleable.DownPullView_itemLimit,1);
        selfLayout = R.layout.downpull_default_selflayout;
        downpullLayout = R.layout.downpull_default_downlayout;
        itemLayout = R.layout.downpull_default_itemlayout;
        tvColor = typedArray.getColor(R.styleable.DownPullView_tvColor, Color.BLACK);
        tvSize = typedArray.getInteger(R.styleable.DownPullView_tvSizeSp,14);
        strDefault = typedArray.getString(R.styleable.DownPullView_tvDefault)==null?"请选择":typedArray.getString(R.styleable.DownPullView_tvDefault);
        typedArray.recycle();
        paint.setTextSize(ScreenUtil.sp2px(context,tvSize));
        paint.getTextBounds(strDefault,0,strDefault.length(),rect);
        initViews();
    }

    private void initViews() {
        View selftlayout = LayoutInflater.from(getContext()).inflate(selfLayout, this, true);
        tv_content = selftlayout.findViewById(R.id.tv_content);
        tv_content.setTextColor(tvColor);
        tv_content.setTextSize(tvSize);
        tv_content.setText(strDefault);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(popupWindow==null){
                    open();
                }else {
                    close();
                }
            }
        });
    }

    private void open() {
        int result;
        if(datas.size()<itemLimit){
            result = datas.size();
        }else {
            result = itemLimit;
        }
        popupWindow = new DownPullPopupWindow(getContext(),downpullLayout,result);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(this);
    }

    private void close() {
        popupWindow.dismiss();
        popupWindow = null;
    }

    public void setPosition(int position){
        if(position>datas.size() || position < 0){
            position = 0;
        }
        if(datas==null || datas.isEmpty()){
            tv_content.setText(strDefault);
        }else {
            tv_content.setText(datas.get(position));
        }
    }

    public void setDatas(List<String> datas){
        if(datas.size()<1){
            throw new RuntimeException("至少需包含1条数据");
        }
        this.datas = datas;
    }

    public void setListener(DownPullListener listener) {
        this.listener = listener;
    }

    public interface DownPullListener{
        void onItemClick(int position);
    }
    private class DownPullPopupWindow extends PopupWindow{

        public DownPullPopupWindow(Context context,int layoutId,int i) {
            initView(context,layoutId,i);
        }

        private void initView(Context context,int layoutId,int i) {
            View view = LayoutInflater.from(context).inflate(layoutId, null, false);
            setContentView(view);
            setWidth(getMeasuredWidth());
            setHeight(getMeasuredHeight()*i);
            RecyclerView recyclerView = view.findViewById(R.id.rv_datas);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            DownPullAdapter adapter = new DownPullAdapter(context,itemLayout,datas);
            recyclerView.setAdapter(adapter);
        }
    }

    private class DownPullAdapter extends BaseAdapter<String>{

        public DownPullAdapter(Context context, int layoutId, List<String> datas) {
            super(context, layoutId, datas);
        }

        @Override
        public void bindViews(BaseHolder holder, String s, int position) {
            TextView tv = holder.getView(R.id.tv_item);
            tv.setText(s);
            tv.setTextSize(tvSize);
            tv.setTag(position);
            tv.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,getMeasuredHeight()));
        }

        @Override
        public void bindListener(BaseHolder holder) {
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView textView = holder.getView(R.id.tv_item);
                    tv_content.setText(textView.getText().toString());
                    if(listener!=null){
                        listener.onItemClick((Integer) textView.getTag());
                    }
                    close();
                }
            });;
        }
    }

}
