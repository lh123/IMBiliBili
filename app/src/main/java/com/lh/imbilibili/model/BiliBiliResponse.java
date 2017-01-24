package com.lh.imbilibili.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuhui on 2016/7/8.
 */
public class BiliBiliResponse<T> {
    private int code;
    private String message;
    @SerializedName(value = "result",alternate = "data")
    private T result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return result != null && code == 0;
    }
}
