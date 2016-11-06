package com.lh.imbilibili.utils;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;

/**
 * Created by liuhui on 2016/10/8.
 */

public class RsaHelper {

    private static final String RSA = "RSA";// 非对称加密密钥算法
    private static final String ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";//加密填充方式

    public static byte[] getPublicKey(String str) {
        Pattern pattern = Pattern.compile("-----BEGIN PUBLIC KEY-----([\\s\\S]*)-----END PUBLIC KEY-----");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return Base64.decode(matcher.group(1), Base64.DEFAULT);
        }
        return null;
    }

    public static String encryptByPublicKey(byte[] data, byte[] publicKey) throws Exception {
        // 得到公钥
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
        KeyFactory kf = KeyFactory.getInstance(RSA);
        PublicKey keyPublic = kf.generatePublic(keySpec);
        // 加密数据
        Cipher cp = Cipher.getInstance(ECB_PKCS1_PADDING);
        cp.init(Cipher.ENCRYPT_MODE, keyPublic);
        return Base64.encodeToString(cp.doFinal(data), Base64.DEFAULT);
    }
}
