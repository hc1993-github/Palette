package com.example.palette.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.palette.util.FileUtil;

public class ApkInstallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction()) || Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())){

            String installApplicationId = intent.getDataString().substring(8);
            String launcherActivityName = FileUtil.getLauncherActivityName(context, null);
            String applicationId = null;
            if(installApplicationId.equals(applicationId)){ //安装完后自启动
                Intent i = new Intent();
                i.setClassName(applicationId,launcherActivityName);
                i.setAction("android.intent.action.MAIN");
                i.addCategory("android.intent.category.LAUNCHER");
                i.addCategory("android.intent.category.DEFAULT");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }
    }
}
