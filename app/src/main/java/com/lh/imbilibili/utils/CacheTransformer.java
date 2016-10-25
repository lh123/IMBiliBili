package com.lh.imbilibili.utils;

import java.lang.reflect.Type;

import rx.Observable;

/**
 * Created by liuhui on 2016/10/25.
 * Cache CacheTransformer
 */

public class CacheTransformer<T> implements Observable.Transformer<T, T> {

    private static final int EXPIRE_TIME = 5 * 60 * 1000;

    private String mCacheKey;
    private int mExpireTime;
    private boolean mForeRefresh;
    private Type mType;

    public CacheTransformer(String cacheKey, Type type, int expireTime, boolean foreRefresh) {
        mCacheKey = cacheKey;
        mType = type;
        mExpireTime = expireTime;
        mForeRefresh = foreRefresh;
    }

    public CacheTransformer(String cacheKey, Type type, boolean foreRefresh) {
        mCacheKey = cacheKey;
        mType = type;
        mExpireTime = EXPIRE_TIME;
        mForeRefresh = foreRefresh;
    }

    public CacheTransformer(String cacheKey, Type type) {
        mCacheKey = cacheKey;
        mType = type;
        mExpireTime = EXPIRE_TIME;
        mForeRefresh = false;
    }

    @Override
    public Observable<T> call(Observable<T> observable) {
        return CacheUtils.wrapObservable(observable, mType, mCacheKey, mExpireTime, mForeRefresh);
    }
}
