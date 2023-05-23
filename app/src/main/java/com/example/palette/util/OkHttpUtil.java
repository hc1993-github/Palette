package com.example.palette.util;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.example.palette.module.ProgressInterceptor;
import com.example.palette.module.ProgressListener;
import com.example.palette.module.ProgressRequestBody;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtil {
    private volatile static OkHttpUtil okHttpUtil;
    private OkHttpClient client;
    private Handler handler;

    private OkHttpUtil() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS);
        client = builder
                //.addInterceptor(new NoNetInterceptor())
                //.eventListenerFactory(OkHttpEventListener.FACTORY)
                .build();
        handler = new Handler(Looper.getMainLooper());
    }

    public static OkHttpUtil getInstance() {
        if (okHttpUtil == null) {
            synchronized (OkHttpUtil.class) {
                if (okHttpUtil == null) {
                    okHttpUtil = new OkHttpUtil();
                }
            }
        }
        return okHttpUtil;
    }

    /**
     * get post请求
     * @param url
     * @param isGet 是否get
     * @param headerParams request头部参数
     * @param params 请求参数
     * @param callback
     */
    public void requestWithParams(String url, boolean isGet, Map<String, String> headerParams, Map<String, String> params, ResultCallback callback) {
        Request.Builder builder = new Request.Builder();
        if (headerParams != null && !headerParams.isEmpty()) {
            for (Map.Entry<String, String> entry : headerParams.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request;
        if (isGet) {
            String param = "";
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    param += "&" + entry.getKey() + "=" + entry.getValue();
                }
            }
            if (!TextUtils.isEmpty(param)) url += "?" + param.substring(1);
            request = builder.url(url).build();
        } else {
            FormBody.Builder form = new FormBody.Builder();
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    form.add(entry.getKey(), entry.getValue());
                }
                request = builder.url(url).post(form.build()).build();
            } else {
                request = builder.url(url).post(form.build()).build();
            }
        }
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onNetError("网络连接失败,请稍后重试", callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccessCallback(response.body().string(), callback);
                } else {
                    onFailCallback(response.body().string(), callback);
                }
            }
        });

    }

    /**
     * post请求
     * @param url
     * @param headerParams request头部参数
     * @param json json数据
     * @param callback
     */
    public void requestWithJson(String url, Map<String, String> headerParams, String json, ResultCallback callback) {
        Request.Builder builder = new Request.Builder();
        if (headerParams != null && !headerParams.isEmpty()) {
            for (Map.Entry<String, String> entry : headerParams.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request;
        if (!TextUtils.isEmpty(json)) {
            request = builder.url(url).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"),json)).build();
        } else {
            request = builder.url(url).build();
        }
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onNetError("网络连接失败,请稍后重试", callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccessCallback(response.body().string(), callback);
                } else {
                    onFailCallback(response.body().string(), callback);
                }
            }
        });
    }

    /**
     * get post请求
     * @param url
     * @param isGet 是否get
     * @param headerParams request头部参数
     * @param params 请求参数
     * @param callback
     * @param interceptors 自定义拦截器
     */
    public void requestWithParams(String url, boolean isGet, Map<String, String> headerParams, Map<String, String> params, ResultCallback callback, Interceptor... interceptors) {
        Request.Builder builder = new Request.Builder();
        if (headerParams != null && !headerParams.isEmpty()) {
            for (Map.Entry<String, String> entry : headerParams.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request;
        if (isGet) {
            String param = "";
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    param += "&" + entry.getKey() + "=" + entry.getValue();
                }
            }
            if (!TextUtils.isEmpty(param)) url += "?" + param.substring(1);
            request = builder.url(url).build();
        } else {
            FormBody.Builder form = new FormBody.Builder();
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    form.add(entry.getKey(), entry.getValue());
                }
                request = builder.url(url).post(form.build()).build();
            } else {
                request = builder.url(url).post(form.build()).build();
            }
        }

        OkHttpClient.Builder newBuilder = client.newBuilder();
        for (Interceptor interceptor : interceptors) {
            newBuilder.addInterceptor(interceptor);
        }
        OkHttpClient okHttpClient = newBuilder.build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onNetError("网络连接失败,请稍后重试", callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccessCallback(response.body().string(), callback);
                } else {
                    onFailCallback(response.body().string(), callback);
                }
            }
        });
    }

    /**
     * 文件下载
     * @param url
     * @param isGet 是否get
     * @param headerParams request头部参数
     * @param params 请求参数
     * @param file 下载的文件
     * @param listener 进度监听器
     * @param callback
     */
    public void requestDownLoadWithParams(String url, boolean isGet, Map<String, String> headerParams, Map<String, String> params, File file, ProgressListener listener, ResultCallback callback) {
        Request.Builder builder = new Request.Builder();
        if (headerParams != null && !headerParams.isEmpty()) {
            for (Map.Entry<String, String> entry : headerParams.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request;
        if (isGet) {
            String param = "";
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    param += "&" + entry.getKey() + "=" + entry.getValue();
                }
            }
            if (!TextUtils.isEmpty(param)) url += "?" + param.substring(1);
            request = builder.url(url).build();
        } else {
            FormBody.Builder form = new FormBody.Builder();
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    form.add(entry.getKey(), entry.getValue());
                }
                request = builder.url(url).post(form.build()).build();
            } else {
                request = builder.url(url).post(form.build()).build();
            }
        }
        OkHttpClient okHttpClient = client.newBuilder().addInterceptor(new ProgressInterceptor(listener)).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onNetError("网络连接失败,请稍后重试", callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    long contentLength = response.body().contentLength();
                    FileUtil.writeToFile(file.getAbsolutePath(), response.body().byteStream());
                    if (file.length() == contentLength) {
                        onSuccessCallback(null, callback);
                    }
                } else {
                    onFailCallback(response.body().string(), callback);
                }
            }
        });
    }

    /**
     * 文件上传
     * @param url
     * @param headerParams request头部参数
     * @param bodyParams 请求体参数
     * @param file 上传的文件
     * @param listener 进度监听器
     * @param callback
     */
    public void requestUpLoadWithParams(String url, Map<String, String> headerParams, Map<String, String> bodyParams, File file, ProgressListener listener, ResultCallback callback) {
        Request.Builder builder = new Request.Builder();
        builder.addHeader("Content-Type", "multipart/form-data");
        if (headerParams != null && !headerParams.isEmpty()) {
            for (Map.Entry<String, String> entry : headerParams.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        MultipartBody.Builder form = new MultipartBody.Builder();
        form.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"),file));
        if (bodyParams != null && !bodyParams.isEmpty()) {
            form.setType(MultipartBody.FORM);
            for (Map.Entry<String, String> entry : bodyParams.entrySet()) {
                form.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        ProgressRequestBody requestBody = new ProgressRequestBody(form.build(), listener);
        Request request = builder.url(url).post(requestBody).build();
        OkHttpClient okHttpClient = client.newBuilder().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call,IOException e) {
                onNetError("网络连接失败,请稍后重试", callback);
            }

            @Override
            public void onResponse(Call call,Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccessCallback(response.body().string(), callback);
                } else {
                    onFailCallback(response.body().string(), callback);
                }
            }
        });
    }

    private void onSuccessCallback(String info, ResultCallback callback) {
        handler.post(() -> {
            if (callback != null) {
                try {
                    callback.onSuccessResponse(info);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void onNetError(String message, ResultCallback callback) {
        handler.post(() -> {
            if (callback != null) {
                callback.onNetError(message);
            }
        });
    }

    private void onFailCallback(String info, ResultCallback callback) {
        handler.post(() -> {
            if (callback != null) {
                callback.onFailResponse(info);
            }
        });
    }

    public void cancelTag(Object tag) {
        for (Call call : client.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : client.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    public void cancelAll() {
        client.dispatcher().cancelAll();
    }

    public interface ResultCallback {
        void onNetError(String message);

        void onFailResponse(String info);

        void onSuccessResponse(String info) throws IOException;
    }
}
