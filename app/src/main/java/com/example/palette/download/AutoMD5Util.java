package com.example.palette.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AutoMD5Util {

    private static final char[] HEX_DIGITS_UPPER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final char[] HEX_DIGITS_LOWER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static boolean compareMD5IgnoreCase(String remoteMD5, File file){
        String fileMD5 = getFileMD5(file);
        if(remoteMD5.equalsIgnoreCase(fileMD5)){
            return true;
        }else {
            return false;
        }
    }

    public static boolean compareMD5(String remoteMD5,File file){
        String fileMD5 = getFileMD5(file);
        if(remoteMD5.equals(fileMD5)){
            return true;
        }else {
            return false;
        }
    }

    public static String getStringMD5(String src){
        return md5(src.getBytes());
    }

    public static String getFileMD5(File file){
        return bytes2HexString(encryptMD5File(file),true);
    }

    private static byte[] encryptMD5File(final File file) {
        if (file == null) return null;
        FileInputStream fis = null;
        DigestInputStream digestInputStream;
        try {
            fis = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("MD5");
            digestInputStream = new DigestInputStream(fis, md);
            byte[] buffer = new byte[256 * 1024];
            while (true) {
                if (!(digestInputStream.read(buffer) > 0)) break;
            }
            md = digestInputStream.getMessageDigest();
            return md.digest();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String bytes2HexString(final byte[] bytes, boolean isUpperCase) {
        if (bytes == null) return "";
        char[] hexDigits = isUpperCase ? HEX_DIGITS_UPPER : HEX_DIGITS_LOWER;
        int len = bytes.length;
        if (len <= 0) return "";
        char[] ret = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            ret[j++] = hexDigits[bytes[i] >> 4 & 0x0f];
            ret[j++] = hexDigits[bytes[i] & 0x0f];
        }
        return new String(ret);
    }

    private static String md5(byte[] buffer){
        String result=null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            result=toHexString(md.digest(buffer));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String toHexString(byte[] bs){
        StringBuilder sb = new StringBuilder();
        if (bs == null || bs.length <= 0) {
            return null;
        }
        for (byte b : bs) {
            int i = b & 0xff;
            String hexString = Integer.toHexString(i);
            if(hexString.length() < 2){
                hexString = "0" + hexString;
            }
            sb.append(hexString);
        }
        return sb.toString();
    }
}
