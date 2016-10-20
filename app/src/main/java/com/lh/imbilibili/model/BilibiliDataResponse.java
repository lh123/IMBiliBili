package com.lh.imbilibili.model;

/**
 * Created by liuhui on 2016/7/5.
 */
public class BilibiliDataResponse<T> {
    private int code;
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return code == 0 && data != null;
    }
}
