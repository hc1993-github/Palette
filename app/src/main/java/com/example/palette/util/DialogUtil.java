package com.example.palette.util;

import android.app.Dialog;
import android.content.Context;

import com.example.palette.view.CommonDialog;

public class DialogUtil {
    public static void showCommonDialog(Context context,int layoutId,boolean cancelable,int positiveId,int negativeId,int commonId,DefaultListener defaultListener,OtherListener otherListener,int...otherIds){
        CommonDialog commonDialog = new CommonDialog.Builder()
                .setContext(context)
                .setCancelable(cancelable)
                .setLayoutId(layoutId)
                .setLeftViewId(negativeId)
                .setCommonViewId(commonId)
                .setRightViewId(positiveId)
                .setOtherViewId(otherIds)
                .setDefaultListener(new CommonDialog.CommonDialogDefaultOnClickListener() {
                    @Override
                    public void onLeftViewClick(Dialog dialog) {
                        if(defaultListener!=null){
                            defaultListener.onNegativeClick(dialog);
                        }
                    }

                    @Override
                    public void onCommonViewClick(Dialog dialog) {
                        if(defaultListener!=null){
                            defaultListener.onCommonClick(dialog);
                        }
                    }

                    @Override
                    public void onRightViewClick(Dialog dialog) {
                        if(defaultListener!=null){
                            defaultListener.onPositiveClick(dialog);
                        }
                    }
                })
                .setOtherListener((dialog, viewId) -> {
                    if(otherListener!=null){
                        otherListener.onOtherClick(dialog,viewId);
                    }
                }).build();
        commonDialog.show();
    }

    public interface DefaultListener{
        void onPositiveClick(Dialog dialog);
        void onNegativeClick(Dialog dialog);
        void onCommonClick(Dialog dialog);
    }

    public interface OtherListener{
        void onOtherClick(Dialog dialog,int viewId);
    }
}
