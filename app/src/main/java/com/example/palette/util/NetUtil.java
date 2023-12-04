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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Pattern;

public class NetUtil {
    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^(" + "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}" +
                    "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");

    /**
     * 是否网络已连接
     * @param context
     * @return
     */
    public static boolean isNetConnected(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo != null){
            return networkInfo.isConnected();
        }
        return false;
    }

    /**
     * 是否wifi已连接
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(networkInfo !=null){
            return networkInfo.isConnected();
        }
        return false;
    }

    /**
     * 是否移动数据已连接
     * @param context
     * @return
     */
    public static boolean isMobileConnected(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(networkInfo !=null){
            return networkInfo.isConnected();
        }
        return false;
    }

    /**
     * 是否以太网已连接
     * @param context
     * @return
     */
    public static boolean isEthernetConnected(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if(networkInfo !=null){
            return networkInfo.isConnected();
        }
        return false;
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

    private static boolean isIPv4Address(String input) {
        return IPV4_PATTERN.matcher(input).matches();
    }

    /**
     * 获取本地IP地址
     * @return
     */
    public static InetAddress getLocalIPAddress() {
        Enumeration<NetworkInterface> enumeration = null;
        try {
            enumeration = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                NetworkInterface nif = enumeration.nextElement();
                Enumeration<InetAddress> inetAddresses = nif.getInetAddresses();
                if (inetAddresses != null) {
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        if (!inetAddress.isLoopbackAddress() && isIPv4Address(inetAddress.getHostAddress())) {
                            return inetAddress;
                        }
                    }
                }
            }
        }
        return null;
    }
}
