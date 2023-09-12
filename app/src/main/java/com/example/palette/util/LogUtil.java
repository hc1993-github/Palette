package com.example.palette.util;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {
    private static final String TAG = "LogUtil";
    private static LogUtil INSTANCE = null;
    private static String LOG_PATH;
    private LogReader mLogReader = null;
    private int mPid;
    public static final int MSEC = 1;
    public static final int SEC = 1000;
    public static final int MIN = 60000;
    public static final int HOUR = 3600000;
    public static final int DAY = 86400000;
    private static final String VERBOSE = "追踪:";
    private static final String DEBUG = "调试:";
    private static final String INFO = "信息:";
    private static final String WARN = "警告:";
    private static final String ERROR = "异常:";
    private int mSize = 3;
    private static Context mContext;
    private static SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat fullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static Date simpleDate = new Date();
    private static Date fullDate = new Date();
    private LogUtil(Context context) {
        this.mContext = context.getApplicationContext();
        init();
        mPid = android.os.Process.myPid();
    }

    private void init() {
        try {

            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    String[] split = mContext.getPackageName().split("\\.");
                    LOG_PATH = split[split.length-1]+File.separator+"log";
                }
                File file = new File(Environment.getExternalStorageDirectory(),LOG_PATH);
                if (!file.exists()) {
                    file.mkdirs();
                }
                if (file.exists() && file.isDirectory()) {
                    File[] files = file.listFiles();
                    String days = date2String(getDate(new Date(), -mSize, DAY), "yyyy-MM-dd");
                    if (files != null) {
                        for (File f : files) {
                            if (f.getName().compareTo(days) < 0) {
                                f.delete();
                            }
                        }
                    }
                }
            }else {
                String[] splits = mContext.getPackageName().split("\\.");
                LOG_PATH = splits[splits.length-1]+File.separator+"log";
                Uri uri = MediaStore.Files.getContentUri("external");
                ContentResolver contentResolver = mContext.getContentResolver();
                for (int i = 1; i <= mSize; i++) {
                    String preLog = date2String(getDate(new Date(),-i,DAY),"yyyy-MM-dd")+".log";
                    Cursor cursor = contentResolver.query(uri, null, MediaStore.Downloads.DISPLAY_NAME + "=?", new String[]{ preLog }, null);
                    if(cursor!=null && cursor.moveToFirst()){
                        Uri queryUri = ContentUris.withAppendedId(uri, cursor.getLong(25));
                        contentResolver.delete(queryUri, null, null);
                    }else {
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String date2String(Date date, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    private static Date getDate(Date date, long timeSpan, int unit) {
        return millis2Date(date2Millis(date) + timeSpan2Millis(timeSpan, unit));
    }

    private static long date2Millis(Date date) {
        return date.getTime();
    }

    private static Date millis2Date(long millis) {
        return new Date(millis);
    }

    private static long timeSpan2Millis(long timeSpan, int unit) {
        return timeSpan * unit;
    }

    public static LogUtil getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LogUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LogUtil(context);
                }
            }
        }
        return INSTANCE;
    }

    public void start(int level) {
        if (mLogReader == null) {
            mLogReader = new LogReader(String.valueOf(mPid), LOG_PATH,level);
            mLogReader.start();
        }
    }

    public void stop() {
        if (mLogReader != null) {
            mLogReader.stoplog();
            mLogReader = null;
        }
    }

    public static void logv(String msg) {
        if(mContext==null){
            Log.v(TAG, getFullDate()+VERBOSE+msg);
        }else {
            Log.v(mContext.getPackageName(), getFullDate()+VERBOSE+msg);
        }
    }

    public static void logd(String msg) {
        if(mContext==null){
            Log.d(TAG, getFullDate()+DEBUG+msg);
        }else {
            Log.d(mContext.getPackageName(), getFullDate()+DEBUG+msg);
        }
    }

    public static void logi(String msg) {
        if(mContext==null){
            Log.i(TAG, getFullDate()+INFO+msg);
        }else {
            Log.i(mContext.getPackageName(), getFullDate()+INFO+msg);
        }
    }

    public static void logw(String msg) {
        if(mContext==null){
            Log.w(TAG, getFullDate()+WARN+msg);
        }else {
            Log.w(mContext.getPackageName(), getFullDate()+WARN+msg);
        }
    }

    public static void loge(String msg) {
        if(mContext==null){
            Log.e(TAG, getFullDate()+ERROR+msg);
        }else {
            Log.e(mContext.getPackageName(), getFullDate()+ERROR+msg);
        }
    }

    private String getSimpleDate() {
        simpleDate.setTime(System.currentTimeMillis());
        return simpleFormat.format(simpleDate);
    }

    private static String getFullDate() {
        fullDate.setTime(System.currentTimeMillis());
        return fullFormat.format(fullDate);
    }

    private class LogReader extends Thread {
        private Process process;
        private BufferedReader bufferedReader;
        private boolean isRunning = true;
        private String cmds;
        private String mPID;
        private OutputStream fos = null;
        public LogReader(String pid, String dir,int level) {
            mPID = pid;
            try {
                if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
                    fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory()+File.separator+dir,getSimpleDate() + ".log"),true);
                }else {
                    Uri uri = MediaStore.Files.getContentUri("external");
                    ContentResolver contentResolver = mContext.getContentResolver();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.Downloads.RELATIVE_PATH,dir);
                    contentValues.put(MediaStore.Downloads.DISPLAY_NAME,getSimpleDate() + ".log");
                    contentValues.put(MediaStore.Downloads.TITLE,getSimpleDate() + ".log");
                    Cursor cursor = contentResolver.query(uri, null, MediaStore.Downloads.DISPLAY_NAME + "=?", new String[]{ getSimpleDate() + ".log" }, null);
                    if(cursor!=null && cursor.moveToFirst()){
                        Uri queryUri = ContentUris.withAppendedId(uri, cursor.getLong(25));
                        fos = contentResolver.openOutputStream(queryUri);
                        cursor.close();
                    }else {
                        Uri insert = contentResolver.insert(uri, contentValues);
                        if(insert!=null){
                            fos = contentResolver.openOutputStream(insert);
                        }
                    }
                }
                if(level==1){
                    cmds = "logcat *:v | grep \"(" + mPID + ")\"";
                }else if(level==2){
                    cmds = "logcat *:d | grep \"(" + mPID + ")\"";
                }else if(level==3){
                    cmds = "logcat *:i | grep \"(" + mPID + ")\"";
                }else if(level==4){
                    cmds = "logcat *:w | grep \"(" + mPID + ")\"";
                }else if(level==5){
                    cmds = "logcat *:e | grep \"(" + mPID + ")\"";
                }else {
                    cmds = "logcat | grep \"(" + mPID + ")\"";
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public void stoplog() {
            isRunning = false;
        }

        @Override
        public void run() {
            try {
                if(TextUtils.isEmpty(cmds)){
                    return;
                }
                process = Runtime.getRuntime().exec(cmds);
                bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()), 1024);
                String line;
                while (isRunning && (line = bufferedReader.readLine()) != null) {
                    if (line.length() == 0) {
                        continue;
                    }
                    if (fos != null) {
                        if(line.contains(mPID)){
                            fos.write((getSimpleDate() + " " + line + "\n").getBytes());
                        }
                    }else {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (process != null) {
                    process.destroy();
                    process = null;
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                        bufferedReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                        fos = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
