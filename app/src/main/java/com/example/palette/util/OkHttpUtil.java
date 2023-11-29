package com.example.palette.util;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.example.palette.module.ProgressListener;
import com.example.palette.module.ProgressRequestBody;

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
import javax.net.ssl.KeyManagerFactory;
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
    private static final String NET_EXCEPTION_INFO = "连接服务器失败,请检查网络";
    private static final String IO_EXCEPTION_INFO = "服务器响应失败,请稍后重试";
    private static final int IO_EXCEPTION_CODE = -404;
    private static final int TIME_OUT_CONNECT = 5;
    private static final int TIME_OUT_READ = 5;
    private static final int TIME_OUT_WRITE = 5;

    private OkHttpUtil() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(TIME_OUT_CONNECT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT_WRITE, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT_READ, TimeUnit.SECONDS);
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
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        return okHttpUtil;
    }

    /**
     * get post 非json请求
     *
     * @param url            地址
     * @param isGet          是否get
     * @param dispatchToMain 响应数据切换到主线程
     * @param headerParams   request头部参数
     * @param params         其他请求参数(get拼接至url post放至body)
     * @param callback       回调
     */
    public void requestWithParams(String url, boolean isGet, boolean dispatchToMain, Map<String, String> headerParams, Map<String, String> params, ResultCallback callback, Interceptor... interceptors) {
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
        if (interceptors != null && interceptors.length > 0) {
            OkHttpClient.Builder newBuilder = client.newBuilder();
            for (Interceptor interceptor : interceptors) {
                newBuilder.addInterceptor(interceptor);
            }
            OkHttpClient okHttpClient = newBuilder.build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    onFailureCallback(dispatchToMain, NET_EXCEPTION_INFO, callback);
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        if (response.isSuccessful()) {
                            onResponseSuccessCallback(dispatchToMain, response.code(), response.body().string(), callback);
                        } else {
                            onResponseFailureCallback(dispatchToMain, response.code(), response.body().string(), callback);
                        }
                    } catch (Exception e) {
                        onResponseFailureCallback(dispatchToMain, IO_EXCEPTION_CODE, IO_EXCEPTION_INFO, callback);
                        e.printStackTrace();
                    }
                }
            });
        } else {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    onFailureCallback(dispatchToMain, NET_EXCEPTION_INFO, callback);
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        if (response.isSuccessful()) {
                            onResponseSuccessCallback(dispatchToMain, response.code(), response.body().string(), callback);
                        } else {
                            onResponseFailureCallback(dispatchToMain, response.code(), response.body().string(), callback);
                        }
                    } catch (Exception e) {
                        onResponseFailureCallback(dispatchToMain, IO_EXCEPTION_CODE, IO_EXCEPTION_INFO, callback);
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * post Json请求
     *
     * @param url            地址
     * @param dispatchToMain 响应数据切换到主线程
     * @param headerParams   request头部参数
     * @param json           json数据
     * @param callback       回调
     */
    public void requestWithJson(String url, boolean dispatchToMain, Map<String, String> headerParams, String json, ResultCallback callback) {
        Request.Builder builder = new Request.Builder();
        if (headerParams != null && !headerParams.isEmpty()) {
            for (Map.Entry<String, String> entry : headerParams.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request;
        if (!TextUtils.isEmpty(json)) {
            request = builder.url(url).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)).build();
        } else {
            request = builder.url(url).build();
        }
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onFailureCallback(dispatchToMain, NET_EXCEPTION_INFO, callback);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (response.isSuccessful()) {
                        onResponseSuccessCallback(dispatchToMain, response.code(), response.body().string(), callback);
                    } else {
                        onResponseFailureCallback(dispatchToMain, response.code(), response.body().string(), callback);
                    }
                } catch (Exception e) {
                    onResponseFailureCallback(dispatchToMain, IO_EXCEPTION_CODE, IO_EXCEPTION_INFO, callback);
                    e.printStackTrace();
                }
            }
        });
    }

