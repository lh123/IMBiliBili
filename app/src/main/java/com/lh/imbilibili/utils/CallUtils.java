package com.lh.imbilibili.utils;

import retrofit2.Call;

/**
 * Created by home on 2016/8/1.
 */
public class CallUtils {
    public static void cancelCall(Call... calls) {
        for (Call call : calls) {
            if (call != null && !call.isCanceled()) {
                call.cancel();
            }
        }
    }
}
