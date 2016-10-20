package com.lh.imbilibili.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by liuhui on 2016/7/8.
 */
public class BiliBilliSignUtils {
    //    platform=android&_appver=421000&_device=android&_aid=6117792&_tid=0&_p=8&_down=0&cid=9932979&quality=2&otype=json&appkey=6f90a59ac58a4123&type=mp4
    public static String getSign(String params, String secret) {
        try {
            params = sortQueryParams(params);
            params += secret;
            MessageDigest md = MessageDigest.getInstance("md5");
            StringBuilder sb = new StringBuilder();
            byte[] temp = md.digest(params.getBytes());
            for (byte aTemp : temp) {
                String s = Integer.toHexString(aTemp & 0xff);
                if (s.length() < 2)
                    sb.append(0);
                sb.append(s);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String sortQueryParams(String params) {
        if (params != null) {
            List<String> paramsList = Arrays.asList(params.split("&"));
            Collections.sort(paramsList);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < paramsList.size(); i++) {
                stringBuilder.append(paramsList.get(i));
                if (i != paramsList.size() - 1) {
                    stringBuilder.append("&");
                }
            }
            return stringBuilder.toString();
        }
        return null;
    }
}
