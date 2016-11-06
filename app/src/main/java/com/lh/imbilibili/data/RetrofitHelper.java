package com.lh.imbilibili.data;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.lh.imbilibili.data.api.AttentionService;
import com.lh.imbilibili.data.api.BangumiService;
import com.lh.imbilibili.data.api.HistoryService;
import com.lh.imbilibili.data.api.LoginService;
import com.lh.imbilibili.data.api.PartionService;
import com.lh.imbilibili.data.api.PlusVideoPlayService;
import com.lh.imbilibili.data.api.ReplyService;
import com.lh.imbilibili.data.api.SearchService;
import com.lh.imbilibili.data.api.SplashService;
import com.lh.imbilibili.data.api.UserService;
import com.lh.imbilibili.data.api.VideoPlayService;
import com.lh.imbilibili.data.api.VideoService;
import com.lh.imbilibili.utils.StorageUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by liuhui on 2016/10/8.
 */

public class RetrofitHelper {

    private static RetrofitHelper mRetrofitHelper;
    private HttpLoggingInterceptor mLog;
    private OkHttpClient mCommonClient;
    private OkHttpClient mPlayClient;

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

    private RetrofitHelper() {
        mLog = new HttpLoggingInterceptor();
        mLog.setLevel(HttpLoggingInterceptor.Level.BODY);
        String path = StorageUtils.getAppCachePath();
        Cache cache = new Cache(new File(path, "okhttp"), 1024 * 1024 * 100);
        mCommonClient = new OkHttpClient.Builder().writeTimeout(3000, TimeUnit.MILLISECONDS)
                .readTimeout(3000, TimeUnit.MILLISECONDS)
                .connectTimeout(3000, TimeUnit.MILLISECONDS)
                .addInterceptor(new BilliInterceptor())
                .addInterceptor(new BiliSignInterceptor())
                .addInterceptor(mLog)
                .addInterceptor(new StethoInterceptor())
                .cache(cache)
                .build();
        mPlayClient = new OkHttpClient.Builder().writeTimeout(3000, TimeUnit.MILLISECONDS)
                .readTimeout(3000, TimeUnit.MILLISECONDS)
                .connectTimeout(3000, TimeUnit.MILLISECONDS)
                .addInterceptor(new VideoInterceptor())
                .addInterceptor(new BiliSignInterceptor())
                .addInterceptor(mLog)
                .addInterceptor(new StethoInterceptor())
                .build();
        mRetrofit = new Retrofit.Builder().baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(mCommonClient)
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

    public static RetrofitHelper getInstance() {
        if (mRetrofitHelper == null) {
            synchronized (RetrofitHelper.class) {
                if (mRetrofitHelper == null) {
                    mRetrofitHelper = new RetrofitHelper();
                }
            }
        }
        return mRetrofitHelper;
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }


    public BangumiService getBangumiService() {
        return mBangumiService;
    }

    public LoginService getLoginService() {
        OkHttpClient client = new OkHttpClient.Builder().writeTimeout(3000, TimeUnit.MILLISECONDS)
                .readTimeout(3000, TimeUnit.MILLISECONDS)
                .connectTimeout(3000, TimeUnit.MILLISECONDS)
                .addInterceptor(new LoginInterceptor())
                .addInterceptor(new BiliSignInterceptor())
                .addInterceptor(mLog)
                .addInterceptor(new StethoInterceptor())
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constant.PASSWORD_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();
        return retrofit.create(LoginService.class);
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

    public VideoPlayService getVideoPlayService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constant.INTERFACE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(mPlayClient)
                .build();
        return retrofit.create(VideoPlayService.class);
    }

    public PlusVideoPlayService getPlusVideoPlayService() {
        OkHttpClient client = new OkHttpClient.Builder()
                .writeTimeout(15000, TimeUnit.MILLISECONDS)
                .readTimeout(15000, TimeUnit.MILLISECONDS)
                .connectTimeout(15000, TimeUnit.MILLISECONDS)
                .addInterceptor(mLog)
                .addInterceptor(new StethoInterceptor())
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constant.PASSWORD_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();
        return retrofit.create(PlusVideoPlayService.class);
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
