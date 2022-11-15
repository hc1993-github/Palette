package com.example.palette.util;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SecurityUtil {
    private static final String DES_ALGORITHM = "DES";
    private static final String AES_ALGORITHM = "AES";
    private static final String RSA_ALGORITHM = "RSA";
    private static final String DES_TRANSFORMATION = "DES/CBC/PKCS5Padding";
    private static final String AES_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private static final String CHARSET = "utf-8";
    private static final String SHA1PRNG = "SHA1PRNG";
    private static final String IV_PARAMETER = "88886666";
    private static final int DEFAULT = Base64.DEFAULT;//带有换行符
    private static final int NO_PADDING = Base64.NO_PADDING;//省略最后的=
    private static final int CRLF = Base64.CRLF;//使用CRLF为换行符
    private static final int NO_WRAP = Base64.NO_WRAP;//去掉换行符
    private static final int URL_SAFE = Base64.URL_SAFE;//-_代替+/
    private static final int NO_CLOSE = Base64.NO_CLOSE;//当Base64OutputStream封闭时不关闭流
    private static final int DEFAULT_RSA_KEY_SIZE = 1024;
    private static final int DEFAULT_RSA_BUFFERSIZE = DEFAULT_RSA_KEY_SIZE / 8 - 11;
    public static final byte[] DEFAULT_SPLIT = "#PART#".getBytes();

    /**
     * DES加密字符串
     *
     * @param pwd  加密密码
     * @param data 待加密数据
     * @param type 加密方式
     * @return 加密字符串
     */
    public static String encryptStringDES(String pwd, String data, int type) {
        if (pwd == null || pwd.length() < 8) {
            throw new RuntimeException("encryptStringDES failed , the pwd is invalid");
        }
        if (data == null) {
            throw new RuntimeException("encryptStringDES failed , the data is invalid");
        }
        try {
            Key key = generateDESKey(pwd);
            Cipher cipher = Cipher.getInstance(DES_ALGORITHM);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(IV_PARAMETER.getBytes(CHARSET));
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
            return new String(Base64.encode(cipher.doFinal(data.getBytes(CHARSET)), type));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DES解密字符串
     *
     * @param pwd  解密密码
     * @param data 待解密数据
     * @param type 解密方式
     * @return 解密字符串
     */
    public static String decryptStringDES(String pwd, String data, int type) {
        if (pwd == null || pwd.length() < 8) {
            throw new RuntimeException("decryptStringDES failed , the pwd is invalid");
        }
        if (data == null) {
            throw new RuntimeException("decryptStringDES failed , the data is invalid");
        }
        try {
            Key key = generateDESKey(pwd);
            Cipher cipher = Cipher.getInstance(DES_ALGORITHM);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(IV_PARAMETER.getBytes(CHARSET));
            cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
            return new String(cipher.doFinal(Base64.decode(data.getBytes(CHARSET), type)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DES加密文件
     *
     * @param pwd      加密密码
     * @param srcPath  待加密文件路径
     * @param destPath 加密文件路径
     * @return 加密文件路径
     */
    public static String encryptFileDES(String pwd, String srcPath, String destPath) {
        if (pwd == null || pwd.length() < 8) {
            throw new RuntimeException("encryptFileDES failed , the pwd is invalid");
        }
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(IV_PARAMETER.getBytes(CHARSET));
            Cipher cipher = Cipher.getInstance(DES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, generateDESKey(pwd), ivParameterSpec);
            FileInputStream fis = new FileInputStream(new File(srcPath));
            FileOutputStream fos = new FileOutputStream(new File(destPath));
            CipherInputStream cis = new CipherInputStream(fis, cipher);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = cis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
                fos.flush();
            }
            cis.close();
            fis.close();
            fos.close();
            return destPath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DES解密文件
     *
     * @param pwd      解密密码
     * @param srcPath  待解密文件路径
     * @param destPath 解密文件路径
     * @return 解密文件路径
     */
    public static String decryptFileDES(String pwd, String srcPath, String destPath) {
        if (pwd == null || pwd.length() < 8) {
            throw new RuntimeException("decryptFileDES failed , the pwd is invalid");
        }
        try {
            File file = new File(destPath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER.getBytes(CHARSET));
            Cipher cipher = Cipher.getInstance(DES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, generateDESKey(pwd), iv);
            FileInputStream is = new FileInputStream(new File(srcPath));
            FileOutputStream out = new FileOutputStream(file);
            CipherOutputStream cos = new CipherOutputStream(out, cipher);
            byte[] buffer = new byte[1024];
            int r;
            while ((r = is.read(buffer)) >= 0) {
                cos.write(buffer, 0, r);
            }
            cos.close();
            is.close();
            out.close();
            return destPath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Key generateDESKey(String pwd) throws Exception {
        DESKeySpec desKeySpec = new DESKeySpec(pwd.getBytes(CHARSET));
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(DES_ALGORITHM);
        return secretKeyFactory.generateSecret(desKeySpec);
    }

    /**
     * 获取字符串MD5值
     *
     * @param data
     * @return
     */
    public static String generateStringMD5(String data) {
        if (TextUtils.isEmpty(data)) {
            throw new RuntimeException("generateStringMD5 failed , the data is invalid");
        }
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(data.getBytes());
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                builder.append(temp);
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取文件MD5值
     *
     * @param file
     * @return
     */
    public static String generateFileMD5(File file) {
        if (file == null || !file.isFile() || !file.exists()) {
            throw new RuntimeException("generateFileMD5 failed , the file is invalid");
        }
        FileInputStream in = null;
        StringBuilder builder = new StringBuilder();
        byte buffer[] = new byte[1024];
        int len;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer)) != -1) {
                md5.update(buffer, 0, len);
            }
            byte[] bytes = md5.digest();

            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                builder.append(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }

    /**
     * AES加密字符串
     *
     * @param pwd  加密密码
     * @param data 待加密数据
     * @return 加密字符串
     */
    public static String encryptStringAES(String pwd, String data) {
        if (TextUtils.isEmpty(data)) {
            throw new RuntimeException("encryptStringAES failed , the data is invalid");
        }
        try {
            byte[] key = generateAESKey(pwd.getBytes(CHARSET));
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
            byte[] bytes = cipher.doFinal(data.getBytes(CHARSET));
            return Base64Encoder.encode(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * AES解密字符串
     *
     * @param pwd  解密密码
     * @param data 待解密数据
     * @return 解密字符串
     */
    public static String decryptStringAES(String pwd, String data) {
        if (TextUtils.isEmpty(data)) {
            throw new RuntimeException("decryptStringAES failed , the data is invalid");
        }
        try {
            byte[] bytes = Base64Decoder.decodeToBytes(data);
            byte[] key = generateAESKey(pwd.getBytes(CHARSET));
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
            byte[] datas = cipher.doFinal(bytes);
            return new String(datas);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * AES加密文件
     *
     * @param pwd      加密密码
     * @param srcPath  待加密文件路径
     * @param destPath 加密文件路径
     * @return 加密文件路径
     */
    @SuppressLint("DeletedProvider")
    public static String encryptFileAES(String pwd, String srcPath, String destPath) {
        File srcFile = new File(srcPath);
        File destFile = new File(destPath);
        FileInputStream fis;
        FileOutputStream fos;
        CipherInputStream cis;
        try {
            if (destFile.exists()) {
                destFile.delete();
            }
            destFile.createNewFile();
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);
            byte[] keyBytes = generateAESKey(pwd.getBytes(CHARSET));
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            cis = new CipherInputStream(fis, cipher);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = cis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                fos.flush();
            }
            fos.close();
            cis.close();
            fis.close();
            return destPath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * AES解密文件
     *
     * @param pwd      解密密码
     * @param srcPath  待解密文件路径
     * @param destPath 解密文件路径
     * @return 解密文件路径
     */
    public static String decryptFileAES(String pwd, String srcPath, String destPath) {
        File srcFile = new File(srcPath);
        File destFile = new File(destPath);
        FileInputStream fis;
        FileOutputStream fos;
        CipherOutputStream cos;
        try {
            if (destFile.exists()) {
                destFile.delete();
            }
            destFile.createNewFile();
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);
            byte[] keyBytes = generateAESKey(pwd.getBytes(CHARSET));
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
            cos = new CipherOutputStream(fos, cipher);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, len);
                cos.flush();
            }
            cos.close();
            fos.close();
            fis.close();
            return destPath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * RSA公钥加密
     *
     * @param pwd
     * @param data
     * @param type
     * @return
     */
    public static String encryptStringRSAPublic(String pwd, String data, int type) {
        try {
            byte[] dataBytes = data.getBytes(CHARSET);
            if (dataBytes.length <= DEFAULT_RSA_BUFFERSIZE) {
                return encryptRSAPublic(pwd, data, type);
            }
            List<Byte> allBytes = new ArrayList<>(2048);
            int bufIndex = 0;
            int subDataLoop = 0;
            byte[] buf = new byte[DEFAULT_RSA_BUFFERSIZE];
            for (int i = 0; i < dataBytes.length; i++) {
                buf[bufIndex] = dataBytes[i];
                if (++bufIndex == DEFAULT_RSA_BUFFERSIZE || i == dataBytes.length - 1) {
                    subDataLoop++;
                    if (subDataLoop != 1) {
                        for (byte b : DEFAULT_SPLIT) {
                            allBytes.add(b);
                        }
                    }
                    byte[] encryptBytes = encryptRSAPublic(pwd, new String(buf), type).getBytes(CHARSET);
                    for (byte b : encryptBytes) {
                        allBytes.add(b);
                    }
                    bufIndex = 0;
                    if (i == dataBytes.length - 1) {
                        buf = null;
                    } else {
                        buf = new byte[Math.min(DEFAULT_RSA_BUFFERSIZE, dataBytes.length - i - 1)];
                    }
                }
            }
            byte[] bytes = new byte[allBytes.size()];
            {
                int i = 0;
                for (Byte b : allBytes) {
                    bytes[i++] = b.byteValue();
                }
            }
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * RSA私钥加密
     * @param pwd
     * @param data
     * @param type
     * @return
     */
//    public static String encryptStringRSAPrivate(String pwd,String data,int type){
//        try {
//            byte[] dataBytes = data.getBytes(CHARSET);
//            if(dataBytes.length<=DEFAULT_RSA_BUFFERSIZE){
//                return encryptRSAPrivate(pwd, data, type);
//            }
//            List<Byte> allBytes = new ArrayList<>(2048);
//            int bufIndex = 0;
//            int subDataLoop = 0;
//            byte[] buf = new byte[DEFAULT_RSA_BUFFERSIZE];
//            for (int i = 0; i < dataBytes.length; i++) {
//                buf[bufIndex] = dataBytes[i];
//                if (++bufIndex == DEFAULT_RSA_BUFFERSIZE || i == dataBytes.length - 1) {
//                    subDataLoop++;
//                    if (subDataLoop != 1) {
//                        for (byte b : DEFAULT_SPLIT) {
//                            allBytes.add(b);
//                        }
//                    }
//                    byte[] encryptBytes = encryptRSAPrivate(pwd, new String(buf),type).getBytes(CHARSET);
//                    for (byte b : encryptBytes) {
//                        allBytes.add(b);
//                    }
//                    bufIndex = 0;
//                    if (i == dataBytes.length - 1) {
//                        buf = null;
//                    } else {
//                        buf = new byte[Math.min(DEFAULT_RSA_BUFFERSIZE, dataBytes.length - i - 1)];
//                    }
//                }
//            }
//            byte[] bytes = new byte[allBytes.size()];
//            {
//                int i = 0;
//                for (Byte b : allBytes) {
//                    bytes[i++] = b.byteValue();
//                }
//            }
//            return new String(bytes);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return null;
//    }

    /**
     * RSA公钥解密
     * @param pwd
     * @param data
     * @param type
     * @return
     */
//    public static String decryptStringRSAPublic(String pwd,String data,int type){
//        try {
//            if(DEFAULT_SPLIT.length<=0){
//                return decryptRSAPublic(pwd, data, type);
//            }
//            byte[] dataBytes = data.getBytes(CHARSET);
//            int dataLen = dataBytes.length;
//            List<Byte> allBytes = new ArrayList<>(1024);
//            int latestStartIndex = 0;
//            for (int i = 0; i < dataLen; i++) {
//                byte bt = dataBytes[i];
//                boolean isMatchSplit = false;
//                if (i == dataLen - 1) {
//                    byte[] part = new byte[dataLen - latestStartIndex];
//                    System.arraycopy(dataBytes, latestStartIndex, part, 0, part.length);
//                    byte[] decryptPart = decryptRSAPublic(pwd,new String(part),type).getBytes(CHARSET);
//                    for (byte b : decryptPart) {
//                        allBytes.add(b);
//                    }
//                    latestStartIndex = i + DEFAULT_SPLIT.length;
//                    i = latestStartIndex - 1;
//                } else if (bt == DEFAULT_SPLIT[0]) {
//                    if (DEFAULT_SPLIT.length > 1) {
//                        if (i + DEFAULT_SPLIT.length < dataLen) {
//                            for (int j = 1; j < DEFAULT_SPLIT.length; j++) {
//                                if (DEFAULT_SPLIT[j] != dataBytes[i + j]) {
//                                    break;
//                                }
//                                if (j == DEFAULT_SPLIT.length - 1) {
//                                    isMatchSplit = true;
//                                }
//                            }
//                        }
//                    } else {
//                        isMatchSplit = true;
//                    }
//                }
//                if (isMatchSplit) {
//                    byte[] part = new byte[i - latestStartIndex];
//                    System.arraycopy(dataBytes, latestStartIndex, part, 0, part.length);
//                    byte[] decryptPart = decryptRSAPublic(pwd,new String(part),type).getBytes(CHARSET);
//                    for (byte b : decryptPart) {
//                        allBytes.add(b);
//                    }
//                    latestStartIndex = i + DEFAULT_SPLIT.length;
//                    i = latestStartIndex - 1;
//                }
//            }
//            byte[] bytes = new byte[allBytes.size()];
//            {
//                int i = 0;
//                for (Byte b : allBytes) {
//                    bytes[i++] = b.byteValue();
//                }
//            }
//            return new String(bytes);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return null;
//    }

    /**
     * RSA私钥解密
     *
     * @param pwd
     * @param data
     * @param type
     * @return
     */
    public static String decryptStringRSAPrivate(String pwd, String data, int type) {
        try {
            if (DEFAULT_SPLIT.length <= 0) {
                return decryptRSAPrivate(pwd, data, type);
            }
            byte[] encrypted = data.getBytes(CHARSET);
            int dataLen = encrypted.length;
            List<Byte> allBytes = new ArrayList<>(1024);
            int latestStartIndex = 0;
            for (int i = 0; i < dataLen; i++) {
                byte bt = encrypted[i];
                boolean isMatchSplit = false;
                if (i == dataLen - 1) {
                    byte[] part = new byte[dataLen - latestStartIndex];
                    System.arraycopy(encrypted, latestStartIndex, part, 0, part.length);
                    byte[] decryptPart = decryptRSAPrivate(pwd, new String(part), type).getBytes(CHARSET);
                    for (byte b : decryptPart) {
                        allBytes.add(b);
                    }
                    latestStartIndex = i + DEFAULT_SPLIT.length;
                    i = latestStartIndex - 1;
                } else if (bt == DEFAULT_SPLIT[0]) {
                    if (DEFAULT_SPLIT.length > 1) {
                        if (i + DEFAULT_SPLIT.length < dataLen) {
                            for (int j = 1; j < DEFAULT_SPLIT.length; j++) {
                                if (DEFAULT_SPLIT[j] != encrypted[i + j]) {
                                    break;
                                }
                                if (j == DEFAULT_SPLIT.length - 1) {
                                    isMatchSplit = true;
                                }
                            }
                        }
                    } else {
                        isMatchSplit = true;
                    }
                }
                if (isMatchSplit) {
                    byte[] part = new byte[i - latestStartIndex];
                    System.arraycopy(encrypted, latestStartIndex, part, 0, part.length);
                    byte[] decryptPart = decryptRSAPrivate(pwd, new String(part), type).getBytes(CHARSET);
                    for (byte b : decryptPart) {
                        allBytes.add(b);
                    }
                    latestStartIndex = i + DEFAULT_SPLIT.length;
                    i = latestStartIndex - 1;
                }
            }
            byte[] bytes = new byte[allBytes.size()];
            {
                int i = 0;
                for (Byte b : allBytes) {
                    bytes[i++] = b.byteValue();
                }
            }
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String encryptRSAPublic(String pwd, String data, int type) {
        try {
            byte[] bytes = Base64.decode(pwd, type);
            RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance(RSA_ALGORITHM).generatePublic(new X509EncodedKeySpec(bytes));
            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.encodeToString(cipher.doFinal(data.getBytes(CHARSET)), type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    private static String decryptRSAPublic(String pwd,String data,int type){
//        try {
//            byte[] keyBytes = Base64.decode(pwd,type);
//            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
//            Key key = KeyFactory.getInstance(RSA_ALGORITHM).generatePublic(x509EncodedKeySpec);
//            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
//            cipher.init(Cipher.DECRYPT_MODE,key);
//            return new String(cipher.doFinal(data.getBytes(CHARSET)));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return null;
//    }

    private static String decryptRSAPrivate(String pwd, String data, int type) {
        try {
            byte[] bytes = Base64.decode(data.getBytes(CHARSET), type);
            byte[] keyBytes = Base64.decode(pwd, type);
            RSAPrivateKey privateKey = (RSAPrivateKey) KeyFactory.getInstance(RSA_ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(bytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    private static String encryptRSAPrivate(String pwd,String data,int type){
//        try {
//            byte[] keyBytes = Base64.decode(pwd,type);
//            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
//            Key key = KeyFactory.getInstance(RSA_ALGORITHM).generatePrivate(pkcs8EncodedKeySpec);
//            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
//            cipher.init(Cipher.ENCRYPT_MODE,key);
//            return Base64.encodeToString(cipher.doFinal(data.getBytes(CHARSET)),type);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return null;
//    }

    /**
     * 生成RSA秘钥对
     *
     * @return [0]公钥[1]私钥
     */
    public static String[] generateRSAKeyPair() {
        String[] keys = new String[2];
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
            keyPairGenerator.initialize(DEFAULT_RSA_KEY_SIZE);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            String privateKey = new String(Base64.encode(keyPair.getPrivate().getEncoded(), Base64.NO_WRAP));
            String publicKey = new String(Base64.encode(keyPair.getPublic().getEncoded(), Base64.NO_WRAP));
            keys[0] = publicKey;
            keys[1] = privateKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keys;
    }

    @SuppressLint("DeletedProvider")
    private static byte[] generateAESKey(byte[] key) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
        SecureRandom secureRandom = null;
        if (Build.VERSION.SDK_INT >= 17) {
            secureRandom = SecureRandom.getInstance(SHA1PRNG, "Crypto");
        } else {
            secureRandom = SecureRandom.getInstance(SHA1PRNG);
        }
        secureRandom.setSeed(key);
        keyGenerator.init(128, secureRandom);//128 or 192 or 256
        SecretKey secretKey = keyGenerator.generateKey();
        return secretKey.getEncoded();
    }

    public static class Base64Decoder extends FilterInputStream {

        private static final char[] chars = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
                'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

        private static final int[] ints = new int[128];

        static {
            for (int i = 0; i < 64; i++) {
                ints[chars[i]] = i;
            }
        }

        private int charCount;
        private int carryOver;


        private Base64Decoder(InputStream in) {
            super(in);
        }

        public int read() throws IOException {
            // Read the next non-whitespace character
            int x;
            do {
                x = in.read();
                if (x == -1) {
                    return -1;
                }
            } while (Character.isWhitespace((char) x));
            charCount++;

            // The '=' sign is just padding
            if (x == '=') {
                return -1; // effective end of stream
            }

            // Convert from raw form to 6-bit form
            x = ints[x];

            // Calculate which character we're decoding now
            int mode = (charCount - 1) % 4;

            // First char save all six bits, go for another
            if (mode == 0) {
                carryOver = x & 63;
                return read();
            }
            // Second char use previous six bits and first two new bits,
            // save last four bits
            else if (mode == 1) {
                int decoded = ((carryOver << 2) + (x >> 4)) & 255;
                carryOver = x & 15;
                return decoded;
            }
            // Third char use previous four bits and first four new bits,
            // save last two bits
            else if (mode == 2) {
                int decoded = ((carryOver << 4) + (x >> 2)) & 255;
                carryOver = x & 3;
                return decoded;
            }
            // Fourth char use previous two bits and all six new bits
            else if (mode == 3) {
                int decoded = ((carryOver << 6) + x) & 255;
                return decoded;
            }
            return -1; // can't actually reach this line
        }

        public int read(byte[] buf, int off, int len) throws IOException {
            if (buf.length < (len + off - 1)) {
                throw new IOException("The input buffer is too small: " + len + " bytes requested starting at offset " + off + " while the buffer " + " is only " + buf.length + " bytes long.");
            }

            // This could of course be optimized
            int i;
            for (i = 0; i < len; i++) {
                int x = read();
                if (x == -1 && i == 0) { // an immediate -1 returns -1
                    return -1;
                } else if (x == -1) { // a later -1 returns the chars read so far
                    break;
                }
                buf[off + i] = (byte) x;
            }
            return i;
        }

        public static String decode(String encoded) {
            if (TextUtils.isEmpty(encoded)) {
                return "";
            }
            return new String(decodeToBytes(encoded));
        }

        public static byte[] decodeToBytes(String encoded) {
            byte[] bytes = encoded.getBytes();
            Base64Decoder in = new Base64Decoder(new ByteArrayInputStream(bytes));
            ByteArrayOutputStream out = new ByteArrayOutputStream((int) (bytes.length * 0.75));
            try {
                byte[] buf = new byte[4 * 1024]; // 4K buffer
                int bytesRead;
                while ((bytesRead = in.read(buf)) != -1) {
                    out.write(buf, 0, bytesRead);
                }
                return out.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static class Base64Encoder extends FilterOutputStream {

        private static final char[] chars = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
                'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

        private int charCount;
        private int carryOver;
        private boolean isWrapBreak = true;

        private Base64Encoder(OutputStream out) {
            super(out);
        }

        private Base64Encoder(OutputStream out, boolean isWrapBreak) {
            this(out);
            this.isWrapBreak = isWrapBreak;
        }

        public void write(int b) throws IOException {
            // Take 24-bits from three octets, translate into four encoded chars
            // Break lines at 76 chars
            // If necessary, pad with 0 bits on the right at the end
            // Use = signs as padding at the end to ensure encodedLength % 4 == 0

            // Remove the sign bit,
            // thanks to Christian Schweingruber <chrigu@lorraine.ch>
            if (b < 0) {
                b += 256;
            }

            // First byte use first six bits, save last two bits
            if (charCount % 3 == 0) {
                int lookup = b >> 2;
                carryOver = b & 3; // last two bits
                out.write(chars[lookup]);
            }
            // Second byte use previous two bits and first four new bits,
            // save last four bits
            else if (charCount % 3 == 1) {
                int lookup = ((carryOver << 4) + (b >> 4)) & 63;
                carryOver = b & 15; // last four bits
                out.write(chars[lookup]);
            }
            // Third byte use previous four bits and first two new bits,
            // then use last six new bits
            else if (charCount % 3 == 2) {
                int lookup = ((carryOver << 2) + (b >> 6)) & 63;
                out.write(chars[lookup]);
                lookup = b & 63; // last six bits
                out.write(chars[lookup]);
                carryOver = 0;
            }
            charCount++;

            // Add newline every 76 output chars (that's 57 input chars)
            if (this.isWrapBreak && charCount % 57 == 0) {
                out.write('\n');
            }
        }

        public void write(byte[] buf, int off, int len) throws IOException {
            // This could of course be optimized
            for (int i = 0; i < len; i++) {
                write(buf[off + i]);
            }
        }


        public void close() throws IOException {
            // Handle leftover bytes
            if (charCount % 3 == 1) { // one leftover
                int lookup = (carryOver << 4) & 63;
                out.write(chars[lookup]);
                out.write('=');
                out.write('=');
            } else if (charCount % 3 == 2) { // two leftovers
                int lookup = (carryOver << 2) & 63;
                out.write(chars[lookup]);
                out.write('=');
            }
            super.close();
        }


        public static String encode(byte[] bytes) {
            return encode(bytes, true);
        }

        public static String encode(byte[] bytes, boolean isWrapBreak) {
            ByteArrayOutputStream out = new ByteArrayOutputStream((int) (bytes.length * 1.4));
            Base64Encoder encodedOut = new Base64Encoder(out, isWrapBreak);
            try {
                encodedOut.write(bytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    encodedOut.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return out.toString();
        }
    }
}
