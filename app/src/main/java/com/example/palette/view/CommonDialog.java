package com.example.palette.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.palette.R;

public class CommonDialog extends Dialog {
    private TextView leftBtn;
    private TextView rightBtn;
    private Builder builder;
    public CommonDialog(Builder builder) {
        super(builder.context, R.style.commondialog);
        this.builder = builder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(builder.layoutId);
        setCancelable(builder.cancelable);
        initViews();
    }
    public void initViews(){
        leftBtn = findViewById(builder.leftBtnId);
        rightBtn = findViewById(builder.rightBtnId);
        if(leftBtn!=null){
            leftBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.listener.onLeftBtnClick(CommonDialog.this);
                }
            });
        }
        if(rightBtn!=null){
            rightBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.listener.onRightBtnClick(CommonDialog.this);
                }
            });
        }
    }
    public static class Builder{
        private Context context;
        private int layoutId;
        private int leftBtnId;
        private int rightBtnId;
        private CommonDialogOnClickListener listener;
        private boolean cancelable = true;
        public Builder setContext(Context context){
            this.context = context;
            return this;
        }

        public Builder setCancelable(boolean cancelable){
            this.cancelable = cancelable;
            return this;
        }
        public Builder setLeftBtnId(int leftBtnId) {
            this.leftBtnId = leftBtnId;
            return this;
        }

        public Builder setRightBtnId(int rightBtnId) {
            this.rightBtnId = rightBtnId;
            return this;
        }

        public Builder setLayoutId(int layoutId){
            this.layoutId = layoutId;
            return this;
        }
        public Builder setListener(CommonDialogOnClickListener listener){
            this.listener = listener;
            return this;
        }
        public CommonDialog build(){
            return new CommonDialog(this);
        }
    }
    public interface CommonDialogOnClickListener{
        void onLeftBtnClick(Dialog dialog);
        void onRightBtnClick(Dialog dialog);
    }
}
