package com.lh.imbilibili.data;

import android.text.TextUtils;

import com.lh.imbilibili.model.user.User;
import com.lh.imbilibili.utils.UserManagerUtils;

import java.io.IOException;
import java.util.HashMap;
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
        Request request;
        Request oldRequest = chain.request();
        HttpUrl.Builder builder = chain.request().url().newBuilder();
        HashMap<String, String> map = new HashMap<>();
        Set<String> params = oldRequest.url().queryParameterNames();
        for (String s : params) {
            map.put(s, oldRequest.url().queryParameter(s));
            builder.removeAllQueryParameters(s);
        }
        User user = UserManagerUtils.getInstance().getCurrentUser();
        if (user != null && !TextUtils.isEmpty(user.getAccessKey())) {
            builder.addQueryParameter(Constant.QUERY_ACCESS_KEY, user.getAccessKey());
        }
        switch (oldRequest.url().host()) {
            case "account.bilibili.com":
                builder.addQueryParameter(Constant.QUERY_APP_KEY, Constant.LOGIN_APPKEY);
                break;
            case "interface.bilibili.com":
                builder.addQueryParameter(Constant.QUERY_APP_KEY, Constant.PLAYER_APPKEY);
                break;
            default:
                builder.addQueryParameter(Constant.QUERY_APP_KEY, Constant.APPKEY);
                break;
        }
        builder.addQueryParameter(Constant.QUERY_BUILD, Constant.BUILD);
        builder.addQueryParameter(Constant.QUERY_MOBI_APP, Constant.MOBI_APP);
        builder.addQueryParameter(Constant.QUERY_PLATFORM, Constant.PLATFORM);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        request = chain.request().newBuilder().url(builder.build()).build();
        return chain.proceed(request);
    }
}
