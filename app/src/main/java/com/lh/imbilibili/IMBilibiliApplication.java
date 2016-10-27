package com.lh.imbilibili;

import android.app.Application;
import android.os.Handler;

import com.facebook.stetho.Stetho;
import com.lh.imbilibili.utils.UserManagerUtils;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by liuhui on 2016/7/5.
 * Application
 */
public class IMBilibiliApplication extends Application {

    private static IMBilibiliApplication application;

    private Handler mHandler;

    private RefWatcher mRefWatcher;

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
        mRefWatcher = LeakCanary.install(this);
        Stetho.initializeWithDefaults(this);
        mHandler = new Handler();
        UserManagerUtils.getInstance().readUserInfo(this);
    }

    public Handler getHandler() {
        return mHandler;
    }

    public RefWatcher getRefWatcher() {
        return mRefWatcher;
    }
}
