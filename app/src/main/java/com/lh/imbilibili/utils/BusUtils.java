package com.lh.imbilibili.utils;

import com.squareup.otto.Bus;

/**
 * Created by liuhui on 2016/10/8.
 */

public class BusUtils {
    private static Bus mBus;

    public static Bus getBus() {
        if (mBus == null) {
            synchronized (BusUtils.class) {
                if (mBus == null) {
                    mBus = new Bus();
                }
            }
        }
        return mBus;
    }
}
