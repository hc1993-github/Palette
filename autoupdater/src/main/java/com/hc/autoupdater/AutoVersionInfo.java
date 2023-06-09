package com.hc.autoupdater;

import java.io.Serializable;

public class AutoVersionInfo implements Serializable {
    private String versionCode; //版本号
    private String apkUrl; //apk下载路径
    private String updateInfo; //更新内容
    private String unzipPwd;//解压密码

    public String getUnzipPwd() {
        return unzipPwd;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public String getUpdateInfo() {
        return updateInfo;
    }
}
