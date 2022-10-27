package com.example.palette.module;

import com.example.palette.bean.OkHttpEvent;
import java.net.InetAddress;
import java.util.List;

import okhttp3.Call;
import okhttp3.EventListener;

/**
 * 请求质量监听器
 */
public class OkHttpEventListener extends EventListener {
    public static final Factory FACTORY = new Factory() {
        @Override
        public EventListener create(Call call) {
            return new OkHttpEventListener();
        }
    };
    OkHttpEvent okHttpEvent;
    public OkHttpEventListener(){
        super();
        okHttpEvent = new OkHttpEvent();
    }

    @Override
    public void callStart( Call call) {
        super.callStart(call);
    }

    @Override
    public void dnsStart(Call call,String domainName) {
        super.dnsStart(call, domainName);
        okHttpEvent.setDnsStartTime(System.currentTimeMillis());
    }

    @Override
    public void dnsEnd(Call call,String domainName,List<InetAddress> inetAddressList) {
        super.dnsEnd(call, domainName, inetAddressList);
        okHttpEvent.setDnsEndTime(System.currentTimeMillis());
    }

    @Override
    public void requestBodyEnd(Call call, long byteCount) {
        super.requestBodyEnd(call, byteCount);
        okHttpEvent.setResponseBodySize(byteCount);
    }

    @Override
    public void callEnd(Call call) {
        super.callEnd(call);
        okHttpEvent.setSuccess(true);
    }
}
