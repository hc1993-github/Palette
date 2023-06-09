package com.example.palette.encrypt;


import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author: zzy
 * @create: 2019/12/24
 */
public class SM3Util extends GMBaseUtil{





    public static String hash32(String src){
        return ByteUtils.toHexString(hash(src.getBytes(StandardCharsets.UTF_8))).substring(0, 32);
    }

    public static byte[] hash(byte[] srcData) {
        SM3Digest digest = new SM3Digest();
        digest.update(srcData, 0, srcData.length);
        byte[] hash = new byte[digest.getDigestSize()];
        digest.doFinal(hash, 0);
        return hash;
    }

    public static boolean verify(byte[] srcData, byte[] sm3Hash) {
        byte[] newHash = hash(srcData);
        if (Arrays.equals(newHash, sm3Hash)) {
            return true;
        } else {
            return false;
        }
    }

    public static byte[] hmac(byte[] key, byte[] srcData) {
        KeyParameter keyParameter = new KeyParameter(key);
        SM3Digest digest = new SM3Digest();
        HMac mac = new HMac(digest);
        mac.init(keyParameter);
        mac.update(srcData, 0, srcData.length);
        byte[] result = new byte[mac.getMacSize()];
        mac.doFinal(result, 0);
        return result;
    }
    
    public static void main(String[] args) {
//
//    	String text1 = "sickcontrolapp"+ TimeUtils.getNowString(new SimpleDateFormat("yyyyMMdd"));
//    	String text2 = "你好hello world +)##$%";
//    	String text3 = "你好hello world +)##$%1";
//        String SECRET = ByteUtils.toHexString(SM3Util.hash(("sickcontrolapp" + TimeUtils.getNowString(new SimpleDateFormat("yyyyMMdd"))).getBytes())).substring(0, 32);
//
//        String hash1 = ByteUtils.toHexString(SM3Util.hash(text1.getBytes()));
//    	String hash2 = ByteUtils.toHexString(SM3Util.hash(text2.getBytes()));
//    	String hash3 = ByteUtils.toHexString(SM3Util.hash(text3.getBytes()));
//        System.out.println("SECRET="+SECRET);
//    	System.out.println("text1="+text1 + " \t哈希值= " + hash1);
//    	System.out.println("text2="+text2 + " \t哈希值= " + hash2);
//    	System.out.println("text1和text2 " + (text1.equals(text2) ? "相同":"不同") + ", 哈希值 " + (hash1.equals(hash2)?"相同\n":"不同\n"));
//    	System.out.println("text3="+text3 + " \t哈希值= " + hash3);
//    	System.out.println("text2和text3 " + (text2.equals(text3) ? "相同":"不同") + ", 哈希值 " + (hash2.equals(hash3)?"相同":"不同"));
    }

}
