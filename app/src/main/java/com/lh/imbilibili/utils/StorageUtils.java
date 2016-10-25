package com.lh.imbilibili.utils;

import android.os.Environment;

import com.lh.imbilibili.IMBilibiliApplication;

/**
 * Created by liuhui on 2016/7/5.
 */
public class StorageUtils {

    public static String getAppFilePath() {
        if (isSdcardAvalible()) {
            return IMBilibiliApplication.getApplication().getExternalFilesDir(null).getPath();
        } else {
            return IMBilibiliApplication.getApplication().getFileStreamPath(null).getPath();
        }
    }

    public static String getAppCachePath() {
        if (isSdcardAvalible()) {
            return IMBilibiliApplication.getApplication().getExternalCacheDir().getPath();
        } else {
            return IMBilibiliApplication.getApplication().getCacheDir().getPath();
        }
    }

    public static boolean isSdcardAvalible() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
}
