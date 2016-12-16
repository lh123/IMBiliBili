package com.lh.imbilibili.data.helper;

import android.text.TextUtils;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.data.api.PlusVideoPlayService;
import com.lh.imbilibili.data.api.VideoPlayService;
import com.lh.imbilibili.model.user.User;
import com.lh.imbilibili.utils.BiliBilliSignUtils;
import com.lh.imbilibili.utils.UserManagerUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by liuhui on 2016/11/17.
 */

public class VideoPlayerHelper extends BaseHelper {

    private static final String INTERFACE_URL = "https://interface.bilibili.com";
    private static final String PLUS_URL = "https://www.biliplus.com";

    private VideoPlayService mService;
    private PlusVideoPlayService mPlusService;

    private static VideoPlayerHelper mHelper;

    public static VideoPlayerHelper getInstance() {
        if (mHelper == null) {
            mHelper = new VideoPlayerHelper();
        }
        return mHelper;
    }

    private VideoPlayerHelper() {
        initOfficialService();
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
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();
        mPlusService = retrofit.create(PlusVideoPlayService.class);
    }

    private void initOfficialService() {
        OkHttpClient client = new OkHttpClient.Builder().writeTimeout(3000, TimeUnit.MILLISECONDS)
                .readTimeout(3000, TimeUnit.MILLISECONDS)
                .connectTimeout(3000, TimeUnit.MILLISECONDS)
                .addInterceptor(new VideoInterceptor())
                .addInterceptor(new SignInterceptor())
                .addInterceptor(mLog)
                .addInterceptor(new StethoInterceptor())
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(INTERFACE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();
        mService = retrofit.create(VideoPlayService.class);
    }

    public VideoPlayService getOfficialService() {
        return mService;
    }

    public PlusVideoPlayService getPlusService() {
        return mPlusService;
    }

    private static class VideoInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request oldRequest = chain.request();
            HttpUrl.Builder builder = chain.request().url().newBuilder();
            Map<String, String> map = new LinkedHashMap<>();
            Set<String> params = oldRequest.url().queryParameterNames();
            for (String s : params) {
                map.put(s, oldRequest.url().queryParameter(s));
                builder.removeAllQueryParameters(s);
            }
            User user = UserManagerUtils.getInstance().getCurrentUser();
            if (oldRequest.url().queryParameter("player") == null) {
                if (user != null && !TextUtils.isEmpty(user.getAccessToken())) {
                    map.put(Constant.QUERY_ACCESS_KEY, user.getAccessToken());
                    map.put("mid", user.getMid() + "");
                }
                map.put(Constant.QUERY_PLATFORM, Constant.PLATFORM);
                map.put("_appver", Constant.BUILD);
                map.put(Constant.QUERY_BUILD, Constant.BUILD);
                map.put("_device", Constant.PLATFORM);
                map.put(Constant.QUERY_APP_KEY, Constant.APPKEY);
            }
            for (Map.Entry<String, String> entry : map.entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());
            }
            Request.Builder requestBuilder = chain.request().newBuilder().url(builder.build());
            if (chain.request().header("User-Agent") == null) {
                requestBuilder.addHeader("User-Agent", Constant.UA);
            }
            return chain.proceed(requestBuilder.build());
        }
    }

    private static class SignInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request finalRequest;
            Request oldlRequest = chain.request();
            HttpUrl.Builder urlBuilder = chain.request().url().newBuilder();
            if (oldlRequest.url().queryParameter("player") != null) {
                String sign = BiliBilliSignUtils.getSign(oldlRequest.url().query(), Constant.MINILOAD_SECRETKEY);
                urlBuilder.addQueryParameter(Constant.QUERY_SIGN, sign);
            } else {
                String sign = BiliBilliSignUtils.getSign(oldlRequest.url().query(), Constant.SECRETKEY);
                urlBuilder.addQueryParameter(Constant.QUERY_SIGN, sign);
            }
            finalRequest = oldlRequest.newBuilder().url(urlBuilder.build()).build();
            return chain.proceed(finalRequest);
        }

    }


}
