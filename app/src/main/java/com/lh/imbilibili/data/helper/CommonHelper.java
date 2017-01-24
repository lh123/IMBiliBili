package com.lh.imbilibili.data.helper;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.data.api.AttentionService;
import com.lh.imbilibili.data.api.BangumiService;
import com.lh.imbilibili.data.api.HistoryService;
import com.lh.imbilibili.data.api.PartionService;
import com.lh.imbilibili.data.api.ReplyService;
import com.lh.imbilibili.data.api.SearchService;
import com.lh.imbilibili.data.api.SplashService;
import com.lh.imbilibili.data.api.UserService;
import com.lh.imbilibili.data.api.VideoService;
import com.lh.imbilibili.data.interceptor.BilliInterceptor;
import com.lh.imbilibili.utils.StorageUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by liuhui on 2016/10/8.
 */

public class CommonHelper extends BaseHelper {

    private static CommonHelper mCommonHelper;

    private Retrofit mRetrofit;

    private BangumiService mBangumiService;
    private PartionService mPartionService;
    private ReplyService mReplyService;
    private SearchService mSearchService;
    private SplashService mSplashService;
    private VideoService mVideoService;
    private UserService mUserService;
    private HistoryService mHistoryService;
    private AttentionService mAttentionService;

    private CommonHelper() {
        String path = StorageUtils.getAppCachePath();
        Cache cache = new Cache(new File(path, "okhttp"), 1024 * 1024 * 100);
        OkHttpClient client = new OkHttpClient.Builder().writeTimeout(3000, TimeUnit.MILLISECONDS)
                .readTimeout(3000, TimeUnit.MILLISECONDS)
                .connectTimeout(3000, TimeUnit.MILLISECONDS)
                .addInterceptor(new BilliInterceptor())
                .addInterceptor(mSign)
                .addInterceptor(mLog)
                .addInterceptor(mStetho)
                .cache(cache)
                .build();
        mRetrofit = new Retrofit.Builder().baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
        initService();
    }

    private void initService() {
        mBangumiService = mRetrofit.create(BangumiService.class);
        mPartionService = mRetrofit.create(PartionService.class);
        mReplyService = mRetrofit.create(ReplyService.class);
        mSearchService = mRetrofit.create(SearchService.class);
        mSplashService = mRetrofit.create(SplashService.class);
        mVideoService = mRetrofit.create(VideoService.class);
        mUserService = mRetrofit.create(UserService.class);
        mHistoryService = mRetrofit.create(HistoryService.class);
        mAttentionService = mRetrofit.create(AttentionService.class);
    }

    public static CommonHelper getInstance() {
        if (mCommonHelper == null) {
            synchronized (CommonHelper.class) {
                if (mCommonHelper == null) {
                    mCommonHelper = new CommonHelper();
                }
            }
        }
        return mCommonHelper;
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }


    public BangumiService getBangumiService() {
        return mBangumiService;
    }

    public PartionService getPartionService() {
        return mPartionService;
    }

    public ReplyService getReplyService() {
        return mReplyService;
    }

    public SearchService getSearchService() {
        return mSearchService;
    }

    public SplashService getSplashService() {
        return mSplashService;
    }

    public VideoService getVideoService() {
        return mVideoService;
    }

    public UserService getUserService() {
        return mUserService;
    }

    public HistoryService getHistoryService() {
        return mHistoryService;
    }

    public AttentionService getAttentionService() {
        return mAttentionService;
    }
}
