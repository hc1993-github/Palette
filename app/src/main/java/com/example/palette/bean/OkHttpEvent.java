package com.example.palette.bean;

/**
 * 请求质量类
 */
public class OkHttpEvent {
    private long dnsStartTime;//dns开始时间
    private long dnsEndTime;//dns结束时间
    private long responseBodySize;//网络请求返回值大小
    private boolean isSuccess;//请求是否成功
    private String failReason;//请求失败原因

    public long getDnsStartTime() {
        return dnsStartTime;
    }

    public void setDnsStartTime(long dnsStartTime) {
        this.dnsStartTime = dnsStartTime;
    }

    public long getDnsEndTime() {
        return dnsEndTime;
    }

    public void setDnsEndTime(long dnsEndTime) {
        this.dnsEndTime = dnsEndTime;
    }

    public long getResponseBodySize() {
        return responseBodySize;
    }

    public void setResponseBodySize(long responseBodySize) {
        this.responseBodySize = responseBodySize;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }
}
