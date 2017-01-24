package com.lh.imbilibili.data.helper;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.data.api.LoginService;
import com.lh.imbilibili.utils.BiliBilliSignUtils;

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
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by liuhui on 2016/11/17.
 * login Retrofit
 */

public class LoginHelper extends BaseHelper {

    private static final String BASE_URL = "https://passport.bilibili.com";

    private static LoginHelper mHelper;

    private LoginService mService;

    public static LoginHelper getInstance() {
        if (mHelper == null) {
            mHelper = new LoginHelper();
        }
        return mHelper;
    }

    private LoginHelper() {
        OkHttpClient client = new OkHttpClient.Builder().writeTimeout(3000, TimeUnit.MILLISECONDS)
                .readTimeout(3000, TimeUnit.MILLISECONDS)
                .connectTimeout(3000, TimeUnit.MILLISECONDS)
                .addInterceptor(new LoginInterceptor())
                .addInterceptor(mLog)
                .addInterceptor(new StethoInterceptor())
                .build();
        mService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()
                .create(LoginService.class);

    }

    public LoginService getLoginService() {
        return mService;
    }

    private static class LoginInterceptor implements Interceptor {

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
            if (oldRequest.url().encodedPath().equals(Constant.GET_KEY)) {
                map.put(Constant.QUERY_APP_KEY, Constant.APPKEY);
                map.put(Constant.QUERY_BUILD, Constant.BUILD);
                map.put(Constant.QUERY_MOBI_APP, Constant.MOBI_APP);
                map.put(Constant.QUERY_PLATFORM, Constant.PLATFORM);
                String sign = BiliBilliSignUtils.getSign(map, Constant.SECRETKEY);
                map.put(Constant.QUERY_SIGN, sign);
            }
            for (Map.Entry<String, String> entry : map.entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());
            }

            Request.Builder requestBuilder = chain.request().newBuilder().url(builder.build());
            requestBuilder.removeHeader("User-Agent");
            requestBuilder.addHeader("User-Agent", Constant.UA);
            return chain.proceed(requestBuilder.build());
        }
    }
}
