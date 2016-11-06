package com.lh.imbilibili.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by liuhui on 2016/11/5.
 */

public class LoginInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        HttpUrl.Builder builder = chain.request().url().newBuilder();
        HashMap<String, String> map = new HashMap<>();
        Set<String> params = oldRequest.url().queryParameterNames();
        for (String s : params) {
            map.put(s, oldRequest.url().queryParameter(s));
            builder.removeAllQueryParameters(s);
        }
        if (oldRequest.url().toString().contains("getKey")) {
            builder.addQueryParameter(Constant.QUERY_APP_KEY, Constant.APPKEY);
            builder.addQueryParameter(Constant.QUERY_BUILD, Constant.BUILD);
            builder.addQueryParameter(Constant.QUERY_MOBI_APP, Constant.MOBI_APP);
            builder.addQueryParameter(Constant.QUERY_PLATFORM, Constant.PLATFORM);
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
