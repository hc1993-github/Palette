package com.example.palette.util;

import android.graphics.Bitmap;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {

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
    public static void zipFile(String src,String dest){
        File srcFile = new File(src);
        File destFile = new File(dest);
        FileInputStream fis = null;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);
            ZipEntry zipEntry = new ZipEntry(srcFile.getName());
            zos = new ZipOutputStream(fos);
            zos.putNextEntry(zipEntry);
            byte[] buffer=new byte[1024];
            int len = 0;
            while((len =  fis.read(buffer)) != -1) {
                zos.write(buffer,0,len);
            }
            zos.flush();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if(fis!=null){
                    fis.close();
                }
                if(zos!=null){
                    zos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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
}
