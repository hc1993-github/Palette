package com.hc.autoupdater;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AutoDownLoader {
    private AutoProgressListener mListener;
    private String mUrl;
    private Context mContext;
    private String mDestFilePath;
    private String mDestFileName;
    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    mListener.start(msg.arg1);
                    break;
                case 2:
                    mListener.end(Environment.getExternalStorageDirectory()+File.separator+mDestFilePath+File.separator+mUrl.substring(mUrl.lastIndexOf(File.separator) + 1));
                    break;
                case 3:
                    mListener.error((String) msg.obj);
                    break;
            }
        }
    };

    public AutoDownLoader(Context context){
        mContext = context;
    }

    public void downLoad(String url,AutoProgressListener listener){
        try {
            mUrl = url;
            mListener = listener;
            String[] splits = mContext.getPackageName().split("\\.");
            mDestFilePath = Environment.DIRECTORY_DOWNLOADS+File.separator+splits[splits.length-1]+File.separator+"update";
            mDestFileName = url.substring(url.lastIndexOf(File.separator) + 1);
            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
                File destFileDir = new File(Environment.getExternalStorageDirectory(),mDestFilePath);
                if(!destFileDir.exists()){
                    destFileDir.mkdirs();
                }
                File destFile = new File(destFileDir.getAbsolutePath(),mDestFileName);
                buildAndRequest(true,destFile,null);
            }else {
                Uri uri = MediaStore.Files.getContentUri("external");
                ContentResolver contentResolver = mContext.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Downloads.RELATIVE_PATH,mDestFilePath);
                contentValues.put(MediaStore.Downloads.DISPLAY_NAME, mDestFileName);
                contentValues.put(MediaStore.Downloads.TITLE, mDestFileName);
                Uri insertUri = contentResolver.insert(uri, contentValues);
                if(insertUri!=null){
                    OutputStream outputStream = contentResolver.openOutputStream(insertUri);
                    buildAndRequest(false,null,outputStream);
                }else {
                    sendMessage(3,"文件操作失败,下载失败");
                }
            }
        }catch (Exception e){
            sendMessage(3,"发生未知错误,下载失败");
        }
    }

    private void buildAndRequest(boolean isExternalStorageLegacy,File destFile,OutputStream outputStream){
        OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(30,TimeUnit.SECONDS).writeTimeout(30,TimeUnit.SECONDS);
        builder.addInterceptor(new AutoProgressInterceptor(mListener));
        OkHttpClient client = builder.build();
        Request request = new Request.Builder().url(mUrl).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendMessage(3,"连接服务器异常,下载失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    long contentLength = response.body().contentLength();
                    sendMessage(1, null,(int) contentLength);
                    if(isExternalStorageLegacy){
                        AutoFileUtil.writeToFile(destFile.getAbsolutePath(), response.body().byteStream());
                        if (destFile.length() == contentLength) {
                            sendMessage(2,null);
                        }else {
                            sendMessage(3,"文件长度不完整,下载失败");
                        }
                    }else {
                        long writeTotalLength = AutoFileUtil.writeToFile(outputStream,response.body().byteStream());
                        if(writeTotalLength==contentLength){
                            sendMessage(2,null);
                        }else {
                            sendMessage(3,"文件长度不完整,下载失败");
                        }
                    }

                }else {
                    sendMessage(3,"服务器响应失败,下载失败");
                }
            }
        });
    }

    private void sendMessage(int type, String str,int... arg1) {
        Message message = Message.obtain();
        message.what = type;
        if (arg1.length != 0) {
            message.arg1 = arg1[0];
        }
        if(str!=null){
            message.obj = str;
        }
        mHandler.sendMessage(message);
    }

    public boolean deleteFile(String destFilePath,String destFileName){
        boolean isExternalStorageLegacy = false;
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
            isExternalStorageLegacy = true;
        }
        if(isExternalStorageLegacy){
            try {
                File file = new File(Environment.getExternalStorageDirectory(),File.separator+destFilePath);
                if(file.isDirectory()){
                    File[] files = file.listFiles();
                    if(files != null){
                        for (int i = files.length - 1; i >= 0; i--) {
                            if(files[i].isFile() && (files[i].getName().endsWith(".apk") || (files[i].getName().endsWith(".zip")))){
                                files[i].delete();
                            }
                        }
                    }
                }
                return true;
            }catch (Exception e){
                return false;
            }
        }else {
            try {
                Uri uri = MediaStore.Files.getContentUri("external");
                ContentResolver contentResolver = mContext.getContentResolver();
                Cursor cursor = contentResolver.query(uri, null, MediaStore.Downloads.DISPLAY_NAME+"=?", new String[]{destFileName}, null);
                if(cursor!=null && cursor.moveToFirst()){
                    Uri queryUri = ContentUris.withAppendedId(uri, cursor.getLong(25));
                    int delete = contentResolver.delete(queryUri,null,null);
                    if(delete<=0){
                        cursor.close();
                        return false;
                    }else {
                        cursor.close();
                        return true;
                    }
                }else {
                    return true;
                }
            }catch (Exception e){
                return false;
            }
        }
    }
}
