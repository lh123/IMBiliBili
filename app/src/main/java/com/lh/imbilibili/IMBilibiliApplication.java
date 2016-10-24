package com.lh.imbilibili;

import android.app.Application;
import android.os.Handler;

import com.facebook.stetho.Stetho;
import com.lh.imbilibili.utils.UserManagerUtils;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by liuhui on 2016/7/5.
 */
public class IMBilibiliApplication extends Application {

    private static IMBilibiliApplication application;

    private Handler mHandler;

    public static IMBilibiliApplication getApplication() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        Stetho.initializeWithDefaults(this);
        mHandler = new Handler();
        UserManagerUtils.getInstance().readUserInfo(this);
    }

    public Handler getHandler() {
        return mHandler;
    }
}
