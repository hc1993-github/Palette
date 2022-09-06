package com.example.palette.util;

import android.os.Handler;
import android.os.Looper;

import com.example.palette.module.ProgressInterceptor;
import com.example.palette.module.ProgressListener;
import com.example.palette.module.ProgressRequestBody;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
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
                onFailureCallback(call,"连接服务器失败",callback);
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
                    onFailureCallback(call,"连接服务器失败",callback);
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
                    onFailureCallback(call,"连接服务器失败",callback);
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
    public void requestWithHeaderAndBody(String url,Map<String,String> headerParams,Map<String,String> bodyParams,ResultCallback callback){
        Request.Builder builder = new Request.Builder();
        if(headerParams!=null && !headerParams.isEmpty()){
            for(Map.Entry<String,String> entry:headerParams.entrySet()){
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request;
        if(bodyParams!=null && !bodyParams.isEmpty()){
            FormBody.Builder form = new FormBody.Builder();
            for(Map.Entry<String,String> entry:bodyParams.entrySet()){
                form.add(entry.getKey(), entry.getValue());
            }
            RequestBody requestBody = form.build();
            request = new Request.Builder().url(url).post(requestBody).build();
        }else {
            request = builder.url(url).build();
        }
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call,IOException e) {
                onFailureCallback(call,"连接服务器失败",callback);
            }

            @Override
            public void onResponse(Call call,Response response) throws IOException {
                if(response.isSuccessful() && response!=null){
                    onSuccessCallback(response,callback);
                }
            }
        });

    }

    public void requestWithInterceptors(String url,ResultCallback callback,Interceptor... interceptors){
        Request request = new Request.Builder().url(url).build();
        OkHttpClient.Builder newBuilder = client.newBuilder();
        for(Interceptor interceptor:interceptors){
            newBuilder.addInterceptor(interceptor);
        }
        OkHttpClient okHttpClient = newBuilder.build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call,IOException e) {
                onFailureCallback(call,"连接服务器失败",callback);
            }

            @Override
            public void onResponse(Call call,Response response) throws IOException {
                if(response.isSuccessful() && response!=null){
                    onSuccessCallback(response,callback);
                }
            }
        });
    }

    public void requestDownLoad(String url, Map<String,String> headerParams, Map<String,String> bodyParams,File file,ProgressListener listener, ResultCallback callback){
        Request.Builder builder = new Request.Builder();
        if(headerParams!=null && !headerParams.isEmpty()){
            for(Map.Entry<String,String> entry:headerParams.entrySet()){
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request;
        if(bodyParams!=null && !bodyParams.isEmpty()){
            FormBody.Builder form = new FormBody.Builder();
            for(Map.Entry<String,String> entry:bodyParams.entrySet()){
                form.add(entry.getKey(), entry.getValue());
            }
            RequestBody requestBody = form.build();
            request = new Request.Builder().url(url).post(requestBody).build();
        }else {
            request = builder.url(url).build();
        }
        OkHttpClient okHttpClient = client.newBuilder().addInterceptor(new ProgressInterceptor(listener)).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call,IOException e) {
                onFailureCallback(call,"连接服务器失败",callback);
            }

            @Override
            public void onResponse(Call call,Response response) throws IOException {
                if(response.isSuccessful() && response!=null){
                    long contentLength = response.body().contentLength();
                    FileUtil.writeToFile(response.body().byteStream(),file);
                    if(file.length()==contentLength){
                        onSuccessCallback(null,callback);
                    }
                }
            }
        });
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

    private void onFailureCallback(Call call, String message, ResultCallback callback) {
        handler.post(() -> {
            if(callback!=null){
                callback.onFail(call.request(),message);
            }
        });
    }

    public interface ResultCallback{
        void onFail(Request request,String message);
        void onSuccess(Response response) throws IOException;
    }
}
