package com.example.palette.download;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

public class AutoFileUtil {

    public static final String mDefaultDir = "update";
    public static String mCurrentInstallApkName;
    public static String mCurrentInstallApkApplicationId;
    public static String mDirName;
    public static final int APP_COMMON_INSTALLING = 00001;
    public static final int FILE_NOT_EXIST = 10001;
    public static final String FILE_NOT_EXIST_INFO = "文件不存在";
    public static final int FILE_MD5_NOT_EQUALS = 10002;
    public static final String FILE_MD5_NOT_EQUALS_INFO = "文件校验失败";
    public static final int FILE_UNZIP_ING = 10003;
    public static final String FILE_UNZIP_ING_INFO = "文件解压中";
    public static final int FILE_UNZIP_FAILED = 10004;
    public static final String FILE_UNZIP_FAILED_INFO = "文件解压失败";
    public static final int APP_EXISTED = 10005;
    public static final String APP_EXISTED_INFO = "已安装最新版本,请先卸载后重新下载";
    /**
     * 是否有root权限
     * @return
     */
    public static boolean isRoot(){
        String[] rootDirs = new String[]{"/su","/su/bin/su","/sbin/su",
                "/data/local/xbin/su","/data/local/bin/su","/data/local/su",
                "/system/xbin/su","/system/bin/su","/system/sd/xbin/su",
                "/system/bin/failsafe/su","/system/bin/cufsdosck","/system/xbin/cufsdosck",
                "/system/bin/cufsmgr","/system/xbin/cufsmgr","/system/bin/cufaevdd",
                "/system/xbin/cufaevdd","/system/bin/conbb","/system/xbin/conbb"};
        boolean isRoot = false;
        for (int i = 0; i < rootDirs.length; i++) {
            String dir = rootDirs[i];
            if(new File(dir).exists()){
                isRoot = true;
                break;
            }
        }
//        return Build.TAGS!=null && Build.TAGS.contains("test-keys") || isRoot;
        return false;
    }