//    /**
//     * 文件请求
//     * @param url 地址
//     * @param headerParams 请求参数
//     * @param params 文件等其他参数
//     * @param listener 进度监听
//     * @param callback 回调
//     */
//    public void requestWithFile(String url,boolean dispatchToMain, Map<String, String> headerParams, Map<String, Object> params, ProgressListener listener, ResultCallback callback) {
//        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
//        if (headerParams == null) {
//            headerParams = new HashMap<>();
//        }
//        headerParams.put("Content-Type", "multipart/form-data");
//        if (params != null && params.size() > 0) {
//            for (Map.Entry<String, Object> entry : params.entrySet()) {
//                Object value = entry.getValue();
//                if (value instanceof String) {
//                    builder.addFormDataPart(entry.getKey(), (String) entry.getValue());
//                } else if (value instanceof File) {
//                    File f = (File) entry.getValue();
//                    builder.addFormDataPart(entry.getKey(), f.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), f));
//                }
//            }
//        }
//        RequestBody requestBody;
//        if (listener != null) {
//            requestBody = new ProgressRequestBody(builder.build(), listener);
//        } else {
//            requestBody = builder.build();
//        }
//        Request.Builder build = new Request.Builder().url(url).post(requestBody);
//        for (Map.Entry<String, String> entry : headerParams.entrySet()) {
//            build.header(entry.getKey(), entry.getValue());
//        }
//        client.newCall(build.build()).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                onFailureCallback(dispatchToMain,NET_EXCEPTION_INFO, callback);
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) {
//                try {
//                    if (response.isSuccessful()) {
//                        onResponseSuccessCallback(dispatchToMain,response.code(),response.body().string(), callback);
//                    } else {
//                        onResponseFailureCallback(dispatchToMain,response.code(), response.body().string(), callback);
//                    }
//                } catch (Exception e) {
//                    onResponseFailureCallback(dispatchToMain,IO_EXCEPTION_CODE, IO_EXCEPTION_INFO, callback);
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

    private void onFailureCallback(boolean dispatchToMain, String message, ResultCallback callback) {
        if (dispatchToMain) {
            if (handler != null && callback != null) {
                handler.post(() -> callback.onFailure(message));
            }
        } else {
            if (callback != null) {
                callback.onFailure(message);
            }
        }
    }

    private void onResponseSuccessCallback(boolean dispatchToMain, int successCode, String info, ResultCallback callback) {
        if (dispatchToMain) {
            if (handler != null && callback != null) {
                handler.post(() -> callback.onResponseSuccess(successCode, info));
            }
        } else {
            if (callback != null) {
                callback.onResponseSuccess(successCode, info);
            }
        }
    }

    private void onResponseFailureCallback(boolean dispatchToMain, int failureCode, String info, ResultCallback callback) {
        if (dispatchToMain) {
            if (handler != null && callback != null) {
                handler.post(() -> callback.onResponseFailure(failureCode, info));
            }
        } else {
            if (callback != null) {
                callback.onResponseFailure(failureCode, info);
            }
        }
    }

    public void clear() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    public interface ResultCallback {
        void onFailure(String message);

        void onResponseFailure(int failureCode, String info);

        void onResponseSuccess(int successCode, String info);
    }

    /**
     * 信任所有证书
     *
     * @param builder
     */
    private void trustAllCertificate(OkHttpClient.Builder builder) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            X509TrustManager x509TrustManager = new X509TrustManager() {

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
            };
            sslContext.init(null, new TrustManager[]{x509TrustManager}, new SecureRandom());
            builder.sslSocketFactory(sslContext.getSocketFactory(), x509TrustManager);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 信任指定证书
     *
     * @param builder
     * @param certificateStream
     */
    private void trustCertificate(OkHttpClient.Builder builder, InputStream certificateStream) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            Certificate certificate = certificateFactory.generateCertificate(certificateStream);
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", certificate);
            certificateStream.close();
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagerFactory.getTrustManagers()[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
