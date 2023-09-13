package com.example.palette.download;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AutoProgressInterceptor implements Interceptor {

    AutoProgressListener listener;

    public AutoProgressInterceptor(AutoProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        ResponseBody body = response.body();
        Response newresponse = response.newBuilder().body(new AutoProgressResponseBody(body, listener)).build();
        return newresponse;
    }
}
