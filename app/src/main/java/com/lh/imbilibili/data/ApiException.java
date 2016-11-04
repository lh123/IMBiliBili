package com.lh.imbilibili.data;

/**
 * Created by liuhui on 2016/10/20.
 */

public class ApiException extends RuntimeException {

    private int code;

    public ApiException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public ApiException(int code) {
        this(code, "unknown");
    }

    public int getCode() {
        return code;
    }
}
