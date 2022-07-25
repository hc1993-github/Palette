package com.example.palette.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
}
