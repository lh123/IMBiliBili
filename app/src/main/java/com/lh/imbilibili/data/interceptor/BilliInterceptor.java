package com.lh.imbilibili.data.interceptor;

import android.text.TextUtils;

import com.lh.imbilibili.data.Constant;
import com.lh.imbilibili.model.user.User;
import com.lh.imbilibili.utils.UserManagerUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by liuhui on 2016/10/8.
 */

public class BilliInterceptor implements Interceptor {

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
        if (user != null && !TextUtils.isEmpty(user.getAccessToken())) {
            builder.addQueryParameter(Constant.QUERY_ACCESS_KEY, user.getAccessToken());
        }
        builder.addQueryParameter(Constant.QUERY_APP_KEY, Constant.APPKEY);
        builder.addQueryParameter(Constant.QUERY_BUILD, Constant.BUILD);
        builder.addQueryParameter(Constant.QUERY_MOBI_APP, Constant.MOBI_APP);
        builder.addQueryParameter(Constant.QUERY_PLATFORM, Constant.PLATFORM);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        Request.Builder requestBuilder = chain.request().newBuilder().url(builder.build());
        requestBuilder.removeHeader("User-Agent");
        requestBuilder.addHeader("User-Agent", Constant.UA);
        return chain.proceed(requestBuilder.build());
    }
}
