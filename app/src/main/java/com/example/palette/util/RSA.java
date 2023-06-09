package com.example.palette.util;

import android.util.Base64;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSA {

	public static final String KEY_ALGORITHM = "RSA";

	/**
	 * 用公钥解密
	 *
	 * @param data
	 * @param key
	 * @return
	 */
	public static String decryptByPublicKey(byte[] data, String key) {
		try {
			// 对密钥解密
			byte[] keyBytes = Base64.decode(key,Base64.NO_WRAP);

			// 取得公钥
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			Key publicKey = keyFactory.generatePublic(x509KeySpec);

			// 对数据解密
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, publicKey);

			return new String(cipher.doFinal(data));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 用私钥解密
	 *
	 * @param key
	 * @return
	 */
	public static String decryptByPrivateKey(String s, String key) {
		try {
			// 取得私钥
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decode(key,Base64.NO_WRAP));
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
			// 对数据解密
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return new String(cipher.doFinal(Base64.decode(s.getBytes("UTF-8"),Base64.NO_WRAP)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 用公钥加密
	 *
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encryptByPublicKey(String s, String key) {
		try {
			// 取得公钥
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decode(key,Base64.NO_WRAP));
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			Key publicKey = keyFactory.generatePublic(x509KeySpec);
			// 对数据加密
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			return Base64.encodeToString(cipher.doFinal(s.getBytes("UTF-8")),Base64.NO_WRAP);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 用私钥加密
	 *
	 * @param data
	 * @param key
	 * @return
	 */
	public static String encryptByPrivateKey(byte[] data, String key) {
		try {
			byte[] keyBytes = Base64.decode(key,Base64.NO_WRAP);
			// 取得私钥
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

			// 对数据加密
			Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);

			return Base64.encodeToString(cipher.doFinal(data),Base64.NO_WRAP);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void genKeyPair() throws NoSuchAlgorithmException {
		// KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		// 初始化密钥对生成器，密钥大小为96-1024位
		keyPairGen.initialize(1024,new SecureRandom());
		// 生成一个密钥对，保存在keyPair中
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
		String publicKeyString = new String(Base64.encode(publicKey.getEncoded(),Base64.NO_WRAP));
		// 得到私钥字符串
		String privateKeyString = new String(Base64.encode((privateKey.getEncoded()),Base64.NO_WRAP));
		// 将公钥和私钥保存到Map
//		System.out.println("public:"+publicKeyString);
//		System.out.println("private:"+privateKeyString);
	}
}
