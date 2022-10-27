package com.example.palette.util;

import android.annotation.SuppressLint;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

public class NetUtil {

    /**
     * 是否有网络
     * @param context
     * @return
     */
    public static boolean hasNet(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo!=null || networkInfo.isConnected()){
            if(networkInfo.getState()== NetworkInfo.State.CONNECTED){
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是wifi
     * @param context
     * @return
     */
    public static boolean isWifi(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo.getType()==manager.TYPE_WIFI ? true : false;
    }

    /**
     * 网络是否可用
     * @param context
     * @return
     */
    public static boolean netCanUse(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
    }

    /**
     * wifi是否可用
     * @param context
     * @return
     */
    public static boolean wifiCanUse(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
    }

    /**
     * 移动网络流量统计
     * @param context
     * @param startTime 开始时间
     * @param endTime  结束时间
     * @return  流量消耗 单位KB
     */
    public static long getNetStats(Context context,long startTime,long endTime){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return -1;
        }
        long netDataReceive = 0;
        long netDataSend = 0;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String subId = telephonyManager.getSubscriberId();
        NetworkStatsManager manager = (NetworkStatsManager) context.getSystemService(Context.NETWORK_STATS_SERVICE);
        NetworkStats networkStats = null;
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        try {
            networkStats = manager.querySummary(NetworkCapabilities.TRANSPORT_CELLULAR,subId,startTime,endTime);
        }catch (Exception e){
            e.printStackTrace();
        }
        while (networkStats!=null && networkStats.hasNextBucket()){
            networkStats.getNextBucket(bucket);
            int uid = bucket.getUid();
            if(getAppUid(context) == uid){
                netDataReceive += bucket.getRxBytes();
                netDataSend += bucket.getTxBytes();
            }
        }
        return netDataReceive+netDataSend;
    }

    /**
     * 获取app的uid
     * @param context
     * @return
     */
    public static int getAppUid(Context context){
        try {
            String packageName = context.getPackageName();
            PackageManager packageManager = context.getPackageManager();
            @SuppressLint("WrongConstant")
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName,PackageManager.GET_ACTIVITIES);
            return applicationInfo.uid;
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }
}
