package com.lh.imbilibili.data;

import com.lh.imbilibili.utils.BiliBilliSignUtils;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by liuhui on 2016/7/8.
 */
public class BiliSignInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request finalRequest = null;
        Request oldlRequest = chain.request();
        HttpUrl.Builder urlBuilder = chain.request().url().newBuilder();
        String sign;
        switch (oldlRequest.url().host()) {
            case "interface.bilibili.com":
                sign = BiliBilliSignUtils.getSign(oldlRequest.url().query(), Constant.PLAYER_SECRETKEY);
                break;
            case "account.bilibili.com":
                sign = BiliBilliSignUtils.getSign(oldlRequest.url().query(), Constant.LOGIN_SECRETKEY);
                break;
            default:
                sign = BiliBilliSignUtils.getSign(oldlRequest.url().query(), Constant.SECRETKEY);
                break;
        }
        urlBuilder.addQueryParameter(Constant.QUERY_SIGN, sign);
        finalRequest = oldlRequest.newBuilder().url(urlBuilder.build()).build();
        return chain.proceed(finalRequest);
    }
}
