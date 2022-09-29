package com.example.palette.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {
    public static boolean put(Context context,String name,String key,Object value){
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(name,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if(value instanceof String){
            editor.putString(key, (String) value);
        }else if(value instanceof Integer){
            editor.putInt(key, (Integer) value);
        }else if(value instanceof Boolean){
            editor.putBoolean(key, (Boolean) value);
        }else if(value instanceof Long){
            editor.putLong(key, (Long) value);
        }else if(value instanceof Float){
            editor.putFloat(key, (Float) value);
        }else {
            editor.putString(key,value.toString());
        }
        return editor.commit();
    }
    public static String getString(Context context,String name,String key,String defaultValue){
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(name,Context.MODE_PRIVATE);
        return preferences.getString(key,defaultValue);
    }
    public static int getInt(Context context,String name,String key,int defaultValue){
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(name,Context.MODE_PRIVATE);
        return preferences.getInt(key,defaultValue);
    }
    public static boolean getBoolean(Context context,String name,String key,boolean defaultValue){
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(name,Context.MODE_PRIVATE);
        return preferences.getBoolean(key,defaultValue);
    }
    public static long getLong(Context context,String name,String key,long defaultValue){
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(name,Context.MODE_PRIVATE);
        return preferences.getLong(key,defaultValue);
    }
    public static float getFloat(Context context,String name,String key,float defaultValue){
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(name,Context.MODE_PRIVATE);
        return preferences.getFloat(key,defaultValue);
    }
    public static Object getObject(Context context,String name,String key,Object defaultValue){
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(name,Context.MODE_PRIVATE);
        return preferences.getString(key,defaultValue.toString());
    }
}
