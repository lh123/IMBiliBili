package com.lh.imbilibili.utils;

import android.support.annotation.StringRes;
import android.widget.Toast;

import com.lh.imbilibili.IMBilibiliApplication;

import java.lang.ref.WeakReference;

/**
 * Created by home on 2016/8/7.
 * ToastUtils
 */
public class ToastUtils {
    private static WeakReference<Toast> toast;

    public static void showToast(String msg, int duration) {
        if (toast == null || toast.get() == null) {
            toast = new WeakReference<Toast>(Toast.makeText(IMBilibiliApplication.getApplication(), msg, duration));
        } else {
            toast.get().setText(msg);
            toast.get().setDuration(duration);
        }
        toast.get().show();
    }

    public static void showToast(@StringRes int resId, int duration) {
        showToast(IMBilibiliApplication.getApplication().getString(resId), duration);
    }

    public static void showToastShort(String msg) {
        showToast(msg, Toast.LENGTH_SHORT);
    }

    public static void showToastShort(@StringRes int resId) {
        showToast(resId, Toast.LENGTH_SHORT);
    }
}
