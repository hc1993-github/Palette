package com.example.palette.util;

import android.os.Handler;
import android.os.Looper;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtil {
    private volatile static OkHttpUtil okHttpUtil;
    private OkHttpClient client;
    private Handler handler;
    private OkHttpUtil(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20,TimeUnit.SECONDS)
                .readTimeout(20,TimeUnit.SECONDS);
        client = builder.build();
        handler = new Handler(Looper.getMainLooper());
    }
    public static OkHttpUtil getInstance(){
        if(okHttpUtil==null){
            synchronized (OkHttpUtil.class){
                if(okHttpUtil==null){
                    okHttpUtil = new OkHttpUtil();
                }
            }
        }
        return okHttpUtil;
    }
    public void request(String url,ResultCallback callback){
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call,IOException e) {
                onFailureCallback(call,e,callback);
            }

            @Override
            public void onResponse(Call call,Response response) throws IOException {
                if(response.isSuccessful() && response!=null){
                    onSuccessCallback(response,callback);
                }
            }
        });
    }
    public void requestWithHeader(String url, Map<String,String> params,ResultCallback callback){
        if(params!=null && !params.isEmpty()){
            Request.Builder builder = new Request.Builder();
            for(Map.Entry<String,String> entry:params.entrySet()){
                builder.addHeader(entry.getKey(), entry.getValue());
            }
            Request request = builder.url(url).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call,IOException e) {
                    onFailureCallback(call,e,callback);
                }

                @Override
                public void onResponse(Call call,Response response) throws IOException {
                    if(response.isSuccessful() && response!=null){
                        onSuccessCallback(response,callback);
                    }
                }
            });
        }
    }
    public void requestWithBody(String url, Map<String,String> params,ResultCallback callback){
        FormBody.Builder form = new FormBody.Builder();
        if(params!=null && !params.isEmpty()){
            for(Map.Entry<String,String> entry:params.entrySet()){
                form.add(entry.getKey(), entry.getValue());
            }
            RequestBody requestBody = form.build();
            Request request = new Request.Builder().url(url).post(requestBody).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call,IOException e) {
                    onFailureCallback(call,e,callback);
                }

                @Override
                public void onResponse(Call call,Response response) throws IOException {
                    if(response.isSuccessful() && response!=null){
                        onSuccessCallback(response,callback);
                    }
                }
            });
        }
    }

    private void onSuccessCallback(Response response, ResultCallback callback) {
        handler.post(() -> {
            if(callback!=null){
                try {
                    callback.onSuccess(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void onFailureCallback(Call call, IOException e, ResultCallback callback) {
        handler.post(() -> {
            if(callback!=null){
                callback.onFail(call.request(),e);
            }
        });
    }

    public interface ResultCallback{
        void onFail(Request request,Exception e);
        void onSuccess(Response response) throws IOException;
    }
}
