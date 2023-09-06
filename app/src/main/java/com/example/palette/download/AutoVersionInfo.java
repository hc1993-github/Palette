package com.example.palette.download;

import java.io.Serializable;

public class AutoVersionInfo implements Serializable {
    private String url;
    private String md5;
    private String unZipPwd;
    private String updateInfo;
    private String versionCode;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getUnZipPwd() {
        return unZipPwd;
    }

    public void setUnZipPwd(String unZipPwd) {
        this.unZipPwd = unZipPwd;
    }

    public String getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(String updateInfo) {
        this.updateInfo = updateInfo;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    @Override
    public String toString() {
        return "AutoVersionInfo{" +
                "url='" + url + '\'' +
                ", md5='" + md5 + '\'' +
                ", unZipPwd='" + unZipPwd + '\'' +
                ", updateInfo='" + updateInfo + '\'' +
                ", versionCode='" + versionCode + '\'' +
                '}';
    }
}
