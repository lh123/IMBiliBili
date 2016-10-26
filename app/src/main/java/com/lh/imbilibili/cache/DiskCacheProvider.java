package com.lh.imbilibili.cache;

import com.bumptech.glide.disklrucache.DiskLruCache;
import com.lh.imbilibili.utils.StorageUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by liuhui on 2016/10/26.
 * 缓存提供工具
 */

public class DiskCacheProvider {

    private static final String CACHE_PATH = "/http_cache";
    private static final int CACHE_SIZE = 10 * 1024 * 1024;

    private static DiskCacheProvider mProvider;

    private DiskLruCache mDiskCache;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private DiskCacheProvider() {
        File file = new File(StorageUtils.getAppCachePath() + CACHE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            mDiskCache = DiskLruCache.open(file, 1, 2, CACHE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DiskCacheProvider getInstance() {
        if (mProvider == null) {
            synchronized (DiskCacheProvider.class) {
                if (mProvider == null) {
                    mProvider = new DiskCacheProvider();
                }
            }
        }
        return mProvider;
    }

    public DiskLruCache getCache() {
        return mDiskCache;
    }
}
