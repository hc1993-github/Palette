package com.example.palette.util;

import android.graphics.Bitmap;
import android.text.TextUtils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class FileUtil {
    /**
     * zip解压文件
     *
     * @param src      原文件路径
     * @param password 解压密码
     * @return
     */
    public static void unzip(String src, String password, UnZipListener unZipListener) {
        new Thread() {
            @Override
            public void run() {
                File srcFile;
                File sourceFile = null;
                try {
                    srcFile = new File(src);
                    sourceFile = new File(srcFile.getParent());
                    ZipFile zipFile = new ZipFile(srcFile);
                    zipFile.setCharset(Charset.forName("GBK"));
                    if (!zipFile.isValidZipFile()) {
                        unZipListener.onUnZipFinish(false, sourceFile.exists() ? sourceFile.getAbsolutePath() : null);
                    }
                    if (sourceFile.isDirectory() && !sourceFile.exists()) {
                        sourceFile.mkdir();
                    }
                    if (zipFile.isEncrypted()) {
                        zipFile.setPassword(password.toCharArray());
                    }
                    unZipListener.onUnZipIng();
                    zipFile.extractAll(srcFile.getParent());
                    unZipListener.onUnZipFinish(true, sourceFile.getAbsolutePath());
                } catch (Exception e) {
                    unZipListener.onUnZipFinish(false, sourceFile.exists() ? sourceFile.getAbsolutePath() : null);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * zip压缩文件
     *
     * @param src 文件名或文件夹
     * @param pwd 压缩密码
     * @return
     */
    public static void zip(String src, String pwd, ZipListener zipListener) {
        new Thread() {
            @Override
            public void run() {
                String destPath = null;
                try {
                    File srcFile = new File(src);
                    destPath = generateDestPath(srcFile, srcFile.getParent());
                    ZipParameters parameters = new ZipParameters();
                    parameters.setCompressionMethod(CompressionMethod.DEFLATE);
                    parameters.setCompressionLevel(CompressionLevel.NORMAL);
                    if (!TextUtils.isEmpty(pwd)) {
                        parameters.setEncryptFiles(true);
                        parameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
                    }
                    ZipFile zipFile = new ZipFile(destPath, pwd.toCharArray());
                    zipFile.setCharset(Charset.forName("GBK"));
                    zipListener.onZipIng();
                    if (srcFile.isDirectory()) {
                        zipFile.addFolder(srcFile, parameters);
                    } else {
                        zipFile.addFile(srcFile, parameters);
                    }
                    zipListener.onZipFinish(true, destPath);
                } catch (Exception e) {
                    zipListener.onZipFinish(false, TextUtils.isEmpty(destPath) ? null : destPath);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 删除文件,文件夹及子内容
     * 耗时操作 注意线程问题
     */
    public static void delete(String path) {
        try {
            File file = new File(path);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    File f = files[i];
                    delete(f.getAbsolutePath());
                }
                file.delete();
            } else if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件,文件夹及子内容
     * 耗时操作 注意线程问题
     */
    public static void delete(File file) {
        try {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    File f = files[i];
                    delete(f);
                }
                file.delete();
            } else if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件复制
     * 耗时操作 注意线程问题
     * @param srcFile 源文件
     * @param destDir 目标目录
     * @return 是否成功
     */
    public static boolean copy(File srcFile, File destDir) {
        FileOutputStream fosDest = null;
        FileInputStream fisSrc = null;
        try {
            if (srcFile.exists() && !srcFile.isDirectory() && destDir.exists() && destDir.isDirectory()) {
                fosDest = new FileOutputStream(destDir+File.separator+srcFile.getName());
                fisSrc = new FileInputStream(srcFile);
                byte[] buffer = new byte[1024 * 8];
                int length;
                while ((length = fisSrc.read(buffer)) != -1) {
                    fosDest.write(buffer, 0, length);
                    fosDest.flush();
                }
                return true;
            }else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fosDest != null) {
                    fosDest.close();
                }
                if (fisSrc != null) {
                    fisSrc.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 文件复制
     * 耗时操作 注意线程问题
     * @param srcFileAbsolutePath 源文件绝对路径
     * @param destDir 目标目录
     * @return 是否成功
     */
    public static boolean copy(String srcFileAbsolutePath,File destDir){
        FileOutputStream fosDest = null;
        FileInputStream fisSrc = null;
        try {
            File srcFile = new File(srcFileAbsolutePath);
            if(srcFile.exists() && !srcFile.isDirectory()){
                if(destDir.exists() && destDir.isDirectory()){
                    fosDest = new FileOutputStream(destDir+File.separator+srcFile.getName());
                    fisSrc = new FileInputStream(srcFile);
                    byte[] buffer = new byte[1024 * 8];
                    int length;
                    while ((length = fisSrc.read(buffer)) != -1) {
                        fosDest.write(buffer, 0, length);
                        fosDest.flush();
                    }
                    return true;
                }else {
                    return false;
                }
            }else {
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fosDest != null) {
                    fosDest.close();
                }
                if (fisSrc != null) {
                    fisSrc.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 文件复制
     * 耗时操作 注意线程问题
     * @param srcFile 源文件
     * @param destDirAbsolutePath 目标目录绝对路径
     * @return 是否成功
     */
    public static boolean copy(File srcFile,String destDirAbsolutePath){
        FileOutputStream fosDest = null;
        FileInputStream fisSrc = null;
        try {
            if(srcFile.exists() && !srcFile.isDirectory()){
                File destDir = new File(destDirAbsolutePath);
                if(destDir.exists() && destDir.isDirectory()){
                    File destFile = new File(destDirAbsolutePath+File.separator+srcFile.getName());
                    fosDest = new FileOutputStream(destFile);
                    fisSrc = new FileInputStream(srcFile);
                    byte[] buffer = new byte[1024 * 8];
                    int length;
                    while ((length = fisSrc.read(buffer)) != -1) {
                        fosDest.write(buffer, 0, length);
                        fosDest.flush();
                    }
                    return true;
                }else {
                    return false;
                }
            }else {
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            try {
                if (fosDest != null) {
                    fosDest.close();
                }
                if (fisSrc != null) {
                    fisSrc.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 文件复制
     * 耗时操作 注意线程问题
     * @param srcFileAbsolutePath 源文件绝对路径
     * @param destDirAbsolutePath 目标目录绝对路径
     * @return 是否成功
     */
    public static boolean copy(String srcFileAbsolutePath,String destDirAbsolutePath){
        FileOutputStream fosDest = null;
        FileInputStream fisSrc = null;
        try {
            File srcFile = new File(srcFileAbsolutePath);
            if(srcFile.exists() && !srcFile.isDirectory()){
                File destDir = new File(destDirAbsolutePath);
                if(destDir.exists() && destDir.isDirectory()){
                    File destFile = new File(destDirAbsolutePath+File.separator+srcFile.getName());
                    fosDest = new FileOutputStream(destFile);
                    fisSrc = new FileInputStream(srcFile);
                    byte[] buffer = new byte[1024 * 8];
                    int length;
                    while ((length = fisSrc.read(buffer)) != -1) {
                        fosDest.write(buffer, 0, length);
                        fosDest.flush();
                    }
                    return true;
                }else {
                    return false;
                }
            }else {
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            try {
                if (fosDest != null) {
                    fosDest.close();
                }
                if (fisSrc != null) {
                    fisSrc.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 写出到文件
     * 耗时操作 注意线程问题
     * @param destAbsolutePath 目标文件绝对路径
     * @param stream 输入流
     * @return 是否成功
     */
    public static boolean writeToFile(String destAbsolutePath, InputStream stream) {
        FileOutputStream fileOutputStream = null;
        try {
            File file = new File(destAbsolutePath);
            if(stream != null){
                fileOutputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024 * 8];
                int length;
                while ((length = stream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, length);
                    fileOutputStream.flush();
                }
                return true;
            }else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (stream != null) {
                    stream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 写出到文件
     * 耗时操作 注意线程问题
     * @param destAbsolutePath 目标文件绝对路径
     * @param data 字节数组
     * @return 是否成功
     */
    public static boolean writeToFile(String destAbsolutePath, byte[] data) {
        FileOutputStream fos = null;
        try {
            File file = new File(destAbsolutePath);
            if(data != null){
                fos = new FileOutputStream(file);
                fos.write(data);
                fos.flush();
                return true;
            }else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 写出到文件
     * 耗时操作 注意线程问题
     * @param destAbsolutePath 目标文件绝对路径
     * @param bitmap 位图
     * @return 是否成功
     */
    public static boolean writeToFile(String destAbsolutePath, Bitmap bitmap) {
        FileOutputStream fos = null;
        try {
            if(bitmap!=null){
                File file = new File(destAbsolutePath);
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                return true;
            }else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 写出到文件
     * 耗时操作 注意线程问题
     * @param destAbsolutePath 目标文件绝对路径
     * @param string 字符串
     * @return 是否成功
     */
    public static boolean writeToFile(String destAbsolutePath, String string) {
        FileOutputStream fos = null;
        try {
            if(!TextUtils.isEmpty(string)){
                File file = new File(destAbsolutePath);
                fos = new FileOutputStream(file);
                fos.write(string.getBytes("utf-8"));
                fos.flush();
                return true;
            }else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从文件读取
     * 耗时操作 注意线程问题
     * @param srcAbsolutePath 源文件绝对路径
     * @return 字符串
     */
    public static String readFromFile(String srcAbsolutePath){
        InputStream inputStream=null;
        InputStreamReader inputStreamReader=null;
        try {
            File file = new File(srcAbsolutePath);
            if(file.exists() && !file.isDirectory() && file.canRead()){
                StringBuilder stringBuilder = new StringBuilder();
                inputStream = new FileInputStream(file);
                inputStreamReader = new InputStreamReader(inputStream,"utf-8");
                int length;
                char[] buffer = new char[1024 * 4];
                while ((length=inputStreamReader.read(buffer)) !=-1){
                    stringBuilder.append(buffer,0,length);
                }
                return stringBuilder.toString();
            }else {
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从文件读取
     * 耗时操作 注意线程问题
     * @param file 源文件
     * @return 字符串
     */
    public static String readFromFile(File file){
        InputStream inputStream=null;
        InputStreamReader inputStreamReader=null;
        try {
            if(file.exists() && !file.isDirectory() && file.canRead()){
                StringBuilder stringBuilder = new StringBuilder();
                inputStream = new FileInputStream(file);
                inputStreamReader = new InputStreamReader(inputStream,"utf-8");
                int length;
                char[] buffer = new char[1024 * 4];
                while ((length=inputStreamReader.read(buffer)) !=-1){
                    stringBuilder.append(buffer,0,length);
                }
                return stringBuilder.toString();
            }else {
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String generateDestPath(File srcFile, String dest) {
        if (TextUtils.isEmpty(dest)) {
            if (srcFile.isDirectory()) {
                dest = srcFile.getParent() + File.separator + srcFile.getName() + ".zip";
            } else {
                String fileName = srcFile.getName().substring(0, srcFile.getName().lastIndexOf("."));
                dest = srcFile.getParent() + File.separator + fileName + ".zip";
            }
        } else {
            File destDir;
            if (dest.endsWith(File.separator)) {
                destDir = new File(dest);
                String fileName;
                if (srcFile.isDirectory()) {
                    fileName = srcFile.getName();
                } else {
                    fileName = srcFile.getName().substring(0, srcFile.getName().lastIndexOf("."));
                }
                dest += fileName + ".zip";
            } else {
                destDir = new File(dest.substring(0, dest.lastIndexOf(File.separator)));
            }
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
        }
        return dest;
    }

    public interface UnZipListener {
        void onUnZipIng();

        void onUnZipFinish(boolean isSuccess, String destFilePath);
    }

    public interface ZipListener {
        void onZipIng();

        void onZipFinish(boolean isSuccess, String destFilePath);
    }
}
