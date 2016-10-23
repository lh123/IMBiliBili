package com.lh.imbilibili.data;

import com.lh.imbilibili.model.BilibiliDataResponse;

import rx.functions.Func1;

/**
 * Created by liuhui on 2016/10/20.
 */

public class BiliBiliDataFunc<T> implements Func1<BilibiliDataResponse<T>, T> {
    @Override
    public T call(BilibiliDataResponse<T> tBilibiliDataResponse) {
        if (!tBilibiliDataResponse.isSuccess()) {
            throw new ApiException(tBilibiliDataResponse.getCode());
        }
        return tBilibiliDataResponse.getData();
    }
}
