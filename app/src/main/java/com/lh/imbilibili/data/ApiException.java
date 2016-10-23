package com.lh.imbilibili.data;

/**
 * Created by liuhui on 2016/10/20.
 */

public class ApiException extends RuntimeException {

    private int code;

    public ApiException(int code) {
        this.code = code;
    }
}
