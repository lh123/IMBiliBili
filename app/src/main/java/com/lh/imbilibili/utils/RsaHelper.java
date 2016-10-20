package com.lh.imbilibili.utils;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;

/**
 * Created by liuhui on 2016/10/8.
 */

public class RsaHelper {

    public static String decodeKey(String key) {
        Pattern pattern = Pattern.compile("-----BEGIN PUBLIC KEY-----([\\s\\S]*)-----END PUBLIC KEY-----");
        Matcher matcher = pattern.matcher(key);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    public static PublicKey getPublicKey(String str) {
        byte[] keyByte = Base64.decode(str, Base64.DEFAULT);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyByte);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encryptData(String str, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            // 编码前设定编码方式及密钥
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            // 传入编码数据并返回编码结果
            return Base64.encodeToString(cipher.doFinal(str.getBytes()), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
