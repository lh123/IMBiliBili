package com.lh.imbilibili.data.helper;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.lh.imbilibili.data.interceptor.BiliSignInterceptor;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by liuhui on 2016/11/17.
 */

class BaseHelper {

    HttpLoggingInterceptor mLog;
    BiliSignInterceptor mSign;
    StethoInterceptor mStetho;

    BaseHelper() {
        mLog = new HttpLoggingInterceptor();
        mLog.setLevel(HttpLoggingInterceptor.Level.BODY);
        mSign = new BiliSignInterceptor();
        mStetho = new StethoInterceptor();
    }
}
