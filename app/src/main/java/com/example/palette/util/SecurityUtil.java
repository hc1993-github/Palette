package com.example.palette.util;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SecurityUtil {
    private static final char[] HEX_DIGITS_UPPER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final char[] HEX_DIGITS_LOWER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static final int SM4_ENCRYPT = 1;
    private static final int SM4_DECRYPT = 0;

    /**
     * 获取字符串md5值
     * @param string
     * @return
     */
    public static String md5FromString(String string){
        return md5(string.getBytes());
    }

    /**
     * 获取文件md5值
     * @param file
     * @return
     */
    public static String md5FromFile(File file){
        return bytes2HexString(encryptMD5File(file),true);
    }

    /**
     * RSA公钥解密字符串
     * @param string
     * @param key
     * @return
     */
    public static String rsaDecryptStringByPublicKey(String string,String key){
        try {
            byte[] stringbytes = Base64.decode(string, Base64.NO_WRAP);
            byte[] keybytes = Base64.decode(key, Base64.NO_WRAP);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keybytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            Key publicKey = keyFactory.generatePublic(x509KeySpec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(cipher.doFinal(stringbytes));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * RSA私钥解密字符串
     * @param string
     * @param key
     * @return
     */
    public static String rsaDecryptStringByPrivateKey(String string,String key){
        try {
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decode(key,Base64.NO_WRAP));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(Base64.decode(string.getBytes("UTF-8"),Base64.NO_WRAP)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * RSA公钥加密字符串
     * @param string
     * @param key
     * @return
     */
    public static String rsaEncryptStringByPublicKey(String string, String key) {
        try {
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decode(key,Base64.NO_WRAP));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            Key publicKey = keyFactory.generatePublic(x509KeySpec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.encodeToString(cipher.doFinal(string.getBytes("UTF-8")),Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * RSA私钥解密字符串
     * @param string
     * @param key
     * @return
     */
    public static String rsaEncryptStringByPrivateKey(String string, String key) {
        try {
            byte[] keyBytes = Base64.decode(key,Base64.NO_WRAP);
            byte[] stringBytes = string.getBytes();
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return Base64.encodeToString(cipher.doFinal(stringBytes),Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DES解密字符串
     * @param string
     * @param key
     * @return
     */
    public static String desDecryptString(String string,String key){
        if(key==null || key.length()<8){
            return null;
        }
        if(string==null){
            return null;
        }
        try {
            Key secretKey = generateDESKey(key);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec("88886666".getBytes("utf-8"));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            return new String(cipher.doFinal(Base64.decode(string.getBytes("utf-8"), Base64.DEFAULT)), "utf-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DES加密字符串
     * @param string
     * @param key
     * @return
     */
    public static String desEncryptString(String string,String key){
        if(key==null || key.length()<8){
            return null;
        }
        if(string==null){
            return null;
        }
        try {
            Key secretKey = generateDESKey(key);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec("88886666".getBytes("utf-8"));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] bytes = cipher.doFinal(string.getBytes("utf-8"));
            return new String(Base64.encode(bytes, Base64.DEFAULT));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DES加密文件
     * @param srcFile
     * @param destFile
     * @param key
     * @return
     */
    public static String desEncryptFile(String srcFile,String destFile,String key){
        if(key==null || key.length()<8){
            return null;
        }
        try {
            IvParameterSpec iv = new IvParameterSpec("88886666".getBytes("utf-8"));
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, generateDESKey(key), iv);
            InputStream is = new FileInputStream(srcFile);
            OutputStream out = new FileOutputStream(destFile);
            CipherInputStream cis = new CipherInputStream(is, cipher);
            byte[] buffer = new byte[1024];
            int r;
            while ((r = cis.read(buffer)) > 0) {
                out.write(buffer, 0, r);
            }
            cis.close();
            is.close();
            out.close();
            return destFile;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * DES解密文件
     * @param srcFile
     * @param destFile
     * @param key
     * @return
     */
    public static String desDecryptFile(String srcFile,String destFile,String key){
        if(key==null || key.length()<8){
            return null;
        }
        try {
            File file = new File(destFile);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            IvParameterSpec iv = new IvParameterSpec("88886666".getBytes("utf-8"));
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, generateDESKey(key), iv);
            InputStream is = new FileInputStream(srcFile);
            OutputStream out = new FileOutputStream(destFile);
            CipherOutputStream cos = new CipherOutputStream(out, cipher);
            byte[] buffer = new byte[1024];
            int r;
            while ((r = is.read(buffer)) >= 0) {
                cos.write(buffer, 0, r);
            }
            cos.close();
            is.close();
            out.close();
            return destFile;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * AES加密字符串
     * @param string
     * @param key
     * @return
     */
    public static String aesEncryptString(String string,String key){
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, getAESSecretKey(key));
            byte[] encryptByte = cipher.doFinal(string.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(encryptByte,Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * AES解密字符串
     * @param string
     * @param key
     * @return
     */
    public static String aesDecryptString(String string,String key){
        try {
            byte[] data = Base64.decode(string,Base64.NO_WRAP);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, getAESSecretKey(key));
            byte[] result = cipher.doFinal(data);
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * AES加密文件
     * @param srcFile
     * @param destFile
     * @param key
     * @return
     */
    public static String aesEncryptFile(String srcFile,String destFile,String key){
        FileOutputStream outputStream = null;
        try {
            File encryptFile = new File(destFile);
            outputStream = new FileOutputStream(encryptFile);
            SecretKeySpec secretKeySpec = getAESSecretKey(key);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
            CipherInputStream cipherInputStream = new CipherInputStream(new FileInputStream(srcFile), cipher);
            byte[] buffer = new byte[1024 * 2];
            int len;
            while ((len = cipherInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
                outputStream.flush();
            }
            cipherInputStream.close();
            return destFile;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(outputStream!=null){
                    outputStream.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * ASE解密文件
     * @param srcFile
     * @param destFile
     * @param key
     * @return
     */
    public static String aesDecryptFile(String srcFile,String destFile,String key){
        FileInputStream inputStream = null;
        try {
            File decryptFile = new File(destFile);
            SecretKeySpec secretKeySpec = getAESSecretKey(key);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
            inputStream = new FileInputStream(srcFile);
            CipherOutputStream cipherOutputStream = new CipherOutputStream(new FileOutputStream(decryptFile), cipher);
            byte[] buffer = new byte[1024 * 2];
            int len;
            while ((len = inputStream.read(buffer)) >= 0) {
                cipherOutputStream.write(buffer, 0, len);
                cipherOutputStream.flush();
            }
            cipherOutputStream.close();
            return destFile;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(inputStream!=null){
                    inputStream.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    private static SecretKeySpec getAESSecretKey(String secretKey) {
        secretKey = toMakeKey(secretKey, 32, "0");
        return new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
    }

    private static String toMakeKey(String secretKey, int length, String text) {
        // 获取密钥长度
        int strLen = secretKey.length();
        // 判断长度是否小于应有的长度
        if (strLen < length) {
            // 补全位数
            StringBuilder builder = new StringBuilder();
            // 将key添加至builder中
            builder.append(secretKey);
            // 遍历添加默认文本
            for (int i = 0; i < length - strLen; i++) {
                builder.append(text);
            }
            // 赋值
            secretKey = builder.toString();
        }
        return secretKey;
    }

    private static Key generateDESKey(String password) throws Exception {
        DESKeySpec dks = new DESKeySpec(password.getBytes("utf-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        return keyFactory.generateSecret(dks);
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
