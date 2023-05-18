package com.hc;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.tencent.mmkv.MMKV;


import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AutoDownLoader {
    private boolean isPause = true;
    private boolean isCancel = false;
    private boolean isFinish = false;
    private boolean isStart = false;
    private AutoProgressListener mListener;
    private File file;
    private File destFile;
    private String mUrl;
    private String mDestFileAbsolutePath;
    private String mLogFileAbsolutePath;
    private Context context;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    Log.i(context.getPackageName(), getFullDate()+" "+"开始下载-----");
                    mListener.start(msg.arg1);
                    break;
                case 2:
                    mListener.progress(msg.arg1);
                    break;
                case 3:
                    Log.i(context.getPackageName(), getFullDate()+" "+"暂停下载-----");
                    mListener.pause(msg.arg1);
                    break;
                case 4:
                    Log.i(context.getPackageName(), getFullDate()+" "+"取消下载-----");
                    mListener.cancel();
                    break;
                case 5:
                    Log.i(context.getPackageName(), getFullDate()+" "+"下载完成-----");
                    mListener.finish(destFile.getAbsolutePath());
                    break;
                case 6:
                    Log.i(context.getPackageName(), getFullDate()+" "+"下载失败-----");
                    mListener.error((String) msg.obj);
                    break;
            }
        }
    };

    public AutoDownLoader(Context context) {
        this.context = context;
    }

    /**
     * @param url 请求地址
     * @param listener 回调接口
     * @param destFileAbsolutePath 下载文件全路径
     * @param logFileAbsolutePath  断点文件全路径
     */
    public void download(@NonNull final String url, @NonNull final AutoProgressListener listener, String destFileAbsolutePath, String logFileAbsolutePath) {
        try {
            mUrl = url;
            isPause = false;
            isCancel = false;
            isStart = true;
            mListener = listener;
            mDestFileAbsolutePath = destFileAbsolutePath;
            mLogFileAbsolutePath = logFileAbsolutePath;
            if (logFileAbsolutePath == null) {
                file = new File(Environment.getExternalStorageDirectory(), "MMKV_cache");
            } else {
                file = new File(logFileAbsolutePath);
            }
            if (destFileAbsolutePath == null) {
                String[] split = context.getPackageName().split("\\.");
                File file = new File(Environment.getExternalStorageDirectory(),split[split.length-1]+"");
                if(!file.exists()){
                    file.mkdirs();
                }
//                else {
//                    File[] files = file.listFiles();
//                    for(File f:files){
//                        if(f.getName().equals(url.substring(url.lastIndexOf("/") + 1))){
//                            continue;
//                        }
//                        f.delete();
//                    }
//                }
                destFile = new File(file.getAbsolutePath(),url.substring(url.lastIndexOf("/") + 1));
            } else {
                destFile = new File(destFileAbsolutePath);
            }
            MMKV.initialize(file.getAbsolutePath());
            final MMKV mmkv = MMKV.defaultMMKV();
            final int currentlength = mmkv.getInt(url, 0);
            final RandomAccessFile randomAccessFile = new RandomAccessFile(destFile, "rwd");
            Request request = new Request.Builder().header("RANGE", "bytes=" + currentlength + "-").url(url).build();
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30,TimeUnit.SECONDS)
                    .writeTimeout(30,TimeUnit.SECONDS);
            OkHttpClient client = builder.build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    sendMessage(6,"网络异常,下载失败");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response.isSuccessful()){
                        long filetotalsize = response.body().contentLength() + currentlength;
                        sendMessage(1, null,(int) filetotalsize);
                        sendMessage(2, null,currentlength);
                        randomAccessFile.setLength(filetotalsize);
                        randomAccessFile.seek(currentlength);
                        InputStream is = response.body().byteStream();
                        byte[] buf = new byte[1024*4];
                        int readlength;
                        int currentreadlength = currentlength;
                        while ((readlength = is.read(buf)) != -1) {
                            if (isCancel) {
                                sendMessage(4,null);
                                closeResources(mmkv, randomAccessFile, is, response.body());
                                return;
                            }
                            if (isPause) {
                                mmkv.encode(url, currentreadlength);
                                sendMessage(3, null,currentreadlength);
                                closeResources(mmkv, randomAccessFile, is, response.body());
                                return;
                            }
                            randomAccessFile.write(buf, 0, readlength);
                            currentreadlength += readlength;
                            sendMessage(2, null,currentreadlength);
                            randomAccessFile.seek(currentreadlength);
                        }
                        closeResources(mmkv, randomAccessFile, is, response.body());
                        deleteFile(file);
                        isFinish = true;
                        isStart = false;
                        sendMessage(5,null);
                    }else {
                        sendMessage(6,"服务器发生异常,下载失败");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            sendMessage(6,"发生未知错误,下载失败");
        }
    }

    public void download2(String url,AutoProgressListener listener,String destFileAbsolutePath){
        try {
            if (destFileAbsolutePath == null) {
                String[] split = context.getPackageName().split("\\.");
                File file = new File(Environment.getExternalStorageDirectory(),split[split.length-1]+"");
                if(!file.exists()){
                    file.mkdirs();
                }
                destFile = new File(file.getAbsolutePath(),url.substring(url.lastIndexOf("/") + 1));
            } else {
                destFile = new File(destFileAbsolutePath);
            }
            mListener = listener;
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(30,TimeUnit.SECONDS)
                    .writeTimeout(30,TimeUnit.SECONDS);
            builder.addInterceptor(new AutoProgressInterceptor(mListener));
            OkHttpClient client = builder.build();
            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call,IOException e) {
                    sendMessage(6,"连接服务器异常,下载失败");
                }

                @Override
                public void onResponse( Call call,Response response) throws IOException {
                    if(response.isSuccessful()){
                        long contentLength = response.body().contentLength();
                        AutoFileUtil.writeToFile(destFile.getAbsolutePath(), response.body().byteStream());
                        if (destFile.length() == contentLength) {
                            sendMessage(5,null);
                        }else {
                            sendMessage(6,"文件长度不完整,下载失败");
                        }
                    }else {
                        sendMessage(6,"服务器响应失败,下载失败");
                    }
                }
            });
        }catch (Exception e){
            sendMessage(6,"发生未知错误,下载失败");
        }
    }

    /**
     * 流关闭
     *
     * @param mmkv
     * @param closeables
     */
    private void closeResources(MMKV mmkv, Closeable... closeables) {
        try {
            mmkv.close();
            int length = closeables.length;
            for (int i = 0; i < length; i++) {
                closeables[i].close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件删除
     *
     * @param files
     */
    private void deleteFile(File... files) {
        try {
            int length = files.length;
            for (int i = 0; i < length; i++) {
                if (files[i].isDirectory()) {
                    for (File f : files[i].listFiles()) {
                        f.delete();
                    }
                    files[i].delete();
                } else {
                    files[i].delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停下载
     */
    public void pause() {
        isPause = true;
    }

    /**
     * 恢复下载
     */
    public void resume() {
        if (isPause && !isFinish && !TextUtils.isEmpty(mUrl) && isStart) {
            //download(mUrl, mListener, mDestFileAbsolutePath, mLogFileAbsolutePath);
            //download2(mUrl, mListener, mDestFileAbsolutePath);
        }
    }

    /**
     * 取消下载
     */
    public void cancel() {
//        isCancel = true;
//        isPause = true;
//        sendMessage(4,null);
//        deleteFile(file, destFile);
    }

    /**
     * 是否暂停
     *
     * @return
     */
    public boolean isPause() {
        return isPause;
    }

    /**
     * 往handler中发消息
     * @param type
     * @param arg1
     */
    private void sendMessage(int type, String str,int... arg1) {
        Message message = Message.obtain();
        message.what = type;
        if (arg1.length != 0) {
            message.arg1 = arg1[0];
        }
        if(str!=null){
            message.obj = str;
        }
        handler.sendMessage(message);
    }

    private String getFullDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date(System.currentTimeMillis()));
    }

    public interface Listener {
        void start(int totalsize);

        void progress(int progress);

        void pause(int progress);

        void cancel();

        void finish(String fileAbsolutePath);

        void error(String message);
    }
}
