package com.example.palette.util;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

public class FileUtil {
    public static void writeToFile(InputStream inputStream,File file){
        FileOutputStream fileOutputStream=null;
        try {
            fileOutputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024*8];
            int length;
            while ((length=inputStream.read(buffer))!=-1){
                fileOutputStream.write(buffer,0,length);
                fileOutputStream.flush();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(fileOutputStream!=null){
                    fileOutputStream.close();
                }
                if(inputStream!=null){
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static int uncompressZip4j(String zip, String parentDirectoy, String password) {
        File zipFile_ = new File(zip);
        File sourceFile = new File(parentDirectoy);
        int result = -1;
        try {
            ZipFile zipFile = new ZipFile(zipFile_);
            zipFile.setFileNameCharset("GBK");
            if (!zipFile.isValidZipFile()){
                throw new ZipException("压缩文件不合法,可能被损坏.");
            }
            if (sourceFile.isDirectory() && !sourceFile.exists()) {
                sourceFile.mkdir();
            }
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password.toCharArray());
            }
            zipFile.extractAll(parentDirectoy);

        } catch (ZipException e) {
            result = -1;
            return result;
        }
        return result;
    }
    public static String getMd5ByFile(File file){
        FileInputStream in =null ;
        StringBuffer sb = new StringBuffer();
        try {
            in = new FileInputStream(file);
            FileChannel channel = in.getChannel();
            long position = 0;
            long total = file.length();
            long page = 1024 * 1024 * 500;
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            while (position < total) {
                long size = page <= total - position ? page : total - position;
                MappedByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, position, size);
                position += size;
                md5.update(byteBuffer);
            }
            byte[] b = md5.digest();

            for (int i = 0; i < b.length; i++) {
                sb.append(byteToChars(b[i]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString().toLowerCase();
    }
    private static char[] byteToChars(byte b) {
        int h = ((b & 0xf0) >> 4);
        int l = (b & 0x0f);
        char[] r = new char[2];
        r[0] = intToChart(h);
        r[1] = intToChart(l);

        return r;
    }
    private static char intToChart(int i) {
        if (i < 0 || i > 15) {
            return ' ';
        }
        if (i < 10) {
            return (char) (i + 48);
        } else {
            return (char) (i + 55);
        }
    }
}
