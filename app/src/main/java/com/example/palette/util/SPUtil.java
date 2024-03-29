package com.example.palette.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {
    private static Context mContext;
    private static SPUtil mSpUtil;
    private static String mName;

    /**
     * 初始化
     * @param context
     * @return
     */
    public static SPUtil init(Context context) {
        mContext = context.getApplicationContext();
        if (mSpUtil == null) {
            synchronized (SPUtil.class) {
                if (mSpUtil == null) {
                    mSpUtil = new SPUtil();
                }
            }
        }
        return mSpUtil;
    }

    /**
     * 设置sp名称
     * @param name
     * @return
     */
    public static SPUtil setName(String name) {
        if (mSpUtil == null) {
            throw new RuntimeException("you must init before setName");
        }
        mName = name;
        return mSpUtil;
    }

    private SPUtil() {

    }

    /**
     * 存储数据
     * @param key 键
     * @param value 值
     * @return 是否成功
     */
    public static boolean put(String key, Object value) {
        checkNameValid();
        SharedPreferences preferences = mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else {
            editor.putString(key, value.toString());
        }
        return editor.commit();
    }

    /**
     * 获取string
     * @param key 键
     * @param defaultValue 默认值
     * @return 返回
     */
    public static String getString(String key, String defaultValue) {
        checkNameValid();
        SharedPreferences preferences = mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
        return preferences.getString(key, defaultValue);
    }

    /**
     * 获取int
     * @param key 键
     * @param defaultValue 默认值
     * @return 返回
     */
    public static int getInt(String key, int defaultValue) {
        checkNameValid();
        SharedPreferences preferences = mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
        return preferences.getInt(key, defaultValue);
    }

    /**
     * 获取boolean
     * @param key
     * @param defaultValue
     * @return 返回
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        checkNameValid();
        SharedPreferences preferences = mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, defaultValue);
    }

    /**
     * 获取long
     * @param key 键
     * @param defaultValue 默认值
     * @return 返回
     */
    public static long getLong(String key, long defaultValue) {
        checkNameValid();
        SharedPreferences preferences = mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
        return preferences.getLong(key, defaultValue);
    }

    /**
     * 获取float
     * @param key 键
     * @param defaultValue 默认值
     * @return 返回
     */
    public static float getFloat(String key, float defaultValue) {
        checkNameValid();
        SharedPreferences preferences = mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
        return preferences.getFloat(key, defaultValue);
    }

    /**
     * 获取对象
     * @param key 键
     * @param defaultValue 默认值
     * @return 返回
     */
    public static Object getObject(String key, Object defaultValue) {
        checkNameValid();
        SharedPreferences preferences = mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
        return preferences.getString(key, defaultValue.toString());
    }

    private static void checkNameValid() {
        if (mName == null) {
            throw new RuntimeException("you must setName before put");
        }
    }
}
