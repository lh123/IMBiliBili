package com.lh.imbilibili.cache;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import rx.Observable;

/**
 * Created by liuhui on 2016/10/25.
 * Cache CacheTransformer
 */

public abstract class CacheTransformer<T> implements Observable.Transformer<T, T> {

    private static final int EXPIRE_TIME = 5 * 60 * 1000;

    private String mCacheKey;
    private int mExpireTime;
    private boolean mForeRefresh;

    public CacheTransformer(String cacheKey, int expireTime, boolean foreRefresh) {
        mCacheKey = cacheKey;
        mExpireTime = expireTime;
        mForeRefresh = foreRefresh;
    }

    public CacheTransformer(String cacheKey, boolean foreRefresh) {
        this(cacheKey, EXPIRE_TIME, foreRefresh);
    }

    public CacheTransformer(String cacheKey) {
        this(cacheKey, EXPIRE_TIME, false);
    }

    @Override
    public Observable<T> call(Observable<T> observable) {
        if (canCache()) {
            return CacheHelper.wrapObservable(observable, getType(), mCacheKey, mExpireTime, mForeRefresh);
        } else {
            return observable;
        }
    }

    private Type getType() {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            return String.class;
        }
    }

    protected boolean canCache() {
        return true;
    }
}
