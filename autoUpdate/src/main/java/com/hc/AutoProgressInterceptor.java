package com.hc;



import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AutoProgressInterceptor implements Interceptor {
    static Map<String, AutoProgressListener> listenerMap = new HashMap<>();
    public static void addListener(String url,AutoProgressListener listener){
        listenerMap.put(url,listener);
    }
    public static void removeListener(String url){
        listenerMap.remove(url);
    }
    AutoProgressListener listener;

    public AutoProgressInterceptor(AutoProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        String url = request.url().toString();
        ResponseBody body = response.body();
        Response newresponse = listener==null?response.newBuilder().body(new AutoProgressResponseBody(url, body)).build():response.newBuilder().body(new AutoProgressResponseBody(body,listener)).build();
        return newresponse;
    }
}
