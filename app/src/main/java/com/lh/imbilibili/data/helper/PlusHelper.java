package com.lh.imbilibili.data.helper;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.lh.imbilibili.data.api.PlusService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by liuhui on 2016/12/18.
 */

public class PlusHelper extends BaseHelper {

    private static final String PLUS_URL = "https://www.biliplus.com";

    private PlusService mPlusService;

    private static PlusHelper mHelper;
    private OkHttpClient mClient;

    public static PlusHelper getInstance() {
        if (mHelper == null) {
            mHelper = new PlusHelper();
        }
        return mHelper;
    }

    private PlusHelper() {
        initPlusService();
    }

    private void initPlusService() {
        OkHttpClient client = new OkHttpClient.Builder()
                .writeTimeout(15000, TimeUnit.MILLISECONDS)
                .readTimeout(15000, TimeUnit.MILLISECONDS)
                .connectTimeout(15000, TimeUnit.MILLISECONDS)
                .addInterceptor(mLog)
                .addInterceptor(new StethoInterceptor())
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(PLUS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
        mPlusService = retrofit.create(PlusService.class);
    }

    public PlusService getPlusService() {
        return mPlusService;
    }
}
