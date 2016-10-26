package com.lh.imbilibili.cache;

import android.util.Log;

import com.bumptech.glide.disklrucache.DiskLruCache;
import com.google.gson.Gson;
import com.lh.imbilibili.utils.NetworkUtils;

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

    private static final String TAG = "CacheHepler";

    public static <T> Observable<T> wrapObservable(final Observable<T> origin, final Type type, final String cacheKey, final int expireTime, final boolean refresh) {
        Observable<T> fromCache = Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    DiskLruCache.Value value = DiskCacheProvider.getInstance().getCache().get(cacheKey);
                    if (value != null) {
                        String data = value.getString(0);
                        long time = Long.parseLong(value.getString(1));
                        if (System.currentTimeMillis() - time < expireTime || !NetworkUtils.isNetworkConnected()) {
                            T cache = new Gson().fromJson(data, type);
                            Log.d(TAG, "readFromCache\nkey:"
                                    + cacheKey + "\ntime:"
                                    + time + "\nexpireTime:"
                                    + expireTime);
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
                    Log.d(TAG, "readFromNetwork\n"
                            + "writeToCache\nkey:"
                            + cacheKey);
                    DiskLruCache.Editor edit = DiskCacheProvider.getInstance().getCache().edit(cacheKey);
                    edit.set(0, new Gson().toJson(t));
                    edit.set(1, String.valueOf(System.currentTimeMillis()));
                    edit.commit();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return t;
            }
        });
        if (refresh) {
            return fromNetwork;
        } else {
            return Observable.concat(fromCache, fromNetwork).takeFirst(new Func1<T, Boolean>() {
                @Override
                public Boolean call(T t) {
                    return t != null;
                }
            });
        }
    }
}
