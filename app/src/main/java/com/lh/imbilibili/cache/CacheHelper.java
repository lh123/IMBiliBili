package com.lh.imbilibili.cache;

import com.bumptech.glide.disklrucache.DiskLruCache;
import com.google.gson.Gson;
import com.lh.imbilibili.utils.NetworkUtils;
import com.lh.imbilibili.utils.StorageUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by liuhui on 2016/10/25.
 * Cache工具类
 */

public class CacheHelper {

    private static final String CACHE_PATH = "/http";
    private static final int CACHE_SIZE = 10 * 1024 * 1024;

    private static DiskLruCache mDiskCache;

    static {
        File file = new File(StorageUtils.getAppCachePath() + CACHE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            mDiskCache = DiskLruCache.open(file, 2, 2, CACHE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> Observable<T> wrapObservable(final Observable<T> origin, final Type type, final String cacheKey, final int expireTime, final boolean refresh) {
        Observable<T> fromCache = Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    DiskLruCache.Value value = mDiskCache.get(cacheKey);
                    if (value != null) {
                        String data = value.getString(0);
                        long time = Long.parseLong(value.getString(1));
                        if (System.currentTimeMillis() - time < expireTime || !NetworkUtils.isNetworkConnected()) {
                            T cache = new Gson().fromJson(data, type);
                            System.out.println("readFromCache");
                            System.out.println("data:" + data);
                            System.out.println("time:" + time);
                            subscriber.onNext(cache);
                        }
                    }
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
        Observable<T> fromNetwork = origin.map(new Func1<T, T>() {
            @Override
            public T call(T t) {
                try {
                    System.out.println("readFromNetwork");
                    System.out.println("writeToCache");
                    DiskLruCache.Editor edit = mDiskCache.edit(cacheKey);
                    edit.set(0, new Gson().toJson(t));
                    edit.set(1, String.valueOf(System.currentTimeMillis()));
                    edit.commit();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return t;
            }
        });
        if (NetworkUtils.isNetworkConnected()) {
            if (refresh) {
                return fromNetwork;
            } else {
                return Observable.concatDelayError(fromCache, fromNetwork).takeFirst(new Func1<T, Boolean>() {
                    @Override
                    public Boolean call(T t) {
                        return t != null;
                    }
                });
            }
        } else {
            return fromCache;
        }
    }
}
