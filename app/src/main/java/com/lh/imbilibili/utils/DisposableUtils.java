package com.lh.imbilibili.utils;

import io.reactivex.disposables.Disposable;

/**
 * Created by liuhui on 2016/10/20.
 */

public class DisposableUtils {
    public static void dispose(Disposable... disposables) {
        for (Disposable disposable : disposables) {
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
        }
    }
}
