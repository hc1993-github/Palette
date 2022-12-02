package com.example.palette.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;

public class CommonDialog extends Dialog {
    private Builder builder;

    private CommonDialog(Builder builder) {
        super(builder.context);
        this.builder = builder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(builder.layoutId);
        setCancelable(builder.cancelable);
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//dialog透明
        window.setDimAmount(0.5f);//activity昏暗
        window.setTitle(null);//dialog无标题
        initViews();
    }

    protected void initViews() {
        View leftView = findViewById(builder.leftViewId);
        View rightView = findViewById(builder.rightViewId);
        View commonView = findViewById(builder.commonViewId);
        if (builder.defaultListener != null) {
            if (leftView != null) {
                leftView.setOnClickListener(v -> builder.defaultListener.onLeftViewClick(CommonDialog.this));
            }
            if (commonView != null) {
                commonView.setOnClickListener(v -> builder.defaultListener.onCommonViewClick(CommonDialog.this));
            }
            if (rightView != null) {
                rightView.setOnClickListener(v -> builder.defaultListener.onRightViewClick(CommonDialog.this));
            }
        }
        if (builder.otherListener != null) {
            if (builder.ids != null) {
                for (int i : builder.ids) {
                    View view = findViewById(i);
                    if (view != null) {
                        view.setOnClickListener(v -> builder.otherListener.onOtherViewClick(CommonDialog.this, i));
                    }
                }
            }
        }
    }

    public static class Builder {
        private Context context;
        private int layoutId;
        private int leftViewId;
        private int commonViewId;
        private int rightViewId;
        private List<Integer> ids;
        private CommonDialogDefaultOnClickListener defaultListener;
        private CommonDialogOtherOnClickListener otherListener;
        private boolean cancelable = true;

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setLeftViewId(int leftViewId) {
            this.leftViewId = leftViewId;
            return this;
        }

        public Builder setCommonViewId(int commonViewId) {
            this.commonViewId = commonViewId;
            return this;
        }

        public Builder setRightViewId(int rightViewId) {
            this.rightViewId = rightViewId;
            return this;
        }

        public Builder setOtherViewId(int... otherViewIds) {
            if(otherViewIds.length>0){
                if (this.ids == null) {
                    this.ids = new ArrayList<>();
                }
                for (int i : otherViewIds) {
                    this.ids.add(i);
                }
            }
            return this;
        }

        public Builder setLayoutId(int layoutId) {
            this.layoutId = layoutId;
            return this;
        }

        public Builder setDefaultListener(CommonDialogDefaultOnClickListener listener) {
            this.defaultListener = listener;
            return this;
        }

        public Builder setOtherListener(CommonDialogOtherOnClickListener listener) {
            this.otherListener = listener;
            return this;
        }

        public CommonDialog build() {
            if (context == null) {
                throw new RuntimeException("you must setContext before build");
            }
            return new CommonDialog(this);
        }
    }

    public interface CommonDialogDefaultOnClickListener {
        void onLeftViewClick(Dialog dialog);

        void onCommonViewClick(Dialog dialog);

        void onRightViewClick(Dialog dialog);
    }

    public interface CommonDialogOtherOnClickListener {
        void onOtherViewClick(Dialog dialog, int viewId);
    }
}
