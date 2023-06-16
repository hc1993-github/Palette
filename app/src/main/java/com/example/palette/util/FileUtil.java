package com.example.palette.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.text.TextUtils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {
    /**
     * zip解压文件
     * @param src 原文件路径
     * @param password 解压密码
     * @return
     */
    public static String unzip(String src,String password) {
        File srcFile = new File(src);
        File sourceFile = new File(srcFile.getParent());
        try {
            ZipFile zipFile = new ZipFile(srcFile);
            zipFile.setCharset(Charset.forName("GBK"));
            if (!zipFile.isValidZipFile()) {
                throw new ZipException("压缩文件不合法,可能被损坏.");
            }
            if (sourceFile.isDirectory() && !sourceFile.exists()) {
                sourceFile.mkdir();
            }
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password.toCharArray());
            }
            zipFile.extractAll(srcFile.getParent());
            return srcFile.getParent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * zip压缩文件
     * @param src 文件名或文件夹
     * @param pwd 压缩密码
     * @return
     */
    public static String zip(String src,String pwd){
        try {
            File srcFile = new File(src);
            String destPath = generateDestPath(srcFile,srcFile.getParent());
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(CompressionMethod.DEFLATE);
            parameters.setCompressionLevel(CompressionLevel.NORMAL);
            if(!TextUtils.isEmpty(pwd)){
                parameters.setEncryptFiles(true);
                parameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
            }
            ZipFile zipFile = new ZipFile(destPath,pwd.toCharArray());
            zipFile.setCharset(Charset.forName("GBK"));
            if(srcFile.isDirectory()){
                zipFile.addFolder(srcFile,parameters);
            }else {
                zipFile.addFile(srcFile,parameters);
            }
            return destPath;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static String generateDestPath(File srcFile,String dest){
        if(TextUtils.isEmpty(dest)){
            if(srcFile.isDirectory()){
                dest = srcFile.getParent()+File.separator+srcFile.getName()+".zip";
            }else {
                String fileName = srcFile.getName().substring(0,srcFile.getName().lastIndexOf("."));
                dest = srcFile.getParent()+File.separator+fileName+".zip";
            }
        }else {
            File destDir;
            if(dest.endsWith(File.separator)){
                destDir = new File(dest);
                String fileName;
                if(srcFile.isDirectory()){
                    fileName = srcFile.getName();
                }else {
                    fileName = srcFile.getName().substring(0,srcFile.getName().lastIndexOf("."));
                }
                dest +=fileName+".zip";
            }else {
                destDir = new File(dest.substring(0,dest.lastIndexOf(File.separator)));
            }
            if(!destDir.exists()){
                destDir.mkdirs();
            }
        }
        return dest;
    }

    /**
     * 写出到文件
     * @param absolutePath
     * @param stream
     */
    public static void writeToFile(String absolutePath,InputStream stream) {
        File file = new File(absolutePath);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024 * 8];
            int length;
            while ((length = stream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, length);
                fileOutputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 写出到文件重载
     * @param absolutePath
     * @param data
     */
    public static void writeToFile(String absolutePath, byte[] data) {
        File file = new File(absolutePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 写出到文件重载
     * @param absolutePath
     * @param bitmap
     */
    public static void writeToFile(String absolutePath, Bitmap bitmap) {
        File file = new File(absolutePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 写出到文件重载
     * @param absolutePath
     * @param string
     */
    public static void writeToFile(String absolutePath, String string) {
        File file = new File(absolutePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(string.getBytes("utf-8"));
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 检查某APK是否已经安装了高版本
     * @param context
     * @param file
     * @return
     */
    private static boolean checkIsInstalled(Context context, File file) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo archiveInfo = packageManager.getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
        String destApplicationId = archiveInfo.applicationInfo.packageName;
//        currentInstallApkApplicationId = destApplicationId;
        int destVersionCode = archiveInfo.versionCode;
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        for (int i = 0; i < packageInfos.size(); i++) {
            PackageInfo packageInfo = packageInfos.get(i);
            String applicationId = packageInfo.packageName;
            int versionCode = packageInfo.versionCode;
            if (applicationId.equals(destApplicationId)) {
                if (versionCode >= destVersionCode) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取某个应用的启动Activity
     * @param context
     * @param applicationId
     * @return
     */
    public static String getLauncherActivityName(Context context,String applicationId){
        String className = null;
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setPackage(applicationId);
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_ALL);
        ResolveInfo resolveInfo = resolveInfos.iterator().next();
        if(resolveInfo!=null){
            className = resolveInfo.activityInfo.name;
        }
        LogUtil.logi("启动activity名 "+className);
        return className;
    }
}
