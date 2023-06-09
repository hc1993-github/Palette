package com.example.palette.encrypt;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

public class GMBaseUtil {
    static {
//        Security.addProvider(new BouncyCastleProvider());
        Security.addProvider(new BouncyCastleProvider());
//        try {
//            Cipher.getInstance(ALGORITHM_NAME);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        }

    }
}
