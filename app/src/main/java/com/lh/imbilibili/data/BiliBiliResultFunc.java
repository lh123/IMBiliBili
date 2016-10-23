package com.lh.imbilibili.data;

import com.lh.imbilibili.model.BiliBiliResultResponse;

import rx.functions.Func1;

/**
 * Created by liuhui on 2016/10/20.
 */

public class BiliBiliResultFunc<T> implements Func1<BiliBiliResultResponse<T>, T> {
    @Override
    public T call(BiliBiliResultResponse<T> tBiliBiliResultResponse) {
        if (!tBiliBiliResultResponse.isSuccess()) {
            throw new ApiException(tBiliBiliResultResponse.getCode());
        }
        return tBiliBiliResultResponse.getResult();
    }
}
