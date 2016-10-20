package com.lh.imbilibili.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * Created by home on 2016/8/7.
 */
public class ToastUtils {
    private static WeakReference<Toast> toast;

    public static void showToast(Context context, String msg, int duration) {
        if (toast == null || toast.get() == null) {
            toast = new WeakReference<>(Toast.makeText(context, msg, duration));
        } else {
            toast.get().setText(msg);
            toast.get().setDuration(duration);
        }
        toast.get().show();
    }

    public static void showToast(Context context, @StringRes int resId, int duration) {
        if (toast == null || toast.get() == null) {
            toast = new WeakReference<>(Toast.makeText(context, resId, duration));
        } else {
            toast.get().setText(resId);
            toast.get().setDuration(duration);
        }
        toast.get().show();
    }
}
