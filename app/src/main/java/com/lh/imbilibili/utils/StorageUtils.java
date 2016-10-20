package com.lh.imbilibili.utils;

import android.content.Context;

import java.io.File;

/**
 * Created by liuhui on 2016/7/5.
 */
public class StorageUtils {
    public static File getAppFile(Context context, String name) {
        File rootFile = context.getExternalFilesDir(null);
        File file = new File(rootFile, name);
        if (!rootFile.exists()) {
            rootFile.mkdirs();
        }
        return file;
    }

    public static File getAppCache(Context context, String name) {
        File rootFile = context.getExternalCacheDir();
        File file = new File(rootFile, name);
        if (!rootFile.exists()) {
            rootFile.mkdirs();
        }
        return file;
    }
}
