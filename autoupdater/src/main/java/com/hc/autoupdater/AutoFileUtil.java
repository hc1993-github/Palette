package com.hc.autoupdater;

import android.graphics.Bitmap;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AutoFileUtil {

    /**
     * zip解压文件
     * @param src 原文件路径
     * @param password 解压密码
     * @return
     */
    public static int unZipFile(String src,String password) {
        File zipFile_ = new File(src);
        File sourceFile = new File(zipFile_.getParent());
        int result = -1;
        try {
            ZipFile zipFile = new ZipFile(zipFile_);
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
            zipFile.extractAll(zipFile_.getParent());
            result = 0;
        } catch (ZipException e) {
            result = -1;
            return result;
        }
        return result;
    }

    /**
     * zip压缩文件
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

    public static long writeToFile(OutputStream outputStream, InputStream inputStream) {
        long writeTotalLength = 0;
        try {
            int currentReadLength;
            byte[] buffer = new byte[1024 * 8];
            while ((currentReadLength = inputStream.read(buffer)) != -1){
                outputStream.write(buffer,0,currentReadLength);
                outputStream.flush();
                writeTotalLength += currentReadLength;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(outputStream!=null){
                    outputStream.close();
                }
                if(inputStream!=null){
                    inputStream.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return writeTotalLength;
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

    public static File getApkFile(File file) {
        File fileParent = new File(file.getParent());
        File[] files = fileParent.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().contains("apk")) {
                return files[i];
            }
        }
        return null;
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
