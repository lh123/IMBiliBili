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
 * Created by liuhui on 2016/11/5.
 */

public class VideoInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        HttpUrl.Builder builder = chain.request().url().newBuilder();
        Map<String, String> map = new HashMap<>();
        Set<String> params = oldRequest.url().queryParameterNames();
        for (String s : params) {
            map.put(s, oldRequest.url().queryParameter(s));
            builder.removeAllQueryParameters(s);
        }
        User user = UserManagerUtils.getInstance().getCurrentUser();
        if (user != null && !TextUtils.isEmpty(user.getAccessToken())) {
            map.put(Constant.QUERY_ACCESS_KEY, user.getAccessToken());
            map.put("mid", user.getMid() + "");
        }
        map.put(Constant.QUERY_PLATFORM, Constant.PLATFORM);
        map.put("_appver", Constant.BUILD);
        map.put(Constant.QUERY_BUILD, Constant.BUILD);
        map.put("_device", Constant.PLATFORM);
        map.put(Constant.QUERY_APP_KEY, Constant.APPKEY);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        Request.Builder requestBuilder = chain.request().newBuilder().url(builder.build());
        requestBuilder.removeHeader("User-Agent");
        requestBuilder.addHeader("User-Agent", Constant.UA);
        return chain.proceed(requestBuilder.build());
    }
}
