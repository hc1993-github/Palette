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
import java.util.HashMap;
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
    private static Handler handler;
    private static final String FAILURE_NET_INFO = "网络连接失败,请稍后重试";
    private static final String FAILURE_RESPONSE_INFO = "网络请求异常,请稍后重试";
    private static final int FAILURE_RESPONSE_CODE = -404;
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
    }

    public static OkHttpUtil getInstance() {
        if (okHttpUtil == null) {
            synchronized (OkHttpUtil.class) {
                if (okHttpUtil == null) {
                    okHttpUtil = new OkHttpUtil();
                }
            }
        }
        if(handler==null){
            handler = new Handler(Looper.getMainLooper());
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
                onNetFailure(FAILURE_NET_INFO, callback);
            }

            @Override
            public void onResponse(Call call, Response response){
                try {
                    if (response.isSuccessful()) {
                        onResponseSuccess(response.body().string(), callback);
                    } else {
                        onResponseFailure(response.code(),response.body().string(), callback);
                    }
                }catch (Exception e){
                    onResponseFailure(FAILURE_RESPONSE_CODE,FAILURE_RESPONSE_INFO,callback);
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
                onNetFailure(FAILURE_NET_INFO, callback);
            }

            @Override
            public void onResponse(Call call, Response response){
                try {
                    if (response.isSuccessful()) {
                        onResponseSuccess(response.body().string(), callback);
                    } else {
                        onResponseFailure(response.code(),response.body().string(), callback);
                    }
                }catch (Exception e){
                    onResponseFailure(FAILURE_RESPONSE_CODE,FAILURE_RESPONSE_INFO,callback);
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
                onNetFailure(FAILURE_NET_INFO, callback);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (response.isSuccessful()) {
                        onResponseSuccess(response.body().string(), callback);
                    } else {
                        onResponseFailure(response.code(),response.body().string(), callback);
                    }
                }catch (Exception e){
                    onResponseFailure(FAILURE_RESPONSE_CODE,FAILURE_RESPONSE_INFO,callback);
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 文件上传
     * @param url
     * @param headerParams
     * @param params
     * @param listener
     * @param callback
     */
    public void requestUpload(String url, Map<String, String> headerParams,Map<String,Object> params, ProgressListener listener, ResultCallback callback){
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if(headerParams==null){
            headerParams = new HashMap<>();
        }
        headerParams.put("Content-Type","multipart/form-data");
        if(params!=null && params.size()>0){
            for (Map.Entry<String, Object> entry:params.entrySet()) {
                Object value = entry.getValue();
                if(value instanceof String){
                    builder.addFormDataPart(entry.getKey(), (String) entry.getValue());
                }else if(value instanceof File){
                    File f = (File) entry.getValue();
                    builder.addFormDataPart(entry.getKey(),f.getName(), RequestBody.create(MediaType.parse("application/octet-stream"),f));
                }
            }
        }
        RequestBody requestBody;
        if(listener!=null){
            requestBody = new ProgressRequestBody(builder.build(),listener);
        }else {
            requestBody = builder.build();
        }
        Request.Builder build = new Request.Builder().url(url).post(requestBody);
        for (Map.Entry<String, String> entry:headerParams.entrySet()) {
            build.header(entry.getKey(),entry.getValue());
        }
        client.newCall(build.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onNetFailure(FAILURE_NET_INFO, callback);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response){
                try {
                    if (response.isSuccessful()) {
                        onResponseSuccess(response.body().string(), callback);
                    } else {
                        onResponseFailure(response.code(),response.body().string(), callback);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    onResponseFailure(FAILURE_RESPONSE_CODE,FAILURE_RESPONSE_INFO, callback);
                }
            }
        });
    }

    private void onResponseSuccess(String info, ResultCallback callback) {
        if(handler!=null && callback!=null){
            handler.post(() -> callback.onSuccessResponse(info));
        }
    }

    private void onNetFailure(String message, ResultCallback callback) {
        if(handler!=null && callback!=null){
            handler.post(() -> callback.onFailureNet(message));
        }
    }

    private void onResponseFailure(int failureCode,String info, ResultCallback callback) {
        if(handler!=null && callback!=null){
            handler.post(() -> callback.onFailureResponse(failureCode, info));
        }
    }

    public void clear(){
        if(handler!=null){
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    public interface ResultCallback {
        void onFailureNet(String message);

        void onFailureResponse(int failureCode,String info);

        void onSuccessResponse(String info);
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
