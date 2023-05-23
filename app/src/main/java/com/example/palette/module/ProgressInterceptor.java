package com.example.palette.module;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ProgressInterceptor implements Interceptor {
    static Map<String,ProgressListener> listenerMap = new HashMap<>();
    public static void addListener(String url, ProgressListener listener){
        listenerMap.put(url,listener);
    }
    public static void removeListener(String url){
        listenerMap.remove(url);
    }
    ProgressListener listener;

    public ProgressInterceptor(ProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        String url = request.url().toString();
        ResponseBody body = response.body();
        Response newresponse = listener==null?response.newBuilder().body(new ProgressResponseBody(url, body)).build():response.newBuilder().body(new ProgressResponseBody(body,listener)).build();
        return newresponse;
    }
}
