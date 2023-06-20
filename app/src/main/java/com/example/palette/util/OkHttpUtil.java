package com.example.palette.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.example.palette.module.ProgressInterceptor;
import com.example.palette.module.ProgressListener;
import com.example.palette.module.ProgressRequestBody;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

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
//                .sslSocketFactory(createSSLSocketFactory()) //信任所有证书
//                .hostnameVerifier(new HostnameVerifier() {
//                    @Override
//                    public boolean verify(String hostname, SSLSession session) {
//                        return true;
//                    }
//                })
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
                onNetFailure("网络连接失败,请稍后重试", callback);
            }

            @Override
            public void onResponse(Call call, Response response){
                try {
                    if (response.isSuccessful()) {
                        onResponseSuccess(response.body().string(), callback);
                    } else {
                        onResponseFailure(response.body().string(), callback);
                    }
                }catch (Exception e){
                    onResponseFailure("请求异常",callback);
                    e.printStackTrace();
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
                onNetFailure("网络连接失败,请稍后重试", callback);
            }

            @Override
            public void onResponse(Call call, Response response){
                try {
                    if (response.isSuccessful()) {
                        onResponseSuccess(response.body().string(), callback);
                    } else {
                        onResponseFailure(response.body().string(), callback);
                    }
                }catch (Exception e){
                    onResponseFailure("请求异常",callback);
                    e.printStackTrace();
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
                onNetFailure("网络连接失败,请稍后重试", callback);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (response.isSuccessful()) {
                        onResponseSuccess(response.body().string(), callback);
                    } else {
                        onResponseFailure(response.body().string(), callback);
                    }
                }catch (Exception e){
                    onResponseFailure("请求异常",callback);
                    e.printStackTrace();
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
                onNetFailure("网络连接失败,请稍后重试", callback);
            }

            @Override
            public void onResponse(Call call, Response response){
                try {
                    if (response.isSuccessful()) {
                        long contentLength = response.body().contentLength();
                        FileUtil.writeToFile(file.getAbsolutePath(), response.body().byteStream());
                        if (file.length() == contentLength) {
                            onResponseSuccess(null, callback);
                        }
                    } else {
                        onResponseFailure(response.body().string(), callback);
                    }
                }catch (Exception e){
                    onResponseFailure("请求异常",callback);
                    e.printStackTrace();
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
                onNetFailure("网络连接失败,请稍后重试", callback);
            }

            @Override
            public void onResponse(Call call,Response response) {
                try {
                    if (response.isSuccessful()) {
                        onResponseSuccess(response.body().string(), callback);
                    } else {
                        onResponseFailure(response.body().string(), callback);
                    }
                }catch (Exception e){
                    onResponseFailure("请求异常",callback);
                    e.printStackTrace();
                }
            }
        });
    }

    private void onResponseSuccess(String info, ResultCallback callback) {
        handler.post(() -> {
            if (callback != null) {
                try {
                    callback.onResponseSuccess(info);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void onNetFailure(String message, ResultCallback callback) {
        handler.post(() -> {
            if (callback != null) {
                callback.onNetFailure(message);
            }
        });
    }

    private void onResponseFailure(String info, ResultCallback callback) {
        handler.post(() -> {
            if (callback != null) {
                callback.onResponseFailure(info);
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
        void onNetFailure(String message);

        void onResponseFailure(String info);

        void onResponseSuccess(String info) throws IOException;
    }

    private SSLSocketFactory createSSLSocketFactory(){
        SSLSocketFactory factory = null;
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null,new TrustManager[]{new TrustAllCerts()},new SecureRandom());
            factory = sslContext.getSocketFactory();
        }catch (Exception e){
            e.printStackTrace();
        }
        return factory;
    }

    private class TrustAllCerts implements X509TrustManager{

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private SSLSocketFactory createSSLSocketFactory(InputStream certStream){
        SSLContext sslContext = null;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            Certificate certificate;
            try {
                certificate = certificateFactory.generateCertificate(certStream);
            }finally {
                certStream.close();
            }
            String defaultType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(defaultType);
            keyStore.load(null,null);
            keyStore.setCertificateEntry("ca",certificate);
            String algorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory factory = TrustManagerFactory.getInstance(algorithm);
            factory.init(keyStore);
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null,factory.getTrustManagers(),null);
        }catch (Exception e){
            e.printStackTrace();
        }
        return sslContext!=null?sslContext.getSocketFactory():null;
    }

    private InputStream getCertStream(Context context){
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open("");
//            inputStream = new ByteArrayInputStream("".getBytes("UTF-8"));
        }catch (Exception e){
            e.printStackTrace();
        }
        return inputStream;
    }
}
