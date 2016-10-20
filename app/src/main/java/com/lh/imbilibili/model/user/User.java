package com.lh.imbilibili.model.user;

/**
 * Created by liuhui on 2016/10/8.
 */

public class User {
    private int mid;
    private String accessKey;
    private long expires;

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }
}