    /**
     * 创建下载目录
     * 不创建则使用/sdcard/applicationId后缀名
     * 创建则使用/sdcard/applicationId后缀名/目录名
     * @param context
     * @param dirName 目录名
     * @return
     */
    public static String createDefaultDir(Context context, String dirName) {
        mDirName = dirName;
        String[] split = context.getApplicationContext().getPackageName().split("\\.");
        String dir;
        if(TextUtils.isEmpty(dirName)){
            dir = split[split.length - 1];
        }else {
            dir = split[split.length - 1] + File.separator + dirName;
        }
        File file = new File(Environment.getExternalStorageDirectory(), dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * 校验文件MD5值并检查是否需要解压
     * @param context
     * @param apkAbsolutePath 文件绝对路径
     * @param remoteMD5 待比较MD5值
     * @param unzipPwd 解压密码
     * @param listener
     */
    public static void checkIsNeedUnzip(Activity context, String apkAbsolutePath, String remoteMD5, String unzipPwd, installListener listener) {
        File file = new File(apkAbsolutePath);
        if (!file.exists()) {
            listener.onCheckedFail(FILE_NOT_EXIST,FILE_NOT_EXIST_INFO);
            return;
        }
        Context applicationContext = context.getApplicationContext();
        File realDir = new File(createDefaultDir(applicationContext, mDirName));
        File cacheDir = new File(createDefaultDir(applicationContext, mDirName));
        if (AutoMD5Util.compareMD5IgnoreCase(remoteMD5,file)) {
            File realApk;
            String[] split = file.getAbsolutePath().split(File.separator);
            String apkFullName = split[split.length - 1];
            mCurrentInstallApkName = apkFullName.substring(0, apkFullName.lastIndexOf("."));
            if (file.getPath().contains("zip")) {
                new Thread(){
                    @Override
                    public void run() {
                        context.runOnUiThread(() -> listener.onUnzipIng(FILE_UNZIP_ING,FILE_UNZIP_ING_INFO));
                        int success = unzip(file.getPath(), realDir.getPath(), unzipPwd);
                        if (success != 0) {
                            file.delete();
                            context.runOnUiThread(() -> listener.onUnzipFail(FILE_UNZIP_FAILED,FILE_UNZIP_FAILED_INFO));
                            return;
                        }
                        context.runOnUiThread(() -> listener.onUnzipSuccess(context,findApkFile(realDir, apkFullName),listener));
                    }
                }.start();
            } else {
                realApk = findApkFile(cacheDir, apkFullName);
                realInstall(context,realApk,listener);
            }
        } else {
            file.delete();
            listener.onCheckedFail(FILE_MD5_NOT_EQUALS,FILE_MD5_NOT_EQUALS_INFO);
        }
    }

    /**
     * 安装
     * @param context
     * @param file
     * @param listener
     */
    public static void realInstall(Activity context, File file, installListener listener){
        try {
            if (checkIsInstalled(context, file)) {
                deleteSimilarFile(context, mCurrentInstallApkName);
                listener.onInstallFail(APP_EXISTED,APP_EXISTED_INFO);
            } else {
                if(isRoot()){
                    realInstallSlience(file);
                }else {
                    realInstallCommon(context,file);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 普通安装
     * @param context
     * @param file
     */
    private static void realInstallCommon(Activity context,File file){
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                uri = FileProvider.getUriForFile(context, context.getPackageName() + ".FileProvider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            context.startActivityForResult(intent,APP_COMMON_INSTALLING);
//          activity回调接收
//            @Override
//            protected void onActivityResult(int requestCode, int resultCode,Intent data) {
//                super.onActivityResult(requestCode, resultCode, data);
//                if(requestCode==APP_COMMON_INSTALLING){
//                    finish();
//                }
//            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 静默安装 需要root权限
     * @param file
     */
    private static void realInstallSlience(File file){
        //此静默安装方法只能将apk放在如sdcard/updateapp/xxxx.apk下 再多目录就抛异常
        boolean result = false;
        BufferedReader es = null;
        DataOutputStream os = null;
        StringBuilder esbuilder = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            String command = "pm install -r " + file.getAbsolutePath() + "\n";
            os.write(command.getBytes(Charset.forName("utf-8")));
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            String line;
            es = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = es.readLine()) != null) {
                esbuilder.append(line);
            }
            if(!esbuilder.toString().contains("Failure") && !esbuilder.toString().contains("failure")){
                result = true;
            }
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (es != null) {
                    es.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 校验设备是否已经安装同版本或更高版本的APP
     * @param context
     * @param file
     * @return
     */
    private static boolean checkIsInstalled(Context context, File file) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo archiveInfo = packageManager.getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
        String destApplicationId = archiveInfo.applicationInfo.packageName;
        mCurrentInstallApkApplicationId = destApplicationId;
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
     * 获取APP启动activity名
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
        return className;
    }

    /**
     * zip解压
     * @param apkAbsolutePath
     * @param parentDir
     * @param unzipPwd
     * @return
     */
    private static int unzip(String apkAbsolutePath, String parentDir, String unzipPwd) {
        File zipSourceFile = new File(apkAbsolutePath);
        try {
            ZipFile zipFile = new ZipFile(zipSourceFile);
            zipFile.setCharset(Charset.forName("GBK"));
            if (!zipFile.isValidZipFile()) {
                return -1;
            }
            deleteMatchFile(parentDir, apkAbsolutePath);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(unzipPwd.toCharArray());
            }
            zipFile.extractAll(parentDir);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 解压前先删除存在的apk
     * @param dirPath
     * @param fileName
     */
    private static void deleteMatchFile(String dirPath, String fileName) {
        File file = new File(dirPath);
        File[] files = file.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (fileName.contains(files[i].getName()) && files[i].getName().endsWith(".apk")) {
                    files[i].delete();
                    return;
                }
            }
        }
    }

    /**
     * 删除同名zip和apk
     * @param context
     * @param name
     */
    public static void deleteSimilarFile(Context context, String name) {
        String path = createDefaultDir(context, mDirName);
        File[] cachefiles = new File(path).listFiles();
        if (cachefiles != null) {
            for (int i = 0; i < cachefiles.length; i++) {
                if (cachefiles[i].getName().contains(name)) {
                    cachefiles[i].delete();
                }
            }
        }
        mCurrentInstallApkName = null;
    }

    /**
     * 清空下载目录下所有zip和apk
     * @param context
     */
    public static void deleteZipAndApk(Context context){
        String path = createDefaultDir(context, mDirName);
        File[] cachefiles = new File(path).listFiles();
        if(cachefiles != null){
            for (int i = 0; i < cachefiles.length; i++) {
                if (cachefiles[i].getName().endsWith(".zip") || cachefiles[i].getName().endsWith(".apk")) {
                    cachefiles[i].delete();
                }
            }
        }
    }

    /**
     * 查找apk
     * @param dir
     * @param apkFullName
     * @return
     */
    private static File findApkFile(File dir, String apkFullName) {
        String subName = apkFullName.substring(0, apkFullName.lastIndexOf("."));
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().contains(subName) && files[i].getName().endsWith(".apk")) {
                return files[i];
            }
        }
        return null;
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
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
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

    public interface installListener {
        void onCheckedFail(int code,String message);
        void onUnzipFail(int code,String message);
        void onUnzipSuccess(Activity context, File file, installListener listener);
        void onUnzipIng(int code,String message);
        void onInstallFail(int code,String message);
    }

    /**
     * zip压缩
     * @param src 原文件路径
     * @param dest 目标文件路径
     */
    public static void zipFile(String src,String dest,ZipFileListener listener){
        File srcFile = new File(src);
        String substring = src.substring(src.lastIndexOf("/")+1,src.lastIndexOf("."));
        File destFile = new File(dest,substring+".zip");
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(CompressionMethod.DEFLATE);
        parameters.setCompressionLevel(CompressionLevel.NORMAL);
        try {
            ZipFile zipFile = new ZipFile(destFile);
            zipFile.addFile(srcFile,parameters);
            listener.onZipFinish(true,destFile);
        }catch (Exception e){
            listener.onZipFinish(false,null);
            e.printStackTrace();
        }
    }

    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteFile(f);
            }
            file.delete();
        } else if (file.exists()) {
            file.delete();
        }
    }

    public interface ZipFileListener{
        void onZipFinish(boolean isSuccess,File destZipFile);
    }
}
